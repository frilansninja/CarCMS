package se.meltastudio.cms.service;

import org.springframework.stereotype.Service;
import se.meltastudio.cms.dto.BookingDTO;
import se.meltastudio.cms.model.Booking;
import se.meltastudio.cms.repository.BookingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public BookingDTO saveBooking(Booking booking) {
        System.out.println("id " + booking.getId());
        System.out.println("tit " + booking.getTitle());
        System.out.println("start " + booking.getStartTime());
        System.out.println("end " + booking.getEndTime());
        System.out.println("mech " + booking.getMechanic().getUsername());

        Booking savedBooking = bookingRepository.save(booking);
        return new BookingDTO(savedBooking);
    }


    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<BookingDTO> findAll() {
        return bookingRepository.findAll().stream()
                .map(BookingDTO::new)
                .collect(Collectors.toList());
    }
}
