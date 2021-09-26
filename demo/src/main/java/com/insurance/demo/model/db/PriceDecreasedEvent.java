package com.insurance.demo.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PriceDecreasedEvent")
public class PriceDecreasedEvent implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "ID") private Integer id;

    @Column(name = "CONTRACT_ID", nullable = false) private Integer contractId;

    @Column(name = "PREMIUM_DECREASE", nullable = false) private Integer premiumDecrease;

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

    public Integer getPremiumDecrease() {
        return premiumDecrease;
    }

    public void setPremiumDecrease(Integer premiumDecrease) {
        this.premiumDecrease = premiumDecrease;
    }

    public LocalDate getAtDate() {
        return atDate;
    }

    public void setAtDate(LocalDate atDate) {
        this.atDate = atDate;
    }
}


