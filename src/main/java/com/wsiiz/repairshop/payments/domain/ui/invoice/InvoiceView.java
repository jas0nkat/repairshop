package com.wsiiz.repairshop.payments.domain.ui.invoice;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.themes.ValoTheme;
import com.wsiiz.repairshop.customers.domain.customer.Customer;
import com.wsiiz.repairshop.customers.domain.customer.Person;
import com.wsiiz.repairshop.customers.domain.customer.PersonRepository;
import com.wsiiz.repairshop.foundation.ui.BaseView;
import com.wsiiz.repairshop.foundation.ui.dialog.ConfirmDialog;
import com.wsiiz.repairshop.payments.domain.invoice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;

@SpringComponent
@UIScope
@SpringView
public class InvoiceView extends BaseView<Invoice> {

    @Autowired
    PersonRepository customerRepo;

    @Autowired
    InvoiceItemsRepository itemsRepository;

    @Autowired
    InvoiceService service;

    public InvoiceView(InvoiceFactory factory, InvoiceService service,
                       InvoiceRepository repository) {
        super(factory, service, repository, new InvoiceEditor(service));
    }

    @Override
    protected void addColumns(MGrid<Invoice> table) {

        table.addColumn(e -> "FV " + e.getId()).setCaption(i18n("invoiceNumber"));
        table.addColumn(e -> customerRepo.findById(e.getCustomerId()).map(Customer::fullName).orElse("")).setCaption(i18n("customer"));
        table.addColumn(e -> e.getCustomerAddress()).setCaption(i18n("address"));
        table.addColumn(e -> e.getInvoiceDate()).setCaption(i18n("date"));
        table.addColumn(e -> e.getStatus()).setCaption(i18n("status"));
    }

    @Override
    protected MGrid<Invoice> createTable() {
        MGrid<Invoice> table = new MGrid<>();

        addColumns(table);

        table.addComponentColumn(entity -> new MHorizontalLayout(

                new MButton(VaadinIcons.TASKS, e -> {
                    new InvoiceItemsUI(entity, getUI(), service, itemsRepository);
                }).withStyleName(ValoTheme.BUTTON_BORDERLESS).withStyleName("no-padding"),

                new MButton(VaadinIcons.EDIT, e -> {
                    editInPopup(entity);
                }).withStyleName(ValoTheme.BUTTON_BORDERLESS).withStyleName("no-padding"),

                new MButton(VaadinIcons.TRASH, e -> {
                    new ConfirmDialog(i18n("deleteConfirmation"), () -> {
                        repository.delete(entity);
                        loadEntities();
                    }, getUI());
                }).withStyleName(ValoTheme.BUTTON_BORDERLESS).withStyleName("no-padding")
        ))
                .setCaption(i18n(BaseView.class, "actions"))
                .setWidth(120);

        table.setHeightFull();
        table.setWidthFull();

        return table;
    }

}
