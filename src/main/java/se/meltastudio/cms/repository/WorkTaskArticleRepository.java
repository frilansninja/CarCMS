package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.WorkTaskArticle;
import java.util.List;

public interface WorkTaskArticleRepository extends JpaRepository<WorkTaskArticle, Long> {
    List<WorkTaskArticle> findByWorkTaskId(Long workTaskId);
}
