package se.meltastudio.cms.enums;

import java.util.EnumSet;
import java.util.Set;

public enum WorkOrderStatus {
    PENDING,               // Arbetsorder har skapats men ej påbörjats
    DIAGNOSING,            // Fordonet undersöks för att fastställa problemet
    WAITING_FOR_APPROVAL,  // Väntar på kundens godkännande av reparation
    WAITING_FOR_PARTS,     // Väntar på reservdelar
    IN_PROGRESS,           // Reparationen pågår
    QUALITY_CONTROL,       // Slutkontroll efter reparation
    READY_FOR_PICKUP,      // Kunden kan hämta fordonet
    COMPLETED,             // Arbetet är slutfört
    CANCELLED;             // Arbetsorder har avbrutits

    public static Set<WorkOrderStatus> getAllowedStatuses(String categoryName) {
        return switch (categoryName.toUpperCase()) {
            case "SERVICE" -> EnumSet.of(PENDING, IN_PROGRESS, READY_FOR_PICKUP, COMPLETED);
            case "REPARATION" -> EnumSet.of(PENDING, DIAGNOSING, WAITING_FOR_PARTS, WAITING_FOR_APPROVAL, IN_PROGRESS, QUALITY_CONTROL, READY_FOR_PICKUP, COMPLETED);
            case "BESIKTNING" -> EnumSet.of(PENDING, READY_FOR_PICKUP, COMPLETED);
            case "DIAGNOSTIK" -> EnumSet.of(PENDING, IN_PROGRESS, READY_FOR_PICKUP, CANCELLED);
            default -> EnumSet.of(PENDING, IN_PROGRESS, COMPLETED); // Standardbeteende om kategorin saknas
        };
    }
}
