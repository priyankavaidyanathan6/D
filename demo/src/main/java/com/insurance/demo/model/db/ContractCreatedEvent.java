package com.insurance.demo.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "ContractCreatedEvent")
public class ContractCreatedEvent implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "ID") private Integer id;

    @Column(name = "CONTRACT_ID", nullable = false) private Integer contractId;

    @Column(name = "PREMIUM", nullable = false) private Integer premium;


    @Column(
            name = "START_DATE",
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

    public Integer getPremium() {
        return premium;
    }

    public void setPremium(Integer premium) {
        this.premium = premium;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

