package com.example.solar_power_plant.service;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.repository.DataByPeriodAndSolarPowerPlant;
import com.example.solar_power_plant.repository.DynamicDataRepository;
import com.example.solar_power_plant.enums.Weather;
import com.example.solar_power_plant.model.*;
import com.example.weather.OpenWeather;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@PropertySource("classpath:project.properties")
public class DynamicDataService {

    @Value("${DATA_COLLECTION_TIME}")
    private int DATA_COLLECTION_TIME; // 5 секунд
    //private final int DATA_COLLECTION_TIME = 5 * 1000; // 5 секунд
    //private final int DATA_COLLECTION_TIME = 1000 * 60 * 30; // 30 хвилин

    @Value("${OPEN_WEATHER_MAP_API_KEY}")
    private String API_KEY;


    private final DynamicDataRepository dynamicDataRepository;
    private final SolarPowerPlantService solarPowerPlantService;


    public DynamicDataService(DynamicDataRepository dynamicDataRepository,
                              SolarPowerPlantService solarPowerPlantService) {
        this.dynamicDataRepository = dynamicDataRepository;
        this.solarPowerPlantService = solarPowerPlantService;
    }

    public List<DynamicData> getDynamicDataBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        return (List<DynamicData>) dynamicDataRepository.findAllBySolarPowerPlant(solarPowerPlant);
    }

    public List<DynamicData> getDynamicDataBetweenCollectionDateTimeAndBySolarPowerPlant(LocalDateTime startDateTime,
                                                                                         LocalDateTime finishDateTime,
                                                                                         SolarPowerPlant solarPowerPlant) {
        return (List<DynamicData>) dynamicDataRepository.findAllByCollectionDateTimeBetweenAndSolarPowerPlant(
                startDateTime,
                finishDateTime,
                solarPowerPlant
        );
    }

    public void addDynamicData(DynamicData dynamicData) {
        dynamicDataRepository.save(dynamicData);
    }

    @Async
    @Scheduled(fixedRateString = "${DATA_COLLECTION_TIME}" /*1000*60*1000*/ /*5 * 1000*/ /*2 * 60 * 1000*/)
    public void collectDynamicData() throws IOException {
        saveDynamicData(LocalDateTime.now(), false);
    }

    public void saveDynamicData(LocalDateTime dateTime, boolean demoData) throws IOException {
        if (demoData) {

            LocalDateTime dateTimeCopy;

            double producedPower, coefficient;
            int month, solarPowerPlantCount = 0, quantityOfDescriptionForSolarPowerPlant;

            Weather weather;

            List<String> weatherDescriptions = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader("demo-data/weather_description_short.txt"));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                weatherDescriptions.add(currentLine);
            }
            bufferedReader.close();

            quantityOfDescriptionForSolarPowerPlant = weatherDescriptions.size() / solarPowerPlantService.getAllSolarPowerPlants().size();

            //в базу вручну
            for (SolarPowerPlant solarPowerPlant : solarPowerPlantService.getAllSolarPowerPlants()) {
                dateTimeCopy = dateTime;
                for (int i = 0; i < quantityOfDescriptionForSolarPowerPlant; i++) {
                    dateTimeCopy = dateTimeCopy.plusMinutes(30);

                    month = dateTime.getMonthValue();
                    weather = Weather.valueOf(getFormattedDescription(weatherDescriptions.get(solarPowerPlantCount * quantityOfDescriptionForSolarPowerPlant + i)));
                    coefficient = weather.getCoefficient();

                    producedPower = generateProducedPower(month,
                            dateTimeCopy.getHour(),
                            coefficient,
                            solarPowerPlant.getStaticData().getPower(),
                            solarPowerPlant.getStaticData().getQuantity(),
                            getYearOfUsageTime(solarPowerPlant));

                    dynamicDataRepository.save(new DynamicData(
                            solarPowerPlant,
                            weather,
                            producedPower,
                            dateTimeCopy));
                }
                solarPowerPlantCount++;
            }
        } else {
            dynamicDataRepository.saveAll(generateData(dateTime));
        }
    }

    private List<DynamicData> generateData(LocalDateTime dateTime) throws IOException {

        List<DynamicData> generatedData = new ArrayList<>();
        List<SolarPowerPlant> solarPowerPlantList = solarPowerPlantService.getAllSolarPowerPlants();

        double producedPower, coefficient;
        int month;
        OpenWeather openWeather;
        Weather weather;

        for (SolarPowerPlant solarPowerPlant : solarPowerPlantList) {
            month = dateTime.getMonthValue();

            openWeather = getOpenWeatherInfo(
                    solarPowerPlant.getLocation().getLatitude(),
                    solarPowerPlant.getLocation().getLongitude());

            coefficient = getWeather(openWeather).getCoefficient();
            weather = getWeather(openWeather);

            producedPower = generateProducedPower(month,
                    dateTime.getHour(),
                    coefficient,
                    solarPowerPlant.getStaticData().getPower(),
                    solarPowerPlant.getStaticData().getQuantity(),
                    getYearOfUsageTime(solarPowerPlant));

            generatedData.add(new DynamicData(
                    solarPowerPlant,
                    weather,
                    producedPower,
                    dateTime));
        }

        return generatedData;
    }

    private double generateProducedPower(
            int month,
            int hour,
            double weatherCoefficient,
            Integer power,
            Integer quantity,
            int usingTime) {

        double producerPower = power;

        double min1 = 0.9, min2 = 0.8;
        double max = 1;

        producerPower
                *= monthlyCoefficient(month, 0.4, 1.02)
                * weatherCoefficient
                * hourlyCoefficient(hour, month)
                * quantity;

        if (usingTime <= 12) {
            producerPower *= min1 + Math.random() * (max - min1);
        } else if (usingTime <= 25) {
            producerPower *= min2 + Math.random() * (max - min2);
        }

        producerPower /= 16 * 3600 / (DATA_COLLECTION_TIME / 1000D);

        return producerPower;
    }

    private double monthlyCoefficient(int month, double firstCoefficient, double secondCoefficient) {
        return ((Math.sin(firstCoefficient * month - secondCoefficient)) + 1) / 2;
    }

    private double hourlyCoefficient(int hour, int month) {
        return (hour >= 5 && hour <= 20) ? (((Math.sin(0.44 * hour + 2.3)) + 1) / 2) * monthlyCoefficient(month, 0.3, 0.25) : 0;
    }

    private OpenWeather getOpenWeatherInfo(double lat, double lon) throws IOException {

        Gson gson = new Gson();
        String fileContent = AuthorizationAccess.getFileContent("http://api.openweathermap.org/data/2.5/weather?lat="
                + lat + "&lon=" + lon + "&appid=" + API_KEY);

        return gson.fromJson(fileContent, OpenWeather.class);
    }

    public Weather getWeather(OpenWeather openWeather) throws IOException {
        String description = openWeather.getWeather().get(0).getDescription();

        try {
            return Weather.valueOf(getFormattedDescription(description));
        } catch (IllegalArgumentException ex) {

            Path filePath = Paths.get("system-information-files/descriptions.txt");

            String message = "Main: \"" + openWeather.getWeather().get(0).getMain() + "\", " +
                    "description: \"" + description + "\", " +
                    "dt: " + openWeather.getDt() + "\n";

            Files.writeString(filePath, message, StandardOpenOption.APPEND);

            return Weather.Other;
        }
    }

    public String getFormattedDescription(String description) {
        String[] words = description.split(" ");

        // capitalize each word
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join("", words);
    }

    private int getYearOfUsageTime(@NotNull SolarPowerPlant solarPowerPlant) {
        return AuthorizationAccess.getUsageTime(solarPowerPlant.getStaticData().getInstallationDate(), LocalDate.now()).get(0);
    }

    public String downloadData(HttpServletResponse response,
                               String fileName) {

        Path file = Paths.get("upload-dir/" + fileName);

        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        if (Files.exists(file)) {
            response.setContentType("application/" + getFileExtension(fileName) + "; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            return "download-file-error";
        }
        return null;
    }

    public void createDataCSV(String filename, List<DynamicData> data) throws IOException {
        String directoryName = "upload-dir/";

        List<String[]> dataLines = new ArrayList<>();

        for (DynamicData dynamicData : data) {
            dataLines.add(new String[]{
                    dynamicData.getCollectionDateTime().toString(),
                    dynamicData.getWeather().name(),
                    dynamicData.getProducedPower().toString()});
        }

        givenDataArray_whenConvertToCSV_thenOutputCreated(directoryName + filename, dataLines);
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String fileName, List<String[]> data) throws IOException {
        File csvOutputFile = new File(fileName);

        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    public void createDataXML(String filename, List<DynamicData> data) throws ParserConfigurationException, TransformerException {
        String directoryName = "upload-dir/";

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // root element
        Element root = document.createElement("solarPowerPlant");
        document.appendChild(root);

        Element dataElement, dateTime, weather, producedPower;

        for (DynamicData dynamicData : data) {
            // data element
            dataElement = document.createElement("data");

            root.appendChild(dataElement);

            // dateTime element
            dateTime = document.createElement("dateTime");
            dateTime.appendChild(document.createTextNode(dynamicData.getCollectionDateTime().toString()));
            dataElement.appendChild(dateTime);

            // weather element
            weather = document.createElement("weather");
            weather.appendChild(document.createTextNode(dynamicData.getWeather().name()));
            dataElement.appendChild(weather);

            // producedPower element
            producedPower = document.createElement("producedPower");
            producedPower.appendChild(document.createTextNode(dynamicData.getProducedPower().toString()));
            dataElement.appendChild(producedPower);

        }
        // create the xml file
        //transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(directoryName + filename));

        transformer.transform(domSource, streamResult);

        System.out.println("Done creating XML File");
    }

    public void createDataJSON(String filename, List<DynamicData> data) throws JSONException {
        String directoryName = "upload-dir/";

        JSONArray result = new JSONArray();
        JSONObject jsonObject;

        for (DynamicData dynamicData : data) {
            jsonObject = new JSONObject();

            jsonObject.put("dateTime", dynamicData.getCollectionDateTime().toString());
            jsonObject.put("weather", dynamicData.getWeather().name());
            jsonObject.put("producedPower", dynamicData.getProducedPower().toString());

            result.put(jsonObject);
        }

        try {
            FileWriter file = new FileWriter(directoryName + filename);
            file.write(result.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Double getTotalPowerBySolarPowerPlant(@NotNull SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.getTotalPower(solarPowerPlant.getId());
    }

    public Double getTotalPowerForLastThirtyDaysBySolarPowerPlant(@NotNull SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.getTotalPowerForLastThirtyDays(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                solarPowerPlant.getId());
    }

    public Double getAveragePowerPerDayBySolarPowerPlant(@NotNull SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.getAveragePowerPerDay(
                LocalDateTime.now().minusDays(28),
                LocalDateTime.now(),
                solarPowerPlant.getId());
    }

    public String getFileExtension(String fileName) {
        Pattern pattern = Pattern
                .compile(".*(data\\.(csv|xml|json))");

        Matcher matcher = pattern.matcher(fileName);

        return (matcher.find()) ? matcher.group(2) : "-1";
    }

    public List<DataByPeriodAndSolarPowerPlant> getDataByMonthAndSolarPowerPlant(@NotNull SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.getDataByMonthAndSolarPowerPlant(solarPowerPlant.getId(),
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now());
    }

    public List<Double> getTotalPowers(SolarPowerPlant solarPowerPlant) {
        List<Double> totalPowers = Arrays.asList(new Double[12]);

        List<DataByPeriodAndSolarPowerPlant> dataByMonthAndSolarPowerPlantList =
                getDataByMonthAndSolarPowerPlant(solarPowerPlant);

        for (DataByPeriodAndSolarPowerPlant totalPower : dataByMonthAndSolarPowerPlantList) {
            totalPowers.set(totalPower.getPeriod() - 1, totalPower.getTotal());
        }

        return totalPowers;
    }

    public List<DataByPeriodAndSolarPowerPlant> getDataByHourAndSolarPowerPlant(SolarPowerPlant solarPowerPlant) {

        return dynamicDataRepository.getDataByHourAndSolarPowerPlant(solarPowerPlant.getId(),
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now());
    }

    public List<Double> getAveragePowers(SolarPowerPlant solarPowerPlant) {
        List<Double> averagePowers = Arrays.asList(new Double[24]);

        List<DataByPeriodAndSolarPowerPlant> dataByHourAndSolarPowerPlantList = getDataByHourAndSolarPowerPlant(
                solarPowerPlant);

        for (DataByPeriodAndSolarPowerPlant totalPower : dataByHourAndSolarPowerPlantList) {
            averagePowers.set(totalPower.getPeriod(), totalPower.getTotal());
        }

        return averagePowers;
    }

    public Optional<DynamicData> getFirstDynamicDataBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.findFirstBySolarPowerPlant(solarPowerPlant);
    }

    public void addTotalAndAveragePowerToModel(Model model, SolarPowerPlant solarPowerPlant) {
        Double totalPower = getTotalPowerBySolarPowerPlant(solarPowerPlant);
        model.addAttribute("totalPower", totalPower != null ? String.format("%,.2f", totalPower) : "Недостатньо даних.");

        Double totalPowerForLarThirtyDays = getTotalPowerForLastThirtyDaysBySolarPowerPlant(solarPowerPlant);
        model.addAttribute("totalPowerForLarThirtyDays", totalPowerForLarThirtyDays != null ? String.format("%,.2f", totalPowerForLarThirtyDays) : "Недостатньо даних.");

        Double averagePowerForDay = getAveragePowerPerDayBySolarPowerPlant(solarPowerPlant);
        model.addAttribute("averagePowerForDay", averagePowerForDay != null ? String.format("%,.2f", averagePowerForDay) : "Недостатньо даних.");

        model.addAttribute("usageTime", solarPowerPlantService.getStringOfUsageTime(solarPowerPlant));
    }
}




