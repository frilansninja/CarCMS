package se.meltastudio.cms.controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import se.meltastudio.cms.dto.WorkTaskArticleDTO;
import se.meltastudio.cms.dto.WorkTaskDTO;
import se.meltastudio.cms.model.Article;
import se.meltastudio.cms.model.WorkOrderStatus;
import se.meltastudio.cms.model.WorkTask;
import se.meltastudio.cms.model.WorkTaskArticle;
import se.meltastudio.cms.repository.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/worktasks")
@CrossOrigin
public class WorkTaskController {
        private final WorkTaskRepository workTaskRepository;
        private final WorkOrderStatusRepository workOrderStatusRepository;

        private final ArticleRepository articleRepository;
        private final WorkTaskArticleRepository workTaskArticleRepository;

    public WorkTaskController(WorkTaskRepository workTaskRepository, WorkOrderStatusRepository workOrderStatusRepository, ArticleRepository articleRepository, WorkTaskArticleRepository workTaskArticleRepository) {
        this.workTaskRepository = workTaskRepository;
        this.workOrderStatusRepository = workOrderStatusRepository;
        this.articleRepository = articleRepository;
        this.workTaskArticleRepository = workTaskArticleRepository;
    }

    /**
         * Hämta alla tillgängliga statusar för arbetsmoment.
         */
        @GetMapping("/statuses")
        public ResponseEntity<List<WorkOrderStatus>> getTaskStatuses() {
            List<WorkOrderStatus> statuses = workOrderStatusRepository.findAll(Sort.by(Sort.Order.asc("id")));
            return ResponseEntity.ok(statuses);
        }

        /**
         * Uppdatera status för ett specifikt arbetsmoment.
         */
        @PatchMapping("/{taskId}/status")
        public ResponseEntity<WorkTask> updateTaskStatus(@PathVariable Long taskId, @RequestBody Map<String, Long> request) {
            Long newStatusId = request.get("statusId");
            if (newStatusId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            WorkTask workTask = workTaskRepository.findById(taskId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Arbetsmomentet finns inte"));

            WorkOrderStatus newStatus = workOrderStatusRepository.findById(newStatusId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Statusen finns inte"));

            workTask.setWorkOrderStatus(newStatus);
            workTaskRepository.save(workTask);

            return ResponseEntity.ok(workTask);
        }

    @GetMapping("/{taskId}")
    public ResponseEntity<WorkTaskDTO> getWorkTaskById(@PathVariable Long taskId) {
        WorkTask workTask = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Arbetsmomentet finns inte"));

        WorkTaskDTO taskDTO = new WorkTaskDTO();
        taskDTO.setId(workTask.getId());
        taskDTO.setWorkOrderId(workTask.getWorkOrder().getId());
        taskDTO.setDescription(workTask.getDescription());
        taskDTO.setStatusId(workTask.getWorkOrderStatus().getId());
        taskDTO.setStatusName(workTask.getWorkOrderStatus().getName());
        if(workTask.getWorkTaskTemplate() != null)
            taskDTO.setTemplateId(workTask.getWorkTaskTemplate().getId());

        List<WorkTaskArticleDTO> articleDTOs = workTask.getWorkTaskArticles().stream().map(article -> {
            WorkTaskArticleDTO articleDTO = new WorkTaskArticleDTO();
            articleDTO.setId(article.getArticle().getId());
            articleDTO.setDescription(article.getArticle().getDescription());
            articleDTO.setPartNumber(article.getArticle().getPartNumber());
            articleDTO.setQuantity(article.getQuantity());
            return articleDTO;
        }).collect(Collectors.toList());

        taskDTO.setArticles(articleDTOs);

        return ResponseEntity.ok(taskDTO);
    }

    @PatchMapping("/{taskId}/add-part")
    public ResponseEntity<WorkTask> addArticleToWorkTask(@PathVariable Long taskId, @RequestBody Map<String, Object> request) {
        Long articleId = ((Number) request.get("articleId")).longValue();
        Integer quantity = ((Number) request.getOrDefault("quantity", 1)).intValue();

        WorkTask workTask = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Arbetsmomentet finns inte"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artikeln finns inte"));

        WorkTaskArticle workTaskArticle = new WorkTaskArticle();
        workTaskArticle.setWorkTask(workTask);
        workTaskArticle.setArticle(article);
        workTaskArticle.setQuantity(quantity);

        workTaskArticleRepository.save(workTaskArticle);

        return ResponseEntity.ok(workTask);
    }

    @GetMapping("/{taskId}/articles")
    public ResponseEntity<List<WorkTaskArticle>> getWorkTaskArticles(@PathVariable Long taskId) {
        List<WorkTaskArticle> articles = workTaskArticleRepository.findByWorkTaskId(taskId);
        return ResponseEntity.ok(articles);
    }


}
