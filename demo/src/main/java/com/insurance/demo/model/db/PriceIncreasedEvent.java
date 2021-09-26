package com.insurance.demo.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "PriceIncreasedEvent")
public class PriceIncreasedEvent implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "ID") private Integer id;

    @Column(name = "CONTRACT_ID", nullable = false) private Integer contractId;

    @Column(name = "PREMIUM_INCREASE", nullable = false) private Integer premiumIncrease;

    @Column(
            name = "AT_DATE",
            nullable = false,
            updatable = false) private LocalDate atDate;

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

    public Integer getPremiumIncrease() {
        return premiumIncrease;
    }

    public void setPremiumIncrease(Integer premiumIncrease) {
        this.premiumIncrease = premiumIncrease;
    }

    public LocalDate getAtDate() {
        return atDate;
    }

    public void setAtDate(LocalDate atDate) {
        this.atDate = atDate;
    }
}


