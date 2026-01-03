package se.meltastudio.cms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.meltastudio.cms.dto.BookingDTO;
import se.meltastudio.cms.dto.BookingRequest;
import se.meltastudio.cms.model.Booking;
import se.meltastudio.cms.model.User;
import se.meltastudio.cms.service.BookingService;
import se.meltastudio.cms.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }


    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingRequest request) {
        Optional<User> mechanicOpt = userService.findById(request.getMechanicId());

        if (mechanicOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Booking booking = new Booking();
        booking.setTitle(request.getTitle());
        booking.setMechanic(mechanicOpt.get());
        booking.setCategoryColor(request.getCategoryColor());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());

        BookingDTO bookingDTO = bookingService.saveBooking(booking);

        return ResponseEntity.ok(bookingDTO);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
