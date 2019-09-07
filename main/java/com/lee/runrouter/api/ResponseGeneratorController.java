package com.lee.runrouter.api;

import com.lee.runrouter.api.exceptions.PathGenerationFailureException;
import com.lee.runrouter.executor.ResponseObject;
import com.lee.runrouter.api.exceptions.InvalidCoordsException;
import com.lee.runrouter.api.exceptions.InvalidDistanceException;
import com.lee.runrouter.executor.Executor;

import com.lee.runrouter.routegenerator.PathNotGeneratedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * This controller class receives the GET request, checks if it is
 * of the correct form, then passes on the received
 * variables to the executor class.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ResponseGeneratorController {
    final double MAX_LAT = 52.7;
    final double MIN_LAT = 50;
    final double MAX_LON = 2;
    final double MIN_LON = -6;
    final double MIN_RUN_LENGTH_M = 2000;
    final double MAX_RUN_LENGTH_M = 21000;
    private Executor executor;

    @Autowired
    public ResponseGeneratorController(
            @Qualifier("ExecutorMain") Executor executor) {
     this.executor = executor;
    }

    // endpoint for the route query. Receives the route parameters and passes them to the
    // Executor class
    @GetMapping(value="route/args")
    public ResponseEntity<ResponseObject> receiveArgs(@RequestParam("lat") double lat, @RequestParam("lon") double lon,
                                                      @RequestParam("distance") double distance, @RequestParam("maxGradient") double maxGradient,
                                                      @RequestParam("options") boolean[] options) {

        // Coordinates outside of accepted range
        if (!checkCoordInput(lat, lon)) {
            throw new InvalidCoordsException(String.format("Coordinates: (%s,%s)", lat, lon));
        }

        // Distance outside of accepted range
        if (!checkLengthInput(distance)) {
            throw new InvalidDistanceException(String.format("Distance: %s", distance));
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        System.out.println("TODO : remove crossorigin");

        double[] coords = {lat, lon};

        try {
            ResponseObject result = executor.executeQuery(coords, maxGradient, distance, options);
            return new ResponseEntity<>(result, responseHeaders, HttpStatus.CREATED);

        // a suitable path could not be generated
        } catch (PathNotGeneratedException e) {
            throw new PathGenerationFailureException(String.format("Coordinates: (%s,%s), Distance: %s",
                    lat, lon, distance));
        }
    }


    // intialise the graph construction when the starting coordinates are received.
    // does not return anything until the route parameters are sent
    @GetMapping(value="start/args")
    public void receiveStartCoords(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        // Coordinates outside of accepted range
        if (!checkCoordInput(lat, lon)) {
            throw new InvalidCoordsException(String.format("Coordinates: (%s,%s)", lat, lon));
        }

        double[] coords = {lat, lon};

        executor.executeInitialGraphBuild(coords);
    }

    // method for checking lower and upper bounds of coordinate
    // parameters
    private boolean checkCoordInput(double lat, double lon) {
        if (lat > MAX_LAT || lat < MIN_LAT) {
            return false;
        }

        if (lon > MAX_LON || lon < MIN_LON) {
            return false;
        }
        return true;
    }


    // method for checking lower and upper bounds of distance
    // parameter
    private boolean checkLengthInput(double length){
        return length >= MIN_RUN_LENGTH_M &&
                length <= MAX_RUN_LENGTH_M;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
