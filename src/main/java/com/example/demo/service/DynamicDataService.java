package com.example.demo.service;

import com.example.demo.dao.DynamicDataRepository;
import com.example.demo.model.DynamicData;
import com.example.demo.model.SolarPowerPlant;
import com.example.demo.model.Weather;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DynamicDataService {

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

    private List<DynamicData> generateData() {
        List<DynamicData> generatedData = new ArrayList<>();
        List<SolarPowerPlant> solarPowerPlantList = solarPowerPlantService.getAllSolarPowerPlants();

        LocalDateTime localDateTime = LocalDateTime.now();

        double producedPower,
                leftLimit = 1D,
                rightLimit = 100D;

        int month, weatherNumber;

        for (SolarPowerPlant solarPowerPlant : solarPowerPlantList) {
            // producedPower = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
            month = localDateTime.getMonthValue();

            System.out.println("Month: " + month);
            weatherNumber = getWeatherNumber(month);
            producedPower = generateProducerPower(month, localDateTime.getHour(), weatherNumber);

            generatedData.add(new DynamicData(
                    solarPowerPlant,
                    Weather.values()[weatherNumber],
                    producedPower,
                    localDateTime));
        }

        return generatedData;
    }

    @Async
    @Scheduled(fixedRate = /*1000*60*1000*/ 5 * 1000 /*2 * 60 * 1000*/)
    public void saveDynamicData() {
        System.out.println("\n\n save dynamic data: ");
        //dynamicDataRepository.save(dynamicData);
        for (DynamicData dynamicData : generateData()) {
            System.out.println("  id: " + dynamicData.getSolarPowerPlant().getId());
            dynamicDataRepository.save(dynamicData);
        }
        System.out.println("\n\n\n");
    }

    private double generateProducerPower(int month, int hour, int weatherNumber) {
        double producerPower = 340.0 / 48.0; //якщо збирати дані раз в 30 хвилин
        producerPower *= monthlyCoefficient(month, 0.4, 1.02) * weatherCoefficient(month, weatherNumber) * hourlyCoefficient(hour, month);

        /*
        Months.values()[index]
         */


        //ArrayList<Weather> weathers = new ArrayList<Weather>();
        //System.out.println("Arrays: "+Arrays.toString(Weather.values()));
        System.out.println("\nProduced power: " + producerPower);

        System.out.println("Monthly coefficient: " + monthlyCoefficient(month, 0.4, 1.02));
        System.out.println("Hourly coefficient: " + hourlyCoefficient(hour, month));


        return producerPower;
    }

    private double monthlyCoefficient(int month, double firstCoefficient, double secondCoefficient) {
        return ((Math.sin(firstCoefficient * month - secondCoefficient)) + 1) / 2;
    }

    private double weatherCoefficient(int month, int weatherNumber) {
        /*int weatherNumber;
        do {
            weatherNumber = new Random().nextInt(Weather.values().length);
            System.out.println("weatherNumber: "+weatherNumber+"  name: "+Weather.values()[weatherNumber].name());
        } while (month > 3 && month < 10 && Weather.values()[weatherNumber].name().equals("Snow"));*/
        System.out.println("Weather coefficient: " + Weather.values()[weatherNumber].getCoefficient());
        //System.out.println("w n: " + weatherNumber);
        //return new double[]{Weather.values()[weatherNumber].getCoefficient(), weatherNumber};
        return Weather.values()[weatherNumber].getCoefficient();
    }

    private int getWeatherNumber(int month) {
        int weatherNumber;
        do {
            weatherNumber = new Random().nextInt(Weather.values().length);
            System.out.println("weatherNumber: " + weatherNumber + "  name: " + Weather.values()[weatherNumber].name());
        } while (month > 3 && month < 10 && Weather.values()[weatherNumber].name().equals("Snow"));
        System.out.println("Weather coefficient: " + Weather.values()[weatherNumber].getCoefficient());
        //System.out.println("w n: " + weatherNumber);
        //return new double[]{Weather.values()[weatherNumber].getCoefficient(), weatherNumber};
        return weatherNumber;
    }

    private double hourlyCoefficient(int hour, int month) {
        System.out.println("hour: " + hour);
        return (hour >= 5 && hour <= 20) ? (((Math.sin(0.44 * hour + 2.3)) + 1) / 2) * monthlyCoefficient(month, 0.3, 0.25) : 0;
    }

    public String downloadData(HttpServletRequest request,
                               HttpServletResponse response,
                               String fileName) {
        //String fileName = "f.csv";
        System.out.println("t-t-t-t-t-t-t");
        String dataDirectory = request.getServletContext().getRealPath("upload-dir/");
        Path file = Paths.get("upload-dir/" + fileName);

        if (Files.exists(file)) {
            System.out.println("5-5-5-5-5-5-5");

            response.setContentType("application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("File not found");
            return "download-file-error";
        }
        System.out.println("0=0=0=0=0=0=0=0=0");
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

        /*dataLines.add(new String[]
                {"John", "Doe", "38", "Comment Data\nAnother line of comment data"});
        dataLines.add(new String[]
                {"Jane", "Doe, Jr.", "19", "She said \"I'm being quoted\""});*/

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
        //assertTrue(csvOutputFile.exists());
    }

    public void createDataXML(String filename, List<DynamicData> data) throws ParserConfigurationException, TransformerException {
        String directoryName = "upload-dir/";

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();

        // root element
        Element root = document.createElement("solarPowerPlant");
        document.appendChild(root);

        for (DynamicData dynamicData : data) {
            // data element
            Element dataElement = document.createElement("data");

            root.appendChild(dataElement);

            // also use staff.setAttribute("id", "1")

            // firstname element
            Element dateTime = document.createElement("dateTime");
            dateTime.appendChild(document.createTextNode(dynamicData.getCollectionDateTime().toString()));
            dataElement.appendChild(dateTime);

            // weather element
            Element weather = document.createElement("weather");
            weather.appendChild(document.createTextNode(dynamicData.getWeather().name()));
            dataElement.appendChild(weather);

            // producedPower element
            Element producedPower = document.createElement("producedPower");
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
        //System.out.println("JSON file created: " + jsonObject);
    }
}

