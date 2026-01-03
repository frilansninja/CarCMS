package se.meltastudio.cms.service;

import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.ArticleRepository;
import se.meltastudio.cms.repository.WorkOrderStatusRepository;

import java.util.List;

public class WorkTaskService {
    private final WorkOrderStatusRepository workOrderStatusRepository;
    private final ArticleRepository articleRepository;

    private final Long PENDING = 1L;

    public WorkTaskService(WorkOrderStatusRepository workOrderStatusRepository, ArticleRepository articleRepository) {
        this.workOrderStatusRepository = workOrderStatusRepository;
        this.articleRepository = articleRepository;
    }

    private WorkTask createTask(WorkOrder workOrder, String description, List<String> partNumbers) {
        WorkTask task = new WorkTask();
        task.setWorkOrder(workOrder);
        task.setDescription(description);

        // Hämta PENDING-status från databasen istället för att använda en enum
        WorkOrderStatus pendingStatus = workOrderStatusRepository.findById(PENDING)
                .orElseThrow(() -> new RuntimeException("Status 'PENDING' saknas i databasen"));

        task.setWorkOrderStatus(pendingStatus);

        List<Article> articles = articleRepository.findByPartNumberIn(partNumbers);

        if (articles.size() != partNumbers.size()) {
            throw new RuntimeException("Vissa artiklar kunde inte hittas i databasen.");
        }

        // 🔥 Koppla artiklarna till arbetsmomentet
        List<WorkTaskArticle> workTaskArticles = articles.stream().map(article -> {
            WorkTaskArticle workTaskArticle = new WorkTaskArticle();
            workTaskArticle.setWorkTask(task);
            workTaskArticle.setArticle(article);
            workTaskArticle.setQuantity(1); // Standardvärde, kan justeras
            return workTaskArticle;
        }).toList();

        task.setWorkTaskArticles(workTaskArticles);
        return task;
    }

}
