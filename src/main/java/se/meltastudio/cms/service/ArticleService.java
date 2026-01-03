package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.Article;
import se.meltastudio.cms.repository.ArticleRepository;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<Article> getAllParts() {
        return articleRepository.findAll();
    }
}
