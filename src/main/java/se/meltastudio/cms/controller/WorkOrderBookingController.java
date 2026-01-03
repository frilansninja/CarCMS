package se.meltastudio.cms.controller;


import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.model.WorkOrderBooking;
import se.meltastudio.cms.repository.WorkOrderBookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class WorkOrderBookingController {
    private final WorkOrderBookingRepository bookingRepository;

    public WorkOrderBookingController(WorkOrderBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/week")
    public List<WorkOrderBooking> getBookingsForWeek(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        return bookingRepository.findByStartTimeBetween(start, end);
    }

    @PostMapping("/create")
    public String createBooking(@RequestBody WorkOrderBooking booking) {
        boolean conflict = bookingRepository.existsByMechanicAndStartTimeLessThanAndEndTimeGreaterThan(
                booking.getMechanic(), booking.getEndTime(), booking.getStartTime()
        );

        if (conflict) {
            return "Mechanic is already booked during this time.";
        }

        bookingRepository.save(booking);
        return "Booking created successfully.";
    }
}
