package se.hkr.java.db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.hkr.java.db.generated.tables.records.OrderHeadRecord;
import se.hkr.java.db.generated.tables.records.OrderLineRecord;
import se.hkr.java.db.repositories.CustomerRepository;
import se.hkr.java.db.repositories.FurnitureRepository;
import se.hkr.java.db.repositories.OrderRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FurnitureRepository furnitureRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/new")
    public String showOrderForm(Model model, @RequestParam("employeeId") Long employeeId) {
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("furnitures", furnitureRepository.findAll());
        model.addAttribute("customers", customerRepository.findAll());
        return "order-new";
    }

    @PostMapping()
    public String createOrder(@RequestParam Long employeeId,
                              @RequestParam LocalDate orderDate,
                              @RequestParam Long customerId,
                              @RequestParam(name = "furniture") List<Long> furnitureIds,
                              @RequestParam(name = "quantity") List<Integer> quantities) {

        // create order head
        var orderHead = new OrderHeadRecord();
        orderHead.setEmployeeId(employeeId);
        orderHead.setCustomerId(customerId);
        orderHead.setOrderDate(orderDate);

        // create order lines
        List<OrderLineRecord> orderLines = new ArrayList<>();
        for (int i = 0; i < furnitureIds.size(); i++) {
            Long furnitureId = furnitureIds.get(i);
            if (furnitureId == null) continue;
            var orderLine = new OrderLineRecord();
            orderLine.setFurnitureId(furnitureId);
            orderLine.setQuantity(quantities.get(i));
            orderLines.add(orderLine);
        }

        // save order
        orderRepository.save(orderHead, orderLines);
        return "redirect:/employees";
    }
}
