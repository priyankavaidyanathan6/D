package com.insurance.demo.service;

import com.insurance.demo.model.ContractReport;
import com.insurance.demo.model.GenerateReportRequest;

import java.util.ArrayList;
import java.util.Map;

public interface ReportService {

    void generateReport(GenerateReportRequest generateReportRequest);


    ArrayList<ContractReport> getReportForCreatedAndTerminatedEvents(int year);

    ArrayList<ContractReport> getReportForAllEvents(int year);


}
