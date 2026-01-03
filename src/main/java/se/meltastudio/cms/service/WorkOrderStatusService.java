package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.model.WorkOrderStatus;
import se.meltastudio.cms.repository.WorkOrderStatusRepository;

import java.util.List;

@Service
public class WorkOrderStatusService {
    private final WorkOrderStatusRepository repository;

    public WorkOrderStatusService(WorkOrderStatusRepository repository) {
        this.repository = repository;
    }

    public List<WorkOrderStatus> getAllStatuses() {
        return repository.findAll();
    }

    public WorkOrderStatus getStatusById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
