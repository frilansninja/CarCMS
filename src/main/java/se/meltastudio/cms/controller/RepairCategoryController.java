package se.meltastudio.cms.controller;

import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.model.RepairCategory;
import se.meltastudio.cms.model.WorkOrderCategory;
import se.meltastudio.cms.repository.RepairCategoryRepository;
import se.meltastudio.cms.repository.WorkTaskTemplateRepository;

import java.util.List;

@RestController
@RequestMapping("/api/repaircategories")
public class RepairCategoryController {

    private final RepairCategoryRepository repairCategoryRepository;

    public RepairCategoryController(RepairCategoryRepository repairCategoryRepository) {
        this.repairCategoryRepository = repairCategoryRepository;
    }




    @GetMapping
    public List<RepairCategory> getCategories() {
        return repairCategoryRepository.findAll();
    }

    @PostMapping
    public RepairCategory addCategory(@RequestBody RepairCategory category) {
        return repairCategoryRepository.save(category);
    }

}
