package com.insurance.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Month;
import java.util.HashMap;


public class Contract {


    private Integer contractId;

    private Integer premium;

    HashMap<Month,Integer> priceChange ;

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

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Contract)) {
            return false;
        }
        Contract otherMember = (Contract)anObject;
        return otherMember.getContractId().equals(getContractId());
    }

    public HashMap<Month, Integer> getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(HashMap<Month, Integer> priceChange) {
        this.priceChange = priceChange;
    }




}

