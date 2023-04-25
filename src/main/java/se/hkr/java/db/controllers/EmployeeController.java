package se.hkr.java.db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import se.hkr.java.db.generated.tables.records.EmployeeRecord;
import se.hkr.java.db.repositories.EmployeeRepository;
import se.hkr.java.db.repositories.OrderRepository;

@Controller
@RequestMapping("/")
public class EmployeeController {
    @Autowired
    private EmployeeRepository repository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/employees")
    public String listStaff(Model model) {
        model.addAttribute("employees", repository.findAll());
        return "employee-list";
    }

    @GetMapping("/employees/new")
    public String showNewEmployeeForm() {
        return "employee-new";
    }

    @PostMapping("/employees")
    public String createEmployee(@ModelAttribute("employee") EmployeeRecord employee) {
        repository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/orders")
    public String showEmployeeOrders(@PathVariable Long id, Model model) {
        EmployeeRecord e = repository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid employee id!"));
        model.addAttribute("orders", orderRepository.findByEmployeeIdOrderByOrderDateDesc(id));
        model.addAttribute("employee", e);
        return "employee-orders";
    }
}
