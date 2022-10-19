package com.dd.position.simulator.journey;

import com.dd.position.simulator.publisher.PositionPublisher;
import com.dd.position.simulator.utils.PropertyLoaderUtility;
import com.dd.position.simulator.utils.VehicleNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.dd.position.simulator.utils.PropertyLoaderUtility.getInstance;

@Component
@Slf4j
public class JourneySimulator implements Runnable {

    private ExecutorService threadPool;

    public JourneySimulator() {
    }


    @Override
    public void run() {
        try {
            this.runVehicleSimulation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runVehicleSimulation() throws InterruptedException {
        Map<String, List<String>> reports = setUpData();
        threadPool = Executors.newCachedThreadPool();
        boolean stillRunning = true;

        while (stillRunning) {

            List<Callable<Object>> calls = new ArrayList<>();

            for (String vehicleName : reports.keySet()) {
                calls.add(new Journey(new PositionPublisher(), reports.get(vehicleName), vehicleName));
            }
            threadPool.invokeAll(calls);
            if (threadPool.isShutdown()) {
                stillRunning = false;
            }
        }
    }

    /**
     * Read the data from the resources' directory - should work for an executable Jar as
     * well as through direct execution
     */
    private Map<String, List<String>> setUpData() {
        Map<String, List<String>> reports = new HashMap<>();
        PathMatchingResourcePatternResolver path = new PathMatchingResourcePatternResolver();

        PropertyLoaderUtility propertyLoader = getInstance();
        try {
            for (Resource nextFile : path.getResources("tracks/*")) {
                URL resource = nextFile.getURL();
                File f = new File(resource.getFile());
                String vehicleName = VehicleNameUtils.prettifyName(f.getName());
                Optional<List<String>> vehicleReports = propertyLoader.getVehiclePositions("/tracks/" + f.getName());

                vehicleReports.ifPresent(value -> {
                    reports.put(vehicleName, value);
                });
            }
            return Collections.unmodifiableMap(reports);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        threadPool.shutdown();
    }
}
