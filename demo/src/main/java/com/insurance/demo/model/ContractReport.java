package com.insurance.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insurance.demo.model.Contract;

import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;

public class ContractReport {

    private Integer noOfContracts;
    private Integer monthPremium;
    private Integer yearPremium;
    @JsonIgnore
    private Integer carriedOverYearlyPremium;
    private Month month;

    @JsonIgnore
    private ArrayList<Contract> contractList;

    public Integer getNoOfContracts() {
        return noOfContracts;
    }

    public void setNoOfContracts(Integer noOfContracts) {
        this.noOfContracts = noOfContracts;
    }

    public Integer getMonthPremium() {
        return monthPremium;
    }

    public void setMonthPremium(Integer monthPremium) {
        this.monthPremium = monthPremium;
    }

    public Integer getYearPremium() {
        return yearPremium;
    }

    public void setYearPremium(Integer yearPremium) {
        this.yearPremium = yearPremium;
    }

    public ArrayList<Contract> getContractList() {
        return contractList;
    }

    public void setContractList(ArrayList<Contract> contractList) {
        this.contractList = contractList;
    }

    public Integer getCarriedOverYearlyPremium() {
        return carriedOverYearlyPremium;
    }

    public void setCarriedOverYearlyPremium(Integer carriedOverYearlyPremium) {
        this.carriedOverYearlyPremium = carriedOverYearlyPremium;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }
}
