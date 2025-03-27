package com.trainbooker.service;

import com.trainbooker.dto.BookingDTO;
import com.trainbooker.dto.BookingRequest;
import com.trainbooker.model.Booking;
import com.trainbooker.model.Train;
import com.trainbooker.model.User;
import com.trainbooker.repository.BookingRepository;
import com.trainbooker.repository.TrainRepository;
import com.trainbooker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TrainRepository trainRepository;
    private final TrainService trainService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          TrainRepository trainRepository,
                          TrainService trainService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.trainRepository = trainRepository;
        this.trainService = trainService;
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserOrderByBookingDateDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return convertToDTO(booking);
    }

    @Transactional
    public BookingDTO createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new RuntimeException("Train not found"));

        // Check if enough seats are available
        if (train.getAvailableSeats() < request.getPassengers()) {
            throw new RuntimeException("Not enough seats available");
        }

        // Calculate total price
        double totalPrice = train.getPrice() * request.getPassengers();

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrain(train);
        booking.setPassengers(request.getPassengers());
        booking.setTotalPrice(totalPrice);
        booking.setBookingDate(LocalDate.now());
        booking.setStatus("Confirmed");
        booking.setSeatNumber(generateSeatNumber());

        // Update train available seats
        trainService.updateTrainSeats(train.getId(), request.getPassengers());

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("Cancelled".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus("Cancelled");

        // Restore train seats
        trainService.restoreTrainSeats(booking.getTrain().getId(), booking.getPassengers());

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO restoreBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("Confirmed".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already confirmed");
        }

        Train train = booking.getTrain();

        // Check if enough seats are available
        if (train.getAvailableSeats() < booking.getPassengers()) {
            throw new RuntimeException("Not enough seats available to restore this booking");
        }

        booking.setStatus("Confirmed");

        // Update train available seats
        trainService.updateTrainSeats(train.getId(), booking.getPassengers());

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }

    public String generateSeatNumber() {
        Random random = new Random();
        char letter = (char) ('A' + random.nextInt(26)); // Random letter from A-Z
        int number = random.nextInt(90) + 10; // Random two-digit number (10-99)
        return letter + String.valueOf(number);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUsername(booking.getUser().getUsername());
        dto.setTrainId(booking.getTrain().getId());
        dto.setTrainNumber(booking.getTrain().getTrainNumber());
        dto.setTrainName(booking.getTrain().getName());
        dto.setSeatNumber(booking.getSeatNumber());
        dto.setFromStation(booking.getTrain().getFromStation());
        dto.setToStation(booking.getTrain().getToStation());
        dto.setDate(booking.getTrain().getDate());
        dto.setPassengers(booking.getPassengers());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}

