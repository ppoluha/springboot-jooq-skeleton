package se.hkr.java.db.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.hkr.java.db.generated.tables.records.FurnitureRecord;
import se.hkr.java.db.repositories.FurnitureRepository;

@Controller
public class FurnitureController {

    @Autowired
    private FurnitureRepository repository;

    @GetMapping("/furnitures")
    public String showFurnitureList(Model model) {
        model.addAttribute("furnitures", repository.findAll());
        return "furniture-list";
    }

    @GetMapping("/furnitures/new")
    public String showNewFurnitureForm(Model model) {
        return "furniture-new";
    }

    @PostMapping("/furnitures")
    public String createFurniture(@ModelAttribute("furniture") FurnitureRecord furniture) {
        repository.save(furniture);
        return "redirect:/furnitures";
    }
}
