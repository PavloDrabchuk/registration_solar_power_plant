package com.example.solar_power_plant.service;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.dao.DataByPeriodAndSolarPowerPlant;
import com.example.solar_power_plant.dao.DynamicDataRepository;
import com.example.solar_power_plant.enums.Weather;
import com.example.solar_power_plant.model.*;
import com.example.weather.OpenWeather;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
//import org.springframework.boot.configurationprocessor.json.JSONArray;
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
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
        System.out.println("\n\n save dynamic data: ");
        //dynamicDataRepository.save(dynamicData);
        /*for (DynamicData dynamicData : generateData(LocalDateTime.now())) {
            System.out.println("  id: " + dynamicData.getSolarPowerPlant().getId());
            dynamicDataRepository.save(dynamicData);
        }*/
        System.out.println("\n\n\n");
        saveDynamicData(LocalDateTime.now(), false);
    }

    public void saveDynamicData(LocalDateTime dateTime, boolean demoData) throws IOException {
        if (demoData) {

            LocalDateTime dateTimeCopy;

            double producedPower, coefficient;
            int month, solarPowerPlantCount = 0, quantityOfDescriptionForSolarPowerPlant;

            Weather weather;

            //List<String> weatherDescriptions = readWeatherDescriptionsFromCSV("demo-data/weather_description_old.csv");
            List<String> weatherDescriptions = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(
                    //new FileReader("demo-data/weather_description.txt"));
                    new FileReader("demo-data/weather_description_short.txt"));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                weatherDescriptions.add(currentLine);
            }
            bufferedReader.close();

            /*for (String w : weatherDescriptions) {
                System.out.println("  w: " + w);
            }*/

            /*LocalDateTime dateTimeCopy;
            double producedPower;
            int month;

            Weather weather;
            double coefficient;

            int solarPowerPlantCount = 0;*/
            quantityOfDescriptionForSolarPowerPlant = weatherDescriptions.size() / solarPowerPlantService.getAllSolarPowerPlants().size();

            //System.out.println(" === q: " + quantityOfDescriptionForSolarPowerPlant + " s: " + weatherDescriptions.size() + " as: " + solarPowerPlantService.getAllSolarPowerPlants().size());

            //в базу вручну
            for (SolarPowerPlant solarPowerPlant : solarPowerPlantService.getAllSolarPowerPlants()) {
                //System.out.println(" --> solarPowerPlantCount: " + solarPowerPlantCount);
                dateTimeCopy = dateTime;
                for (int i = 0; i < quantityOfDescriptionForSolarPowerPlant; i++) {
                    dateTimeCopy = dateTimeCopy.plusMinutes(30);

                    month = dateTime.getMonthValue();

                    //weather = Weather.valueOf("ClearSky");
                    weather = Weather.valueOf(getFormattedDescription(weatherDescriptions.get(solarPowerPlantCount * quantityOfDescriptionForSolarPowerPlant + i)));

                    //System.out.println(" ***** weather: " + weather.name());
                    coefficient = weather.getCoefficient();

                    producedPower = generateProducedPower(month,
                            dateTimeCopy.getHour(),
                            //weatherNumber,
                            //getWeather(openWeather).getCoefficient(),
                            coefficient,
                            solarPowerPlant.getStaticData().getPower(),
                            solarPowerPlant.getStaticData().getQuantity(),
                            getYearOfUsageTime(solarPowerPlant));

                    dynamicDataRepository.save(new DynamicData(
                            solarPowerPlant,
                            //Weather.values()[weatherNumber],
                            //openWeather.getWeather().get(0).getDescription(),
//                    getWeather(openWeather),
                            weather,
                            producedPower,
                            dateTimeCopy));
                    //System.out.println(" kk: " + solarPowerPlantCount * 20 + i);
                }
                solarPowerPlantCount++;
            }
        } else {
            /*for (DynamicData dynamicData : generateData(dateTime)) {
                //System.out.println("  id: " + dynamicData.getSolarPowerPlant().getId());
                dynamicDataRepository.save(dynamicData);
            }*/
            dynamicDataRepository.saveAll(generateData(dateTime));
        }
    }

    private List<DynamicData> generateData(LocalDateTime dateTime) throws IOException {

        List<DynamicData> generatedData = new ArrayList<>();
        List<SolarPowerPlant> solarPowerPlantList = solarPowerPlantService.getAllSolarPowerPlants();

        //LocalDateTime localDateTime = LocalDateTime.now();

        double producedPower, coefficient;
        /*
                leftLimit = 1D,
                rightLimit = 100D;
                */

        int month;
        OpenWeather openWeather;
        Weather weather;

        for (SolarPowerPlant solarPowerPlant : solarPowerPlantList) {
            // producedPower = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
            month = dateTime.getMonthValue();

            System.out.println("Month: " + month);


            /*if (demoData) {
                //
                coefficient = 0.01D;
                weather = Weather.Other;
                weather = Weather.valueOf("ClearSky");
                System.out.println("iiiiiiii: " + weather.getDescription() + " --> " + getFormattedDescription(weather.getDescription()) + " : " + weather.name());
                coefficient = weather.getCoefficient();
                //weather=getWeather(openWeather);

            } else {*/
            //weatherNumber = getWeatherNumber(month);
            openWeather = getOpenWeatherInfo(
                    solarPowerPlant.getLocation().getLatitude(),
                    solarPowerPlant.getLocation().getLongitude());

            coefficient = getWeather(openWeather).getCoefficient();
            weather = getWeather(openWeather);

            // }
            producedPower = generateProducedPower(month,
                    dateTime.getHour(),
                    //weatherNumber,
                    //getWeather(openWeather).getCoefficient(),
                    coefficient,
                    solarPowerPlant.getStaticData().getPower(),
                    solarPowerPlant.getStaticData().getQuantity(),
                    getYearOfUsageTime(solarPowerPlant));

            generatedData.add(new DynamicData(
                    solarPowerPlant,
                    //Weather.values()[weatherNumber],
                    //openWeather.getWeather().get(0).getDescription(),
//                    getWeather(openWeather),
                    weather,
                    producedPower,
                    dateTime));
        }

        return generatedData;
    }

    private double generateProducedPower(
            int month,
            int hour,
            //int weatherNumber,
            double weatherCoefficient,
            Integer power,
            Integer quantity,
            int usingTime) {
        //double producerPower = 340.0 / 48.0; //якщо збирати дані раз в 30 хвилин
        double producerPower = power;

        double min1 = 0.9, min2 = 0.8;
        double max = 1;

        producerPower
                *= monthlyCoefficient(month, 0.4, 1.02)
                //* weatherCoefficient(month, weatherNumber)
                * weatherCoefficient
                * hourlyCoefficient(hour, month)
                * quantity;

        //producerPower *= quantity;
        /*
        Months.values()[index]
         */
        /*if (usingTime >= 10 && usingTime < 25) {
            producerPower *= 0.9;
        } else if (usingTime >= 25) {
            producerPower *= 0.8;
        }*/

        /*double min1 = 0.9, min2 = 0.8;
        double max = 1;*/

        //double random = min1 + Math.random() * (max - min1);

        if (usingTime <= 12) {
            producerPower *= min1 + Math.random() * (max - min1);
        } else if (usingTime <= 25) {
            producerPower *= min2 + Math.random() * (max - min2);
        }

        producerPower /= 16 * 3600 / (DATA_COLLECTION_TIME / 1000D);
        //System.out.println("DATA_COLLECTION_TIME: " + 16 * 3600 / (DATA_COLLECTION_TIME / 1000));

        //ArrayList<Weather> weathers = new ArrayList<Weather>();
        //System.out.println("Arrays: "+Arrays.toString(Weather.values()));

        /*System.out.println("\nProduced power: " + producerPower);

        System.out.println("Monthly coefficient: " + monthlyCoefficient(month, 0.4, 1.02));
        System.out.println("Hourly coefficient: " + hourlyCoefficient(hour, month));*/

        return producerPower;
    }

    private double monthlyCoefficient(int month, double firstCoefficient, double secondCoefficient) {
        return ((Math.sin(firstCoefficient * month - secondCoefficient)) + 1) / 2;
    }

    private double hourlyCoefficient(int hour, int month) {
        System.out.println("hour: " + hour);
        return (hour >= 5 && hour <= 20) ? (((Math.sin(0.44 * hour + 2.3)) + 1) / 2) * monthlyCoefficient(month, 0.3, 0.25) : 0;
    }

    /*private double weatherCoefficient(int month, int weatherNumber) {
        *//*int weatherNumber;
        do {
            weatherNumber = new Random().nextInt(Weather.values().length);
            System.out.println("weatherNumber: "+weatherNumber+"  name: "+Weather.values()[weatherNumber].name());
        } while (month > 3 && month < 10 && Weather.values()[weatherNumber].name().equals("Snow"));*//*
        System.out.println("Weather coefficient: " + Weather.values()[weatherNumber].getCoefficient());
        //System.out.println("w n: " + weatherNumber);
        //return new double[]{Weather.values()[weatherNumber].getCoefficient(), weatherNumber};
        return Weather.values()[weatherNumber].getCoefficient();
    }*/

    /*private int getWeatherNumber(int month) {
        int weatherNumber;
        do {
            weatherNumber = new Random().nextInt(Weather.values().length);
            System.out.println("weatherNumber: " + weatherNumber + "  name: " + Weather.values()[weatherNumber].name());
        } while (month > 3 && month < 10 && Weather.values()[weatherNumber].name().equals("Snow"));
        System.out.println("Weather coefficient: " + Weather.values()[weatherNumber].getCoefficient());
        //System.out.println("w n: " + weatherNumber);
        //return new double[]{Weather.values()[weatherNumber].getCoefficient(), weatherNumber};
        return weatherNumber;
    }*/

    private OpenWeather getOpenWeatherInfo(double lat, double lon) throws IOException {
        //String API_KEY = "ad4112f68d38350518e7c19239012a75";
//        String API_KEY = "${OPEN_WEATHER_API_KEY}";
        //System.out.println("OPEN_WEATHER_API_KEY: "+API_KEY);
        //System.out.println("   --- lon: " + lon + ", lat: " + lat);

        Gson gson = new Gson();
        //        String fileContent=Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json"));
        String fileContent = AuthorizationAccess.getFileContent("http://api.openweathermap.org/data/2.5/weather?lat="
                + lat + "&lon=" + lon + "&appid=" + API_KEY);

        //Product product = gson.fromJson(fileContent, Product.class);

        //System.out.println(".-.-.-.-.-.-: data: " + getFileContent("http://api.openweathermap.org/data/2.5/weather?lat=48.924569&lon=24.723712&appid=" + API_KEY));
//        System.out.println(".-.-.-.-.-.-: data: "+ Files.readString(Path.of("D:\\My\\Наука\\ПНУ\\Бакалаврська робота\\Test1\\upload-dir\\product.json")));

        //System.out.println("\n product: " + openWeather.getName() + " desc: " + openWeather.getWeather().get(0).getMain());
        //System.out.println("==== data: " + fileContent);

        //String description = openWeather.getWeather().get(0).getDescription();
        return gson.fromJson(fileContent, OpenWeather.class);
    }

    public Weather getWeather(OpenWeather openWeather) throws IOException {
        //        Double coef= Weather.values().equals()
        //Pair<Integer, String> pair = new Pair<>(1, "One");
        //Integer key = pair.getKey();
        //String value = pair.getValue();
//HashMap<Weather,Double> result=new HashMap<>();
        //Weather weatherResult;

        String description = openWeather.getWeather().get(0).getDescription();
        /*double coefficient = -1;
        for (Weather weather : Weather.values()) {
            if (weather.getDescription().equals(description)) {
                //coefficient = weather.getCoefficient();
                //pair=new Pair<>(weather,coefficient);
                //result.put(weather,coefficient);
                return weather;
                //break;
            }
        }*/

        try {
            //Weather weather = Weather.valueOf(getFormattedDescription(description));
            //yes
            System.out.println("--- yes ---");
            return Weather.valueOf(getFormattedDescription(description));
        } catch (IllegalArgumentException ex) {
            //nope
            Path filePath = Paths.get("system-information-files/descriptions.txt");

            String message = "Main: \"" + openWeather.getWeather().get(0).getMain() + "\", " +
                    "description: \"" + description + "\", " +
                    "dt: " + openWeather.getDt() + "\n";

            Files.writeString(filePath, message, StandardOpenOption.APPEND);
            //result.put(Weather.Other)
            //  }
            System.out.println("description: " + description + " coefficient: " + 0.01);
            System.out.println("--- no ---");
            return Weather.Other;
        }


        //if (coefficient == -1) {

        //    coefficient = 0.01D;
        //зберігати в файли інформацію про невідомі description

         /*   Path filePath = Paths.get("system-information-files/descriptions.txt");

            String message = "Main: \"" + openWeather.getWeather().get(0).getMain() + "\", " +
                    "description: \"" + description + "\", " +
                    "dt: " + openWeather.getDt() + "\n";

            Files.writeString(filePath, message, StandardOpenOption.APPEND);
            //result.put(Weather.Other)
      //  }
        System.out.println("description: " + description + " coefficient: " + coefficient);
        return Weather.Other;*/

        //return new HashMap<>(wea);
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
        /*Calendar calendar = new GregorianCalendar();
        calendar.setTime(solarPowerPlant.getStaticData().getInstallationDate());

        LocalDate date = LocalDate.now();

        int year = date.getYear(),
                month = date.getMonthValue(),
                day = date.getDayOfMonth();

        *//*year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();*//*



        day -= calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 0) month--;
        month -= (calendar.get(Calendar.MONTH) + 1);
        if (month < 0) year--;
        year -= calendar.get(Calendar.YEAR);

        return year;*/
        return AuthorizationAccess.getUsageTime(solarPowerPlant).get(0);
    }

    public String downloadData(HttpServletRequest request,
                               HttpServletResponse response,
                               String fileName) throws UnsupportedEncodingException {
        //String fileName = "f.csv";
        System.out.println("t-t-t-t-t-t-t filename: " + fileName);

        //String dataDirectory = request.getServletContext().getRealPath("upload-dir/");
        Path file = Paths.get("upload-dir/" + fileName);

        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        if (Files.exists(file)) {
            System.out.println("5-5-5-5-5-5-5 file: " + file.toString() + " file extension: " + getFileExtension(fileName));

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
        //System.out.println("JSON file created: " + jsonObject);
    }

    public Double getTotalPowerBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.getTotalPower(solarPowerPlant.getId());
    }

    public Double getTotalPowerForLastThirtyDaysBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        //System.out.println("day start: " + LocalDateTime.now().minusDays(30));
        //System.out.println("day finish: " + LocalDateTime.now());
        return dynamicDataRepository.getTotalPowerForLastThirtyDays(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                solarPowerPlant.getId());
    }

    public Double getAveragePowerPerDayBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        //System.out.println("day start: " + LocalDateTime.now().minusDays(30));
        //System.out.println("day finish: " + LocalDateTime.now());
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


    /*public String getFileContent(String filePath) throws IOException {


        ApplicationContext appContext =
                new ClassPathXmlApplicationContext(new String[]{});

        Resource resource = appContext.getResource(filePath);

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }*/

    /*private static List<String> readWeatherDescriptionsFromCSV(String fileName) {
        List<String> books = new ArrayList<>();
        Path pathToFile = Paths.get(fileName); // create an instance of BufferedReader // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            // read the first line from the text file
            String line = br.readLine(); // loop until all lines are read
            while (line != null) {
                // use string.split to load a string array with the values from // each line of // the file, using a comma as the delimiter
                //String[] attributes = line.split(",");
                //String weatherDescription = createBook(attributes); // adding book into ArrayList
                books.add(line); // read next line before looping // if end of file reached, line would be null
                line = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return books;
    }*/

    // public HashMap<Integer,Double> getDataByMonthAndSolarPowerPlant(SolarPowerPlant solarPowerPlant){
    public List<DataByPeriodAndSolarPowerPlant> getDataByMonthAndSolarPowerPlant(SolarPowerPlant solarPowerPlant) {

        return dynamicDataRepository.getDataByMonthAndSolarPowerPlant(solarPowerPlant.getId(),
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now());
    }

    public List<DataByPeriodAndSolarPowerPlant> getDataByHourAndSolarPowerPlant(SolarPowerPlant solarPowerPlant) {

        return dynamicDataRepository.getDataByHourAndSolarPowerPlant(solarPowerPlant.getId(),
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now());
    }

    public Optional<DynamicData> getFirstDynamicDataBySolarPowerPlant(SolarPowerPlant solarPowerPlant) {
        return dynamicDataRepository.findFirstBySolarPowerPlant(solarPowerPlant);
    }

    public void addTotalAndAveragePowerToModel(Model model, SolarPowerPlant solarPowerPlant) {
        Double totalPower = getTotalPowerBySolarPowerPlant(solarPowerPlant);
        //if (totalPower != null) model.addAttribute("totalPower", String.format("%,.2f", totalPower));
        //else model.addAttribute("totalPower", "Недостатньо даних.");

        model.addAttribute("totalPower", totalPower != null ? String.format("%,.2f", totalPower) : "Недостатньо даних.");

        Double totalPowerForLarThirtyDays = getTotalPowerForLastThirtyDaysBySolarPowerPlant(solarPowerPlant);

        model.addAttribute("totalPowerForLarThirtyDays", totalPowerForLarThirtyDays != null ? String.format("%,.2f", totalPowerForLarThirtyDays) : "Недостатньо даних.");


        //model.addAttribute("totalPowerForLarThirtyDays",
        //        String.format("%,.2f", dynamicDataService.getTotalPowerForLastThirtyDaysBySolarPowerPlant(solarPowerPlant.get())));
        //model.addAttribute("averagePowerForDay", "Недостатньо даних.");

        Double averagePowerForDay = getAveragePowerPerDayBySolarPowerPlant(solarPowerPlant);
        //model.addAttribute("averagePowerForDay",
        //      String.format("%,.2f", dynamicDataService.getAveragePowerPerDayBySolarPowerPlant(solarPowerPlant.get())));

        model.addAttribute("averagePowerForDay", averagePowerForDay != null ? String.format("%,.2f", averagePowerForDay) : "Недостатньо даних.");

        System.out.println(" using time: " + solarPowerPlantService.getStringOfUsageTime(solarPowerPlant));

        model.addAttribute("usingTime", solarPowerPlantService.getStringOfUsageTime(solarPowerPlant));
    }
}




