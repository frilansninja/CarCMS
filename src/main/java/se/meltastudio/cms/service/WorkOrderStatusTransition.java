package se.meltastudio.cms.service;

import se.meltastudio.cms.enums.WorkOrderStatus;

import java.util.Map;
import java.util.Set;

public class WorkOrderStatusTransition {

    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> allowedTransitions = Map.of(
            WorkOrderStatus.PENDING, Set.of(WorkOrderStatus.DIAGNOSING, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.DIAGNOSING, Set.of(WorkOrderStatus.WAITING_FOR_APPROVAL, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.WAITING_FOR_APPROVAL, Set.of(WorkOrderStatus.WAITING_FOR_PARTS, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.WAITING_FOR_PARTS, Set.of(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.IN_PROGRESS, Set.of(WorkOrderStatus.QUALITY_CONTROL, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.QUALITY_CONTROL, Set.of(WorkOrderStatus.READY_FOR_PICKUP, WorkOrderStatus.CANCELLED),
            WorkOrderStatus.READY_FOR_PICKUP, Set.of(WorkOrderStatus.COMPLETED),
            WorkOrderStatus.COMPLETED, Set.of(), // Ingen vidare ändring möjlig
            WorkOrderStatus.CANCELLED, Set.of()  // Ingen vidare ändring möjlig
    );

    public static boolean isValidTransition(WorkOrderStatus current, WorkOrderStatus newStatus) {
        return allowedTransitions.getOrDefault(current, Set.of()).contains(newStatus);
    }
}
