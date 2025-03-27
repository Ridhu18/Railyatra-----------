package com.trainbooker.repository;

import com.trainbooker.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByFromStationAndToStationAndDate(String fromStation, String toStation, LocalDate date);

    @Query("SELECT t FROM Train t WHERE " +
            "LOWER(t.fromStation) LIKE LOWER(CONCAT('%', :fromStation, '%')) AND " +
            "LOWER(t.toStation) LIKE LOWER(CONCAT('%', :toStation, '%')) AND " +
            "t.date = :date")
    List<Train> searchTrains(String fromStation, String toStation, LocalDate date);
}

