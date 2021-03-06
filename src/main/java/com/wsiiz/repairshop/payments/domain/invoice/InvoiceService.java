package com.wsiiz.repairshop.payments.domain.invoice;

import com.wsiiz.repairshop.customers.domain.customer.Customer;
import com.wsiiz.repairshop.customers.domain.customer.CustomerChangedEvent;
import com.wsiiz.repairshop.customers.domain.customer.CustomerRepository;
import com.wsiiz.repairshop.foundation.domain.AbstractService;
import com.wsiiz.repairshop.servicing.domain.service.Service;
import com.wsiiz.repairshop.servicing.domain.service.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceService implements AbstractService<Invoice>,
        ApplicationListener<CustomerChangedEvent> {

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceItemsRepository itemsRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Invoice save(Invoice entity) {
        return invoiceRepository.save(entity);
    }

    @Override
    public void onApplicationEvent(CustomerChangedEvent event) {

        invoiceRepository.findByStatusAndCustomerId(InvoiceStatus.PREPARED, event.getCustomer().getId())
                .ifPresent(invoice -> {
                    String newAddress = buildCustomerAddress(event.getCustomer());
                    if (!invoice.getCustomerAddress().equals(newAddress)) {
                        invoice.setCustomerAddress(newAddress);
                        invoiceRepository.save(invoice);
                    }
                });
    }

    private String buildCustomerAddress(Customer customer) {

        return customer.fullName()
                + (customer.getHomeAddress() == null ? "" :
                (customer.getHomeAddress().getStreet() + "\n"
                        + customer.getHomeAddress().getPostalCode() + " "
                        + customer.getHomeAddress().getLocality()));
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public String getCustomerName(Long id) {
        return customerRepository.findById(id).map(Customer::fullName).orElse("");
    }

    public List<InvoiceItems> getInvoiceItems(Invoice s) {
        return itemsRepository.findAll().stream().filter(f -> f.getInvoice().getId().equals(s.getId())).collect(Collectors.toList());
    }

}
