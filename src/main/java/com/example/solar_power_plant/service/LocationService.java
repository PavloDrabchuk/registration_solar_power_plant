package com.example.solar_power_plant.service;

import com.example.solar_power_plant.AuthorizationAccess;
import com.example.solar_power_plant.model.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LocationService {

    @Value("${MAP_QUEST_API_KEY}")
    private String MAP_QUEST_API_KEY;

    public Location createLonLatCoordinates(Location location) throws IOException {
        final String OUT_FORMAT = "json";
        final int MAX_RESULTS = 1;

        String filePathFirstPart = "http://www.mapquestapi.com/geocoding/v1/address?key=" + MAP_QUEST_API_KEY +
                "&outFormat=" + OUT_FORMAT + "&maxResults=" + MAX_RESULTS + "&location=";

        String filePathSecondPart = UriUtils.encodePath(location.getStreet() + " " + location.getNumber() + "," +
                location.getCity() + "," + location.getRegion().getName() + " область," + location.getCountry(), "UTF-8");

        String filePath = filePathFirstPart + filePathSecondPart;
        String geocodingData = AuthorizationAccess.getFileContent(filePath);

        Pattern pattern = Pattern
                .compile("\"lat\":([0-9\\.-]+),\"lng\":([0-9\\.-]+)");

        Matcher matcher = pattern.matcher(geocodingData);

        if (matcher.find()) {
            location.setLongitude(Double.parseDouble(matcher.group(2)));
            location.setLatitude(Double.parseDouble(matcher.group(1)));
        } else {
            location.setLongitude(-1D);
            location.setLatitude(-1D);
        }

        return location;
    }

}
