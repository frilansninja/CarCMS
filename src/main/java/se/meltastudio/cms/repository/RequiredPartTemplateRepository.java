package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.RequiredPartTemplate;

import java.util.List;


public interface RequiredPartTemplateRepository extends JpaRepository<RequiredPartTemplate, Long> {
    List<RequiredPartTemplate> findByTaskTemplateId(Long taskTemplateId);
}
