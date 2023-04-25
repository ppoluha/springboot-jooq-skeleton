package se.hkr.java.db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.hkr.java.db.generated.tables.records.CustomerRecord;
import se.hkr.java.db.repositories.CustomerRepository;

@Controller
public class CustomerController {
    @Autowired
    private CustomerRepository repository;

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", repository.findAll());
        return "customer-list";
    }

    @GetMapping("/customers/new")
    public String showNewCustomerForm(Model model) {
        return "customer-new";
    }

    @PostMapping("/customers")
    public String createCustomer(@ModelAttribute("customer") CustomerRecord customer) {
        repository.save(customer);
        return "redirect:/customers";
    }
}
