package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.PartOrder;

import java.util.List;

@Repository
public interface PartOrderRepository extends JpaRepository<PartOrder, Long> {
    List<PartOrder> findByWorkOrderId(Long workOrderId);
}

