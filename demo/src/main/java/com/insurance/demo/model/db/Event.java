package com.insurance.demo.model.db;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENT")
public class Event implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) @Column(name = "ID") private Integer id;

    @Column(name = "NAME", nullable = false) private String name;

    @Column(name = "CONTRACTID", nullable = false) private Integer contractId;

    @Column(name = "PREMIUM", nullable = false) private Integer premium;

    @Column(
            name = "DATE",
            nullable = false,
            updatable = false) private LocalDateTime date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
