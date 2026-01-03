package se.meltastudio.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.model.WorkOrderBooking;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface WorkOrderBookingRepository extends JpaRepository<WorkOrderBooking, Long> {
    boolean existsByMechanicAndStartTimeLessThanAndEndTimeGreaterThan(User mechanic, LocalDateTime endTime, LocalDateTime startTime);
    List<WorkOrderBooking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}