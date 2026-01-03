package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.model.WorkOrderCategory;
import se.meltastudio.cms.model.WorkTaskTemplate;
import se.meltastudio.cms.repository.WorkOrderCategoryRepository;
import se.meltastudio.cms.repository.WorkTaskTemplateRepository;

import java.util.List;

@RestController
@RequestMapping("/api/workordercategories")
public class WorkOrderCategoryController {

    private final WorkOrderCategoryRepository workOrderCategoryRepository;
    private final WorkTaskTemplateRepository workTaskTemplateRepository;


    public WorkOrderCategoryController(WorkOrderCategoryRepository workOrderCategoryRepository, WorkTaskTemplateRepository workTaskTemplateRepository) {
        this.workOrderCategoryRepository = workOrderCategoryRepository;
        this.workTaskTemplateRepository = workTaskTemplateRepository;
    }




    @GetMapping
    public List<WorkOrderCategory> getCategories() {
        return workOrderCategoryRepository.findAll();
    }

    @PostMapping
    public WorkOrderCategory addCategory(@RequestBody WorkOrderCategory category) {
        return workOrderCategoryRepository.save(category);
    }

}
