package com.insurance.demo.repository;

import com.insurance.demo.model.db.ContractCreatedEvent;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface ContractCreatedEventRepository extends JpaRepository<ContractCreatedEvent, Integer> {

     ArrayList<ContractCreatedEvent> findAllByDate(LocalDate date);

     @Query("SELECT count(e.id) FROM #{#entityName} e WHERE e.date = :previousMonthEndDate or e.date = :startDate ")
     Integer getCount(
             @Param("previousMonthEndDate") LocalDate previousMonthEndDate, @Param("startDate") LocalDate startDate);

     @Query("SELECT count(e.id) FROM #{#entityName} e WHERE e.date = :previousMonthEndDate or e.date = :startDate ")
     Integer getCount1(
             @Param("previousMonthEndDate") LocalDate previousMonthEndDate, @Param("startDate") LocalDate startDate);


     ArrayList<ContractCreatedEvent> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

     @Query("SELECT e.premium FROM #{#entityName} e WHERE e.contractId = :contractId")
     List<Integer> getPremiumByContractId(@Param("contractId") Integer contractId);

     @Query(nativeQuery = true, value = "SELECT MONTH(e.start_date) FROM #{#entityName} e WHERE e.contract_Id = :contractId")
     Integer getMonthFromContractId(@Param("contractId") Integer contractId);



}