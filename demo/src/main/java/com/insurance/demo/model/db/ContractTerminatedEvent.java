package com.insurance.demo.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ContractTerminatedEvent")
public class ContractTerminatedEvent implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "ID") private Integer id;


    @Column(name = "CONTRACT_ID", nullable = false) private Integer contractId;


    @Column(
            name = "END_DATE",
            nullable = false,
            updatable = false) private LocalDate date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

