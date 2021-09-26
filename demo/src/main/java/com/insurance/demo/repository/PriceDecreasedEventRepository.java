package com.insurance.demo.repository;

import com.insurance.demo.model.db.ContractCreatedEvent;
import com.insurance.demo.model.db.PriceDecreasedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface PriceDecreasedEventRepository extends JpaRepository<PriceDecreasedEvent, Integer> {

    ArrayList<PriceDecreasedEvent> findAllByAtDateBetween(LocalDate startDate, LocalDate endDate);


}
