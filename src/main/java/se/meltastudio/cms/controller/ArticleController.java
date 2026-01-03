package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.ArticleDTO;
import se.meltastudio.cms.model.Article;
import se.meltastudio.cms.repository.ArticleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 🔥 Hämta alla artiklar som ArticleDTO-lista
     */
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(articles);
    }

    /**
     * 🔥 Hämta en specifik artikel med ID
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Artikel ej hittad"));
        return ResponseEntity.ok(convertToDTO(article));
    }

    /**
     * 🔥 Skapa en ny artikel
     */
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleDTO articleDTO) {
        Article article = convertToEntity(articleDTO);
        Article savedArticle = articleRepository.save(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedArticle));
    }

    /**
     * 🔥 Uppdatera en befintlig artikel
     */
    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long articleId, @RequestBody ArticleDTO articleDTO) {
        Optional<Article> existingArticleOpt = articleRepository.findById(articleId);
        if (existingArticleOpt.isPresent()) {
            Article article = existingArticleOpt.get();
            article.setDescription(articleDTO.getDescription());
            article.setPartNumber(articleDTO.getPartNumber());
            article.setSellingPrice(articleDTO.getSellingPrice());
            article.setStockQuantity(articleDTO.getStockQuantity());

            articleRepository.save(article);
            return ResponseEntity.ok(convertToDTO(article));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🔥 Ta bort en artikel
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long articleId) {
        if (articleRepository.existsById(articleId)) {
            articleRepository.deleteById(articleId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 🎯 Konvertera `Article` till `ArticleDTO`
     */
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setDescription(article.getDescription());
        dto.setPartNumber(article.getPartNumber());
        dto.setSellingPrice(article.getSellingPrice());
        dto.setStockQuantity(article.getStockQuantity());
        return dto;
    }

    /**
     * 🎯 Konvertera `ArticleDTO` till `Article`
     */
    private Article convertToEntity(ArticleDTO dto) {
        Article article = new Article();
        article.setDescription(dto.getDescription());
        article.setPartNumber(dto.getPartNumber());
        article.setSellingPrice(dto.getSellingPrice());
        article.setStockQuantity(dto.getStockQuantity());
        return article;
    }
}
