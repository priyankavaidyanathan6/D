package com.insurance.demo.controller;

import com.insurance.demo.model.ContractReport;
import com.insurance.demo.model.GenerateReportRequest;
import com.insurance.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    public static final String STATUS = "status";
    public static final String ERROR_CODE = "errorCode";


    @Autowired private ReportService reportService;

    /*@GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }*/

    @PostMapping(
            value = "/api/report/generateReport",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> generateReport( @ModelAttribute GenerateReportRequest generateReportRequest
    ) {
        try {


            reportService.generateReport(generateReportRequest);


        } catch (HttpClientErrorException e) {
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", e.getStatusText());
            responseMap.put(ERROR_CODE, "ERROR");
            responseMap.put(STATUS, "BAD_REQUEST");
            return ResponseEntity.badRequest().body(responseMap);
        } catch (Exception e) {
            String msg = "Error";
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(STATUS, "SUCCESS");
        return ResponseEntity.ok(responseMap);
    }



    @GetMapping(value = "/api/getReport")
    public ArrayList<ContractReport>getReport() {


        ArrayList<ContractReport> response = reportService.getReportForCreatedAndTerminatedEvents(2020);
        if (response == null) {
            String msg = "Error in fetching Report.";
            //throw new InternalServerErrorException(msg);
        }

        return response;



    }

    @GetMapping(value = "/api/getAllReport")
    public ArrayList<ContractReport> getReportForAllEvents() {


        ArrayList<ContractReport> response = reportService.getReportForAllEvents(2020);
        if (response == null) {
            String msg = "Error in fetching Report.";
            //throw new InternalServerErrorException(msg);
        }

        return response;



    }



}
