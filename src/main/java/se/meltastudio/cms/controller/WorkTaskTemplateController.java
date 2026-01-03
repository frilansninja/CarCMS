package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.RequiredPartDTO;
import se.meltastudio.cms.dto.WorkTaskTemplateDTO;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/worktasktemplates")
@CrossOrigin
public class WorkTaskTemplateController {

    private final WorkTaskTemplateRepository workTaskTemplateRepository;
    private final WorkOrderCategoryRepository workOrderCategoryRepository;
    private final ArticleRepository articleRepository;
    private final RequiredPartTemplateRepository requiredPartTemplateRepository;
    private final RepairCategoryRepository repairCategoryRepository;

    public WorkTaskTemplateController(WorkTaskTemplateRepository templateRepo,
                                      WorkOrderCategoryRepository categoryRepo,
                                      ArticleRepository articleRepository,
                                      RequiredPartTemplateRepository requiredPartTemplateRepository,
                                      RepairCategoryRepository repairCategoryRepository) {
        this.workTaskTemplateRepository = templateRepo;
        this.workOrderCategoryRepository = categoryRepo;
        this.articleRepository = articleRepository;
        this.requiredPartTemplateRepository = requiredPartTemplateRepository;
        this.repairCategoryRepository = repairCategoryRepository;
    }

    @GetMapping
    public List<WorkTaskTemplateDTO> getAllTemplates() {
        return workTaskTemplateRepository.findAllWithCategory().stream().map(template -> {
            WorkTaskTemplateDTO dto = new WorkTaskTemplateDTO();
            dto.setId(template.getId());
            dto.setDescription(template.getDescription());
            dto.setEstimatedTime(template.getEstimatedTime());
            // 🔍 Debug-logg: Se om template har en kategori
            if (template.getCategory() == null) {
                System.out.println("Mall " + template.getId() + " har ingen kategori!");
            } else {
                dto.setCategoryId(template.getCategory().getId());
                dto.setCategoryName(template.getCategory().getName());
                System.out.println("Mall " + template.getId() + " har kategori: " + template.getCategory().getName());
            }

            if(dto.getCategoryId().equals(2)) {
                dto.setRepairCategoryId(template.getRepairCategory().getId());
                dto.setRepairCategoryName(template.getRepairCategory().getName());
            }

            return dto;
        }).toList();
    }


    @GetMapping("/byCategory")
    public List<WorkTaskTemplate> getTemplatesByWorkOrderCategory(@RequestParam Long categoryId) {
        WorkOrderCategory category = workOrderCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorin finns inte."));
        return workTaskTemplateRepository.findByCategory(category);
    }

    @GetMapping("/byRepairCategory")
    public List<WorkTaskTemplate> getTemplatesByRepairCategory(@RequestParam("repairCategoryId") Long repairCategoryId) {
        RepairCategory category = repairCategoryRepository.findById(repairCategoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorin finns inte."));
        return workTaskTemplateRepository.findByRepairCategory(category);
    }

    // ✅ Hämta alla artiklar kopplade till en arbetsuppgiftsmall
    @GetMapping("/{templateId}/articles")
    public ResponseEntity<List<RequiredPartDTO>> getTemplateArticles(@PathVariable Long templateId) {
        Optional<WorkTaskTemplate> templateOpt = workTaskTemplateRepository.findById(templateId);

        if (templateOpt.isPresent()) {
            List<RequiredPartDTO> articleList = requiredPartTemplateRepository.findByTaskTemplateId(templateId)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(articleList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<WorkTaskTemplate> createTemplate(@RequestBody WorkTaskTemplateDTO dto) {
        System.out.println("Mottagen payload: " + dto); // 🔥 Debug-logg

        if (dto.getCategoryId() == null || dto.getDescription().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        WorkOrderCategory category = workOrderCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Kategorin finns inte."));

        WorkTaskTemplate newTemplate = new WorkTaskTemplate();
        newTemplate.setDescription(dto.getDescription());
        newTemplate.setCategory(category);
        newTemplate.setEstimatedTime(dto.getEstimatedTime() != null ? dto.getEstimatedTime() : 0);  // 🔥 Hantera null-värden

        System.out.println("Skapar mall: " + newTemplate); // 🔥 Debug-logg

        WorkTaskTemplate savedTemplate = workTaskTemplateRepository.save(newTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTemplate);
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long templateId) {
        WorkTaskTemplate template = workTaskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mallen finns inte."));

        workTaskTemplateRepository.delete(template);
        return ResponseEntity.ok().build();
    }



    // ✅ Lägg till en artikel i en arbetsuppgiftsmall
    @PostMapping("/{templateId}/articles")
    public ResponseEntity<?> addArticleToTemplate(@PathVariable Long templateId,
                                                  @RequestBody RequiredPartDTO request) {
        WorkTaskTemplate template = workTaskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mallen finns inte."));

        Article article = articleRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikeln finns inte."));

        boolean exists = template.getRequiredParts().stream()
                .anyMatch(part -> part.getArticle().getId().equals(request.getId()));

        if (!exists) {
            RequiredPartTemplate requiredPart = new RequiredPartTemplate();
            requiredPart.setTaskTemplate(template);
            requiredPart.setArticle(article);
            requiredPart.setQuantity(request.getQuantity());

            requiredPartTemplateRepository.save(requiredPart);
        }

        return ResponseEntity.ok(template);
    }

    // ✅ Ta bort en artikel från en arbetsuppgiftsmall
    @DeleteMapping("/{templateId}/articles/{articleId}")
    public ResponseEntity<?> removeArticleFromTemplate(@PathVariable Long templateId, @PathVariable Long articleId) {
        List<RequiredPartTemplate> requiredParts = requiredPartTemplateRepository.findByTaskTemplateId(templateId);
        for (RequiredPartTemplate part : requiredParts) {
            if (part.getArticle().getId().equals(articleId)) {
                requiredPartTemplateRepository.delete(part);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artikeln finns inte i mallen.");
    }

    // ✅ Uppdatera kvantitet på en artikel i en arbetsuppgiftsmall
    @PutMapping("/requiredparts/{requiredPartId}")
    public ResponseEntity<?> updateArticleQuantity(@PathVariable Long requiredPartId,
                                                   @RequestBody RequiredPartDTO request) {
        RequiredPartTemplate requiredPart = requiredPartTemplateRepository.findById(requiredPartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikel finns inte i mallen."));

        requiredPart.setQuantity(request.getQuantity());
        requiredPartTemplateRepository.save(requiredPart);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{templateId}")
    @Transactional
    public ResponseEntity<WorkTaskTemplate> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody WorkTaskTemplateDTO dto) {

        WorkTaskTemplate template = workTaskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mallen finns inte."));

        System.out.println("Uppdaterar mall: " + dto);  // 🔥 Se vad frontend skickar

        if (dto.getCategoryId() != null) {
            WorkOrderCategory category = workOrderCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorin finns inte."));
            template.setCategory(category);
            System.out.println("Kategori uppdaterad till: " + category.getId());  // 🔥 Bekräfta att kategorin sätts
        }

        template.setDescription(dto.getDescription());
        template.setEstimatedTime(dto.getEstimatedTime());

        template = workTaskTemplateRepository.save(template);
        System.out.println("Sparad kategori: " + template.getCategory().getId());
        return ResponseEntity.ok(template);
    }

    @GetMapping("/{templateId}/requiredParts")
    public ResponseEntity<List<RequiredPartDTO>> getRequiredPartsForTemplate(@PathVariable Long templateId) {
        WorkTaskTemplate template = workTaskTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mallen finns inte."));

        List<RequiredPartDTO> requiredParts = requiredPartTemplateRepository.findByTaskTemplateId(templateId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(requiredParts);
    }




    /**
     * 🎯 Konvertera `RequiredPartTemplate` till `RequiredPartDTO`
     */
    private RequiredPartDTO convertToDTO(RequiredPartTemplate part) {
        RequiredPartDTO dto = new RequiredPartDTO();
        dto.setId(part.getArticle().getId());
        dto.setDescription(part.getArticle().getDescription());
        dto.setPartNumber(part.getArticle().getPartNumber());
        dto.setQuantity(part.getQuantity());
        return dto;
    }
}
