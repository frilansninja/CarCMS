package se.meltastudio.cms.parts.domain;

/**
 * Status of a work order part line.
 */
public enum PartLineStatus {
    PLANNED,    // Part is planned but not yet ordered
    ORDERED,    // Order has been placed with supplier
    RECEIVED,   // Part has been received
    INSTALLED,  // Part has been installed on vehicle
    CANCELLED   // Part line was cancelled
}
