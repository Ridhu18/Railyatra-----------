package com.trainbooker.service;

import com.trainbooker.dto.SearchTrainRequest;
import com.trainbooker.dto.TrainDTO;
import com.trainbooker.model.Train;
import com.trainbooker.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    @Autowired
    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public List<TrainDTO> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TrainDTO getTrainById(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        return convertToDTO(train);
    }

    public List<TrainDTO> searchTrains(SearchTrainRequest request) {
        return trainRepository.searchTrains(
                        request.getFromStation(),
                        request.getToStation(),
                        request.getDate()
                ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TrainDTO addTrain(TrainDTO trainDTO) {
        Train train = convertToEntity(trainDTO);
        train.setId(null); // Ensure we're creating a new train
        train.setAvailableSeats(train.getTotalSeats()); // Initialize available seats

        Train savedTrain = trainRepository.save(train);
        return convertToDTO(savedTrain);
    }

    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }

    public TrainDTO updateTrainSeats(Long id, int seatsToReduce) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        if (train.getAvailableSeats() < seatsToReduce) {
            throw new RuntimeException("Not enough seats available");
        }

        train.setAvailableSeats(train.getAvailableSeats() - seatsToReduce);
        Train savedTrain = trainRepository.save(train);

        return convertToDTO(savedTrain);
    }

    public TrainDTO restoreTrainSeats(Long id, int seatsToRestore) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        train.setAvailableSeats(train.getAvailableSeats() + seatsToRestore);

        // Ensure we don't exceed total seats
        if (train.getAvailableSeats() > train.getTotalSeats()) {
            train.setAvailableSeats(train.getTotalSeats());
        }

        Train savedTrain = trainRepository.save(train);

        return convertToDTO(savedTrain);
    }

    private TrainDTO convertToDTO(Train train) {
        TrainDTO dto = new TrainDTO();
        dto.setId(train.getId());
        dto.setTrainNumber(train.getTrainNumber());
        dto.setName(train.getName());
        dto.setFromStation(train.getFromStation());
        dto.setToStation(train.getToStation());
        dto.setDepartureTime(train.getDepartureTime());
        dto.setArrivalTime(train.getArrivalTime());
        dto.setDate(train.getDate());
        dto.setPrice(train.getPrice());
        dto.setTotalSeats(train.getTotalSeats());
        dto.setAvailableSeats(train.getAvailableSeats());
        return dto;
    }

    private Train convertToEntity(TrainDTO dto) {
        Train train = new Train();
        train.setId(dto.getId());
        train.setTrainNumber(dto.getTrainNumber());
        train.setName(dto.getName());
        train.setFromStation(dto.getFromStation());
        train.setToStation(dto.getToStation());
        train.setDepartureTime(dto.getDepartureTime());
        train.setArrivalTime(dto.getArrivalTime());
        train.setDate(dto.getDate());
        train.setPrice(dto.getPrice());
        train.setTotalSeats(dto.getTotalSeats());
        train.setAvailableSeats(dto.getAvailableSeats());
        return train;
    }
}

