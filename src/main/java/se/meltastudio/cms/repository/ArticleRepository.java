package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.meltastudio.cms.model.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a WHERE a.partNumber IN :partNumbers")
    List<Article> findByPartNumberIn(@Param("partNumbers") List<String> partNumbers);

    List<Article> findByPartNumber(String partNumber);
}
