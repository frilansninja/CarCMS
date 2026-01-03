package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.meltastudio.cms.model.WorkTask;


import java.util.List;

public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {

}
