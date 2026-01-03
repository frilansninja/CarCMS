package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.*;
import se.meltastudio.cms.repository.*;

import java.util.List;
import java.util.Optional;

@Service
public class WorkTaskTemplateService {

    private final WorkTaskTemplateRepository workTaskTemplateRepository;
    private final RequiredPartTemplateRepository requiredPartTemplateRepository;
    private final ArticleRepository articleRepository;

    public WorkTaskTemplateService(
            WorkTaskTemplateRepository workTaskTemplateRepository,
            RequiredPartTemplateRepository requiredPartTemplateRepository,
            ArticleRepository articleRepository
    ) {
        this.workTaskTemplateRepository = workTaskTemplateRepository;
        this.requiredPartTemplateRepository = requiredPartTemplateRepository;
        this.articleRepository = articleRepository;
    }

    public Optional<WorkTaskTemplate> getTemplateById(Long id) {
        return workTaskTemplateRepository.findById(id);
    }

    public List<WorkTaskTemplate> getAllTemplates() {
        return workTaskTemplateRepository.findAll();
    }

    public Optional<WorkTaskTemplate> addArticleToTemplate(Long templateId, Long articleId, int quantity) {
        Optional<WorkTaskTemplate> templateOpt = workTaskTemplateRepository.findById(templateId);
        Optional<Article> articleOpt = articleRepository.findById(articleId);

        if (templateOpt.isPresent() && articleOpt.isPresent()) {
            WorkTaskTemplate template = templateOpt.get();
            Article article = articleOpt.get();

            // Kontrollera om artikeln redan finns i mallen
            boolean exists = requiredPartTemplateRepository
                    .findByTaskTemplateId(templateId)
                    .stream()
                    .anyMatch(rpt -> rpt.getArticle().getId().equals(articleId));

            if (!exists) {
                RequiredPartTemplate requiredPart = new RequiredPartTemplate();
                requiredPart.setTaskTemplate(template);
                requiredPart.setArticle(article);
                requiredPart.setQuantity(quantity);
                requiredPartTemplateRepository.save(requiredPart);
            }
            return Optional.of(template);
        }
        return Optional.empty();
    }

    public boolean removeArticleFromTemplate(Long templateId, Long articleId) {
        List<RequiredPartTemplate> requiredParts = requiredPartTemplateRepository.findByTaskTemplateId(templateId);
        for (RequiredPartTemplate part : requiredParts) {
            if (part.getArticle().getId().equals(articleId)) {
                requiredPartTemplateRepository.delete(part);
                return true;
            }
        }
        return false;
    }

    public boolean updateArticleQuantity(Long requiredPartId, int newQuantity) {
        Optional<RequiredPartTemplate> requiredPartOpt = requiredPartTemplateRepository.findById(requiredPartId);
        if (requiredPartOpt.isPresent()) {
            RequiredPartTemplate requiredPart = requiredPartOpt.get();
            requiredPart.setQuantity(newQuantity);
            requiredPartTemplateRepository.save(requiredPart);
            return true;
        }
        return false;
    }
}
