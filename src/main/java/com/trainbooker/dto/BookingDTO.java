package com.trainbooker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String fromStation;
    private String toStation;
    private String seatNumber;
    private LocalDate date;
    private Integer passengers;
    private Double totalPrice;
    private LocalDate bookingDate;
    private String status;
}

