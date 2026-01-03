package se.meltastudio.cms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.ArticleDTO;
import se.meltastudio.cms.dto.SupplierDTO;
import se.meltastudio.cms.model.Article;
import se.meltastudio.cms.model.Supplier;
import se.meltastudio.cms.repository.ArticleRepository;
import se.meltastudio.cms.repository.SupplierRepository;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierRepository supplierRepository;
    private final ArticleRepository articleRepository;

    public SupplierController(SupplierRepository supplierRepository, ArticleRepository articleRepository) {
        this.supplierRepository = supplierRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/parts")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByPart(@RequestParam String partNumber) {
        if (partNumber == null || partNumber.isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400 om partNumber saknas
        }

        List<Article> articles = articleRepository.findByPartNumber(partNumber);
        if (articles.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404 om ingen artikel hittas
        }

        System.out.println("🔍 Hittade artiklar: " + articles.size());


        List<SupplierDTO> supplierDTOs = articles.stream()
                .map(Article::getSupplier)
                .distinct()
                .map(supplier -> {
                    SupplierDTO supplierDTO = new SupplierDTO();
                    supplierDTO.setId(supplier.getId());
                    supplierDTO.setName(supplier.getName());
                    supplierDTO.setContactEmail(supplier.getContactEmail());
                    supplierDTO.setContactPhone(supplier.getContactPhone());
                    supplierDTO.setAddress(supplier.getAddress());

                    // Lägg till artiklar för denna leverantör
                    List<ArticleDTO> articleDTOs = articles.stream()
                            .filter(article -> article.getSupplier().getId().equals(supplier.getId()))
                            .map(article -> {
                                ArticleDTO articleDTO = new ArticleDTO();
                                articleDTO.setId(article.getId());
                                articleDTO.setDescription(article.getDescription());
                                articleDTO.setPartNumber(article.getPartNumber());
                                articleDTO.setSellingPrice(article.getSellingPrice());
                                articleDTO.setStockQuantity(article.getStockQuantity());
                                return articleDTO;
                            })
                            .toList();
                    System.out.println("📦 Leverantör: " + supplier.getName() + " har " + articleDTOs.size() + " artiklar.");

                    supplierDTO.setArticles(articleDTOs);
                    return supplierDTO;
                })
                .toList();

        return ResponseEntity.ok(supplierDTOs);
    }


}
