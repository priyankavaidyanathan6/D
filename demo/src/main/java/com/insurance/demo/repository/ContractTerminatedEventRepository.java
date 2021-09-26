package com.insurance.demo.repository;

import com.insurance.demo.model.db.ContractCreatedEvent;
import com.insurance.demo.model.db.ContractTerminatedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ContractTerminatedEventRepository extends JpaRepository<ContractTerminatedEvent, Integer> {

    ArrayList<ContractTerminatedEvent> findAllByDate(LocalDate date);

    @Query("SELECT count(e.id) FROM #{#entityName} e WHERE e.date = :previousMonthEndDate or e.date = :endDate ")
    Integer getCount(
            @Param("previousMonthEndDate") LocalDate previousMonthEndDate, @Param("endDate") LocalDate endDate);

    ArrayList<ContractTerminatedEvent> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

}
