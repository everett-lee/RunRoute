package com.lee.runrouter.api;


import com.lee.runrouter.algorithm.cyclegenerator.PathNotGeneratedException;
import com.lee.runrouter.api.exceptions.PathGenerationFailureException;
import com.lee.runrouter.graph.graphbuilder.node.Node;
import com.lee.runrouter.api.exceptions.InvalidCoordsException;
import com.lee.runrouter.api.exceptions.InvalidDistanceException;
import com.lee.runrouter.executor.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller class receives the HTTP request, checks if it is
 * of the correct form, then passes on the variables to the
 * executor class.
 */
@RestController
public class ResponseGeneratorController {
    final double MAX_LAT = 52.7;
    final double MIN_LAT = 50;
    final double MAX_LON = 2;
    final double MIN_LON = -6;
    final double MIN_RUN_LENGTH_M = 500;
    final double MAX_RUN_LENGTH_M = 21000;
    private Executor executor;

    @Autowired
    public ResponseGeneratorController(
            @Qualifier("ExecutorMain") Executor executor) {
     this.executor = executor;
    }


    @GetMapping(path = "/route/coords=({lat},{lon}),distance={distance}")
    public List<Node> receiveArgs(@PathVariable double lat, @PathVariable double lon,
                             @PathVariable double distance) {
        if (!checkCoordInput(lat, lon)) {
            throw new InvalidCoordsException(String.format("Coordinates: (%s,%s)", lat, lon));
        }
        if (!checkLengthInput(distance)) {
            throw new InvalidDistanceException(String.format("Distance: %s", distance));
        }

        double[] coords = {lat, lon};
        boolean[] opts = {true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true};

        try {
            return executor.executeQuery(coords, distance, opts);
        } catch (PathNotGeneratedException e) {
            throw new PathGenerationFailureException(String.format("Coordinates: (%s,%s), Distance: %s",
                    lat, lon, distance));
            // actually throw server error here
        }
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
        return length >= MIN_RUN_LENGTH_M &&
                length <= MAX_RUN_LENGTH_M;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
