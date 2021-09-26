package com.insurance.demo.repository;

import com.insurance.demo.model.db.ContractCreatedEvent;
import com.insurance.demo.model.db.PriceIncreasedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public interface PriceIncreasedEventRepository extends JpaRepository<PriceIncreasedEvent, Integer> {

    ArrayList<PriceIncreasedEvent> findAllByAtDateBetween(LocalDate startDate, LocalDate endDate);
}
