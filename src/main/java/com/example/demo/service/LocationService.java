package com.example.demo.service;

import com.example.demo.model.Location;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocationService {

    public String getFileContent(String filePath) throws IOException {
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
    }

    public Location createLonLatCoordinates(Location location) throws IOException {
        final String KEY = "g1CgD1eTytaXG7ubOigQK4bB9QyVSr92";
        final String OUT_FORMAT = "json";
        final int MAX_RESULTS = 1;
        String filePathFirstPart = "http://www.mapquestapi.com/geocoding/v1/address?key=" + KEY +
                "&outFormat=" + OUT_FORMAT + "&maxResults=" + MAX_RESULTS + "&location=";
        String filePathSecondPart= UriUtils.encodePath(location.getStreet() +" "+location.getNumber() +"," +
                location.getCity() + "," +location.getRegion().getName()+" область,"+ location.getCountry(), "UTF-8");
       String filePath=filePathFirstPart+filePathSecondPart;

        System.out.println(" --> filePath: " + filePath);
        String geocodingData = getFileContent(filePath);
        System.out.println("geocodingData: " + geocodingData);

        Pattern pattern = Pattern
                .compile("\"lat\":([0-9\\.-]+),\"lng\":([0-9\\.-]+)");

        Matcher matcher = pattern.matcher(geocodingData);
        if (matcher.find()) {
            System.out.println(" >> lat: " + matcher.group(1) + " - lon: " + matcher.group(2));
            location.setLongitude(Double.parseDouble(matcher.group(1)));
            location.setLatitude(Double.parseDouble(matcher.group(2)));
        } else {
            System.out.println("Not found: " + pattern.toString());
        }
        //return (matcher.find()) ? matcher.group(idGroup) : "-1";

        return location;
    }

}
