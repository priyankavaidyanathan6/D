package com.insurance.demo.service.impl;

import com.insurance.demo.model.Contract;
import com.insurance.demo.model.ContractReport;
import com.insurance.demo.model.GenerateReportRequest;
import com.insurance.demo.model.db.*;
import com.insurance.demo.repository.*;
import com.insurance.demo.service.ReportService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;
import java.time.temporal.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ContractCreatedEventRepository contractCreatedEventRepository;
    @Autowired
    ContractTerminatedEventRepository contractTerminatedEventRepository;
    @Autowired
    PriceDecreasedEventRepository priceDecreasedEventRepository;
    @Autowired
    PriceIncreasedEventRepository priceIncreasedEventRepository;

    @Override
    public void generateReport(GenerateReportRequest generateReportRequest) {
        try {

            MultipartFile multipartFile = generateReportRequest.getFile();
            File file = convertMultiPartToFile(multipartFile, "src/temp");
            ArrayList<ContractCreatedEvent> contractCreatedEventArrayList = new ArrayList<>();
            ArrayList<PriceDecreasedEvent> priceDecreasedEventArrayList = new ArrayList<>();
            ArrayList<PriceIncreasedEvent> priceIncreasedEventArrayList = new ArrayList<>();
            ArrayList<ContractTerminatedEvent> contractTerminatedEventArrayList = new ArrayList<>();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {

                JSONObject json = new JSONObject(line);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String name = json.get("name").toString();

                if (name.equalsIgnoreCase("ContractCreatedEvent")) {
                    ContractCreatedEvent contractCreatedEvent = new ContractCreatedEvent();
                    contractCreatedEvent.setContractId(Integer.parseInt(json.get("contractId").toString()));
                    contractCreatedEvent.setDate(LocalDate.parse(json.get("startDate").toString(), formatter));
                    contractCreatedEvent.setPremium((Integer) json.get("premium"));
                    contractCreatedEventArrayList.add(contractCreatedEvent);

                } else if (name.equalsIgnoreCase("PriceDecreasedEvent")) {
                    PriceDecreasedEvent priceDecreasedEvent = new PriceDecreasedEvent();
                    priceDecreasedEvent.setContractId(Integer.parseInt(json.get("contractId").toString()));
                    priceDecreasedEvent.setAtDate(LocalDate.parse(json.get("atDate").toString(), formatter));
                    priceDecreasedEvent.setPremiumDecrease((Integer) json.get("premiumReduction"));
                    priceDecreasedEventArrayList.add(priceDecreasedEvent);
                } else if (name.equalsIgnoreCase("PriceIncreasedEvent")) {
                    PriceIncreasedEvent priceIncreasedEvent = new PriceIncreasedEvent();
                    priceIncreasedEvent.setAtDate(LocalDate.parse(json.get("atDate").toString(), formatter));
                    priceIncreasedEvent.setContractId(Integer.parseInt(json.get("contractId").toString()));
                    priceIncreasedEvent.setPremiumIncrease((Integer) json.get("premiumIncrease"));
                    priceIncreasedEventArrayList.add(priceIncreasedEvent);
                } else if (name.equalsIgnoreCase("ContractTerminatedEvent")) {
                    ContractTerminatedEvent contractTerminatedEvent = new ContractTerminatedEvent();
                    contractTerminatedEvent.setContractId(Integer.parseInt(json.get("contractId").toString()));
                    contractTerminatedEvent.setDate(LocalDate.parse(json.get("terminationDate").toString(), formatter));
                    contractTerminatedEventArrayList.add(contractTerminatedEvent);
                }

            }

            contractCreatedEventRepository.saveAll(contractCreatedEventArrayList);
            contractTerminatedEventRepository.saveAll(contractTerminatedEventArrayList);
            priceDecreasedEventRepository.saveAll(priceDecreasedEventArrayList);
            priceIncreasedEventRepository.saveAll(priceIncreasedEventArrayList);


        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    public static File convertMultiPartToFile(MultipartFile file, String tempFolderPath)
            throws IOException {
        String filePath = tempFolderPath + file.getOriginalFilename();
        File tempFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile;
    }


    public ArrayList<ContractReport> getReportForCreatedAndTerminatedEvents(int year) {

        HashMap<String, ContractReport> map = new HashMap<>();

        ArrayList contractReportList = null;

        try {

            for (Month month : Month.values()) {
                if (month != Month.JANUARY) {

                    map.put(month.toString(), calculate(year, month, map.get(month.minus(1).toString())));


                } else {
                    map.put(month.toString(), calculateForJanuary(year, month));
                }
            }

            // Getting Collection of values from HashMap
            Collection<ContractReport> values = map.values();

            contractReportList = new ArrayList<>(values);

            System.out.println("end");

            return contractReportList;


        } catch (Exception e) {

            e.printStackTrace();

        }

        return contractReportList;

    }


    private ContractReport calculate(int year, Month month, ContractReport previousContractReport) {
        int noOfContractsForCurrentMonth = 0;
        int totalNumberOfContracts = 0;
        Integer yearlyPremium = 0;
        Integer monthlyPremium = 0;
        LocalDate lastDayOfMonth;
        LocalDate firstDayOfMonth;
        LocalDate firstDayOfPreviousMonth = null;
        LocalDate lastDayOfPreviousMonth = null;
        Integer premium = 0;
        Integer previousMonthContractsPremium = 0;
        ArrayList<Contract> contractList = null;
        int reductionInYearlyPremium = 0;
        int carriedOverYearlyPremium = 0;

        int mm = month.ordinal() + 1;

        firstDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
        lastDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.lastDayOfMonth());

        if (month != Month.JANUARY) {

            firstDayOfPreviousMonth = Year.of(year).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
            lastDayOfPreviousMonth = Year.of(year).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.lastDayOfMonth());
        } else {
            firstDayOfPreviousMonth = Year.of(year - 1).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
            lastDayOfPreviousMonth = Year.of(year - 1).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        ArrayList<ContractCreatedEvent> createdEventsListOfTheCurrentMonth = contractCreatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);
        ArrayList<ContractTerminatedEvent> terminatedEventListOfThePreviousMonth = contractTerminatedEventRepository.findAllByDateBetween(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
        ArrayList<ContractTerminatedEvent> terminatedEventsOfTheCurrentMonth = contractTerminatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);

        if (createdEventsListOfTheCurrentMonth != null) {

            contractList = (ArrayList<Contract>) createdEventsListOfTheCurrentMonth.stream().map(x -> createContractObject(x)).collect(Collectors.toList());
            noOfContractsForCurrentMonth = createdEventsListOfTheCurrentMonth.size();
            premium = createdEventsListOfTheCurrentMonth.stream().map(x -> x.getPremium()).reduce(0, Integer::sum);
        }


        ArrayList<Contract> previousMonthContractList = (ArrayList<Contract>) previousContractReport.getContractList().clone();

        if (previousMonthContractList != null) {
            if (terminatedEventListOfThePreviousMonth != null) {

                List<Integer> cT = terminatedEventListOfThePreviousMonth.stream().map(x -> x.getContractId()).collect(Collectors.toList());

                ArrayList<Contract> matching = (ArrayList<Contract>)
                        previousMonthContractList.stream()
                                .filter(
                                        x -> cT.stream()
                                                .anyMatch(c -> c == x.getContractId()))
                                .collect(Collectors.toList());

                previousMonthContractList.removeAll(matching);
                totalNumberOfContracts = noOfContractsForCurrentMonth + previousContractReport.getNoOfContracts() - terminatedEventListOfThePreviousMonth.size();
            }


            contractList.addAll(previousMonthContractList);

            previousMonthContractsPremium = previousMonthContractList.stream().map(x -> x.getPremium()).reduce(0, Integer::sum);

            // totalNumberOfContracts = noOfContractsForCurrentMonth + previousContractReport.getNoOfContracts() - terminatedEventListOfThePreviousMonth.size();

            monthlyPremium = premium + previousMonthContractsPremium + previousContractReport.getMonthPremium();


            if(terminatedEventsOfTheCurrentMonth != null){

                reductionInYearlyPremium = contractList.stream().filter(x -> terminatedEventsOfTheCurrentMonth.stream().anyMatch(c-> c.getContractId() == x.getContractId())).map(x -> x.getPremium()).reduce(0, Integer::sum);

            }



           // contractList.stream().filter(x-> x.getContractId()== ce.getContractId()).map(x -> x.getPremiumIncrease()).reduce(0, Integer::sum);
            yearlyPremium = (12 * premium) + (previousMonthContractsPremium * 12) + previousContractReport.getCarriedOverYearlyPremium() - reductionInYearlyPremium ;
            carriedOverYearlyPremium = previousContractReport.getCarriedOverYearlyPremium() + calculateCarriedOverYearlyPremiumForTerminatedEvents(mm, terminatedEventsOfTheCurrentMonth);
        }


        ContractReport contractReport = new ContractReport();
        contractReport.setNoOfContracts(totalNumberOfContracts);

        contractReport.setMonthPremium(monthlyPremium);
        contractReport.setMonth(month);

        contractReport.setYearPremium(yearlyPremium);
        contractReport.setContractList(contractList);
        contractReport.setCarriedOverYearlyPremium(carriedOverYearlyPremium);
        return contractReport;


    }

    private ContractReport calculateForJanuary(int year, Month month) {

        LocalDate lastDayOfMonth;
        LocalDate firstDayOfMonth;
        LocalDate firstDayOfPreviousMonth = null;
        LocalDate lastDayOfPreviousMonth = null;
        Integer premium = 0;
        Integer previousMonthContractsPremium = 0;
        ArrayList<Contract> contractList = null;

        int mm = month.ordinal() + 1;

        firstDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
        lastDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.lastDayOfMonth());


        ArrayList<ContractCreatedEvent> createdEventsListOfTheCurrentMonth = contractCreatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);

        if (createdEventsListOfTheCurrentMonth != null) {

            contractList = (ArrayList<Contract>) createdEventsListOfTheCurrentMonth.stream().map(x -> createContractObject(x)).collect(Collectors.toList());
            premium = createdEventsListOfTheCurrentMonth.stream().map(x -> x.getPremium()).reduce(0, Integer::sum);
        }

        ArrayList<ContractTerminatedEvent> terminatedEventsOfTheCurrentMonth = contractTerminatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);

        int totalNumberOfContracts = createdEventsListOfTheCurrentMonth.size();

        Integer monthlyPremium = premium;
        Integer carriedOverYearlyPremium =  calculateCarriedOverYearlyPremiumForTerminatedEvents(mm, terminatedEventsOfTheCurrentMonth);
        Integer yearlyPremium = (12 * premium) + carriedOverYearlyPremium;






        ContractReport contractReport = new ContractReport();
        contractReport.setNoOfContracts(totalNumberOfContracts);
        contractReport.setMonthPremium(monthlyPremium);
        contractReport.setYearPremium(yearlyPremium);
        contractReport.setContractList(contractList);
        contractReport.setCarriedOverYearlyPremium(0);
        return contractReport;


    }


    Contract createContractObject(ContractCreatedEvent event) {
        Contract contract = new Contract();
        contract.setContractId(event.getContractId());
        contract.setPremium(event.getPremium());
        HashMap<Month,Integer> priceChange  = new HashMap<>();
        priceChange.put(event.getDate().getMonth(), event.getPremium());
        return contract;

    }


    Integer calculateCarriedOverYearlyPremiumForTerminatedEvents(Integer m, ArrayList<ContractTerminatedEvent> contractTerminatedEventList) {

        int carriedOverPremium = 0;

        if (contractTerminatedEventList != null) {
            for (ContractTerminatedEvent contractTerminatedEvent : contractTerminatedEventList) {
                Integer premium = (contractCreatedEventRepository.getPremiumByContractId(contractTerminatedEvent.getContractId())).get(0);
                Integer month = contractCreatedEventRepository.getMonthFromContractId(contractTerminatedEvent.getContractId());
                carriedOverPremium = carriedOverPremium + (((m - month) + 1) * premium);

            }
        }

        return carriedOverPremium;

    }


    Integer calculateCarriedOverYearlyPremiumForTerminatedEventsForAllEvents(Integer m,ArrayList<Contract> contractList, ArrayList<ContractTerminatedEvent> contractTerminatedEventList) {

        final int[] carriedOverPremium = {0};

        if (contractTerminatedEventList != null) {
            for (ContractTerminatedEvent contractTerminatedEvent : contractTerminatedEventList) {

                contractList.stream().filter(x -> x.getContractId() == contractTerminatedEvent.getContractId()).forEach(x -> {

                    Collection<Integer> values =  x.getPriceChange().values();


                    carriedOverPremium[0] = carriedOverPremium[0] + values.stream().map(y -> y).reduce(0, Integer::sum);



                });

            }
        }

        return carriedOverPremium[0];

    }

    public ArrayList<ContractReport> getReportForAllEvents(int year) {

        HashMap<String, ContractReport> map = new HashMap<>();

        ArrayList<ContractReport> contractReportList = new ArrayList<>();

        try {

            for (Month month : Month.values()) {
                if (month != Month.JANUARY) {

                    map.put(month.toString(), calculate(year, month, map.get(month.minus(1).toString())));


                } else {
                    map.put(month.toString(), calculateForJanuary(year, month));
                }
            }

            contractReportList = (ArrayList) map.values();

            System.out.println("end");

            return contractReportList;


        } catch (Exception e) {

            e.printStackTrace();

        }

        return contractReportList;

    }
    private ContractReport calculateForAllEvents(int year, Month month, ContractReport previousContractReport) {
        int noOfContractsForCurrentMonth = 0;
        int totalNumberOfContracts = 0;
        int yearlyPremium = 0;
        Integer monthlyPremium = 0;
        LocalDate lastDayOfMonth;
        LocalDate firstDayOfMonth;
        LocalDate firstDayOfPreviousMonth = null;
        LocalDate lastDayOfPreviousMonth = null;
        Integer premium = 0;
        Integer previousMonthContractsPremium = 0;
        ArrayList<Contract> contractList = null;
        HashMap<Month,Integer> priceChange = new HashMap<>();
        Integer calculateCarriedOverYearlyPremiumForTerminatedEventsForAllEvents = 0;

        int mm = month.ordinal() + 1;

        firstDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
        lastDayOfMonth = Year.of(year).atMonth(month).atDay(1).with(TemporalAdjusters.lastDayOfMonth());

        if (month != Month.JANUARY) {

            firstDayOfPreviousMonth = Year.of(year).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
            lastDayOfPreviousMonth = Year.of(year).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.lastDayOfMonth());
        } else {
            firstDayOfPreviousMonth = Year.of(year - 1).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.firstDayOfMonth());
            lastDayOfPreviousMonth = Year.of(year - 1).atMonth(month.minus(1)).atDay(1).with(TemporalAdjusters.lastDayOfMonth());
        }

        ArrayList<ContractCreatedEvent> createdEventsListOfTheCurrentMonth = contractCreatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);
        ArrayList<ContractTerminatedEvent> terminatedEventListOfThePreviousMonth = contractTerminatedEventRepository.findAllByDateBetween(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
        ArrayList<ContractTerminatedEvent> terminatedEventsOfTheCurrentMonth = contractTerminatedEventRepository.findAllByDateBetween(firstDayOfMonth, lastDayOfMonth);

        ArrayList<PriceIncreasedEvent> priceIncreasedEventArrayList = priceIncreasedEventRepository.findAllByAtDateBetween(firstDayOfMonth, lastDayOfMonth);
        ArrayList<PriceDecreasedEvent> priceDecreasedEventArrayList = priceDecreasedEventRepository.findAllByAtDateBetween(firstDayOfMonth, lastDayOfMonth);

        if (createdEventsListOfTheCurrentMonth != null) {

            contractList = (ArrayList<Contract>) createdEventsListOfTheCurrentMonth.stream().map(x -> createContractObject(x)).collect(Collectors.toList());
            noOfContractsForCurrentMonth = createdEventsListOfTheCurrentMonth.size();
            premium = createdEventsListOfTheCurrentMonth.stream().map(x -> x.getPremium()).reduce(0, Integer::sum);
        }



        ArrayList<Contract> previousMonthContractList = (ArrayList<Contract>) previousContractReport.getContractList().clone();

        if (previousMonthContractList != null) {
            if (terminatedEventListOfThePreviousMonth != null) {

                List<Integer> cT = terminatedEventListOfThePreviousMonth.stream().map(x -> x.getContractId()).collect(Collectors.toList());

                ArrayList<Contract> matching = (ArrayList<Contract>)
                        previousMonthContractList.stream()
                                .filter(
                                        x -> cT.stream()
                                                .anyMatch(c -> c == x.getContractId()))
                                .collect(Collectors.toList());

                previousMonthContractList.removeAll(matching);
                totalNumberOfContracts = noOfContractsForCurrentMonth + previousContractReport.getNoOfContracts() - terminatedEventListOfThePreviousMonth.size();
            }


            contractList.addAll(previousMonthContractList);



            /////////////////////////////


            if (priceDecreasedEventArrayList != null) {


                // contractList.stream().forEach();

                contractList.stream().forEach(x -> calculateDecreaseInPremium(x, priceDecreasedEventArrayList, month));

            }

            if (priceIncreasedEventArrayList != null) {

                contractList.stream().forEach(x -> calculateIncreaseInPremium(x, priceIncreasedEventArrayList, month));

            }

            if (priceDecreasedEventArrayList != null) {

                contractList.stream().forEach(x -> calculateDecreaseInPremium(x, priceDecreasedEventArrayList, month));

            }

            if (priceIncreasedEventArrayList != null) {

                priceIncreasedEventArrayList.stream().forEach(x -> calculateIncreaseInPremium(x, priceIncreasedEventArrayList, month));

            }

            ArrayList<Contract> unchanged = (ArrayList<Contract>)
                    contractList.stream()
                            .filter(
                                    x -> priceIncreasedEventArrayList.stream()
                                            .noneMatch(c -> c.getContractId() == x.getContractId()))
                            .collect(Collectors.toList());

            unchanged.stream().forEach(x -> {
                x.getPriceChange().put(month, x.getPriceChange().get(month.minus(1)));
            });

            ArrayList<Contract> unchanged2 = (ArrayList<Contract>)
                    contractList.stream()
                            .filter(
                                    x -> priceDecreasedEventArrayList.stream()
                                            .noneMatch(c -> c.getPremiumDecrease() == x.getContractId()))
                            .collect(Collectors.toList());

            unchanged.stream().forEach(x ->
                x.getPriceChange().put(month, x.getPriceChange().get(month.minus(1))));

            unchanged.addAll(unchanged2);
            unchanged.stream().distinct().collect(Collectors.toList());


            unchanged.stream().forEach(x -> {
                x.getPriceChange().put(month,x.getPriceChange().get(month.minus(1)));
            });





            /////////////////

            previousMonthContractsPremium = previousMonthContractList.stream().map(x -> x.getPremium()).reduce(0, Integer::sum);

            int premiumcheck1 = contractList.stream().map(x -> x.getPriceChange().get(month)).reduce(0, Integer::sum);
            int check = premium + previousMonthContractsPremium;
            int check2 = (12 * premium) + (previousMonthContractsPremium * 12);

            // totalNumberOfContracts = noOfContractsForCurrentMonth + previousContractReport.getNoOfContracts() - terminatedEventListOfThePreviousMonth.size();

            monthlyPremium = premiumcheck1 + previousContractReport.getMonthPremium();


            //yearlyPremium = (premiumcheck1*12) + calculateCarriedOverYearlyPremiumForTerminatedEvents(mm, terminatedEventsOfTheCurrentMonth);
            ArrayList<Contract> terminated = (ArrayList<Contract>)
                    contractList.stream()
                            .filter(
                                    x -> terminatedEventsOfTheCurrentMonth.stream()
                                            .anyMatch(c -> c.getContractId() == x.getContractId()))
                            .collect(Collectors.toList());
            contractList.removeAll(terminated);


            for (int i=0; i < month.getValue(); i++){
               for(Contract contract:contractList){

                   if(contract.getPriceChange().get(i) != null){
                       yearlyPremium = yearlyPremium + contract.getPriceChange().get(i);
                   }

               }

            }

           yearlyPremium = yearlyPremium + ((contractList.stream().map(x -> x.getPriceChange().get(month.getValue())).reduce(0,Integer::sum)) * (12 - month.ordinal()));

            calculateCarriedOverYearlyPremiumForTerminatedEventsForAllEvents = calculateCarriedOverYearlyPremiumForTerminatedEvents(mm, terminatedEventsOfTheCurrentMonth);

            yearlyPremium = yearlyPremium + calculateCarriedOverYearlyPremiumForTerminatedEventsForAllEvents;



        }


        ContractReport contractReport = new ContractReport();
        contractReport.setNoOfContracts(totalNumberOfContracts);

        contractReport.setMonthPremium(monthlyPremium);
        contractReport.setCarriedOverYearlyPremium(calculateCarriedOverYearlyPremiumForTerminatedEventsForAllEvents);

        contractReport.setYearPremium(yearlyPremium);
        contractReport.setContractList(contractList);
        return contractReport;


    }


    public void calculateDecreaseInPremium(Contract ce, ArrayList<PriceDecreasedEvent> priceDecreasedEvents, Month month) {

        priceDecreasedEvents.stream().filter(x -> x.getContractId() == ce.getContractId()).forEach(x -> {
            int premium = ce.getPriceChange().get(month);
            ce.getPriceChange().put(month, (x.getPremiumDecrease() - premium));
        });

    }


    public Contract calculateDecreaseInPremium1(Contract ce, ArrayList<PriceDecreasedEvent> priceDecreasedEvents) {

        int totalPremiumDecrease = priceDecreasedEvents.stream().filter(x-> x.getContractId()== ce.getContractId()).map(x -> x.getPremiumDecrease()).reduce(0, Integer::sum);

        ce.setPremium(ce.getPremium() - totalPremiumDecrease);
        return ce;

    }

    public void calculateIncreaseInPremium(Contract ce, ArrayList<PriceIncreasedEvent> priceIncreasedEvents, Month month) {



        priceIncreasedEvents.stream().filter(x-> x.getContractId()== ce.getContractId()).forEach(x -> {
            int premium = ce.getPriceChange().get(month);
            ce.getPriceChange().put(month,(x.getPremiumIncrease()+premium));
        });


    }




}







