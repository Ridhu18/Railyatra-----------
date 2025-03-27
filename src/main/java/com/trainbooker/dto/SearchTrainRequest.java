package com.trainbooker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchTrainRequest {
    private String fromStation;
    private String toStation;
    private LocalDate date;
    private Integer passengers;
}

