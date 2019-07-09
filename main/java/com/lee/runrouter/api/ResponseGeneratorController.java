package com.lee.runrouter.api;


import com.lee.runrouter.api.exceptions.InvalidCoordsException;
import com.lee.runrouter.api.exceptions.InvalidDistanceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller class receives the HTTP request, checks if it is
 * of the correct form, then passes on the variables to the
 * relevant classes.
 */
@RestController
public class ResponseGeneratorController {
    final double MAX_LAT = 52.7;
    final double MIN_LAT = 50;
    final double MAX_LON = 2;
    final double MIN_LON = -6;
    final double MIN_RUN_LENGTH_KM = 0.5;
    final double MAX_RUN_LENGTH_KM = 21;

    @GetMapping(path = "/route/coords=({lat},{lon}),length={length}")
    // check inputs are within possile range of lat lon values
    // if not return 404 bad request
    // create excpetion response class
    public double receiveArgs(@PathVariable double lat, @PathVariable double lon,
                             @PathVariable double distance) {
        if (!checkCoordInput(lat, lon)) {
            throw new InvalidCoordsException(String.format("Lat: %s Lon: %s", lat, lon));
        }
        if (!checkLengthInput(distance)) {
            throw new InvalidDistanceException(String.format("Length: %s", distance));
        }

        return (lat + lon) * distance;
    }

    @GetMapping(path = "/test/{args}")
    // check inputs are within possile range of lat lon values
    // if not return 404 bad request
    // create excpetion response class
    public List<Boolean> test(@PathVariable List<Boolean> args) {

        return args;
    }

    private boolean checkCoordInput(double lat, double lon) {
        if (lat > MAX_LAT || lat < MIN_LAT) {
            return false;
        }

        if (lon > MAX_LON || lon < MIN_LON) {
            return false;
        }
        return true;
    }

    private boolean checkLengthInput(double length){
        return length >= MIN_RUN_LENGTH_KM &&
                length <= MAX_RUN_LENGTH_KM;
    }
}
