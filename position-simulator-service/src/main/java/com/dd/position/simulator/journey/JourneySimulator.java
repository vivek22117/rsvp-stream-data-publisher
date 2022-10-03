package com.dd.position.simulator.journey;

import com.dd.position.simulator.utils.PropertyLoaderUtility;
import com.dd.position.simulator.utils.VehicleNameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

import static com.dd.position.simulator.utils.PropertyLoaderUtility.getInstance;

public class JourneySimulator implements Runnable {

    private final ThreadPoolTaskExecutor threadPool;

    public JourneySimulator(ThreadPoolTaskExecutor threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        this.runVehicleSimulation();
    }

    private void runVehicleSimulation() {
        Map<String, List<String>> reports = new HashMap<>();
        boolean stillRunning = true;
        while (stillRunning) {
            List<Callable<Object>> calls = new ArrayList<>();

            for (String vehicleName : reports.keySet()) {
                // kick off a message sending thread for this vehicle.
            }
        }
    }

    /**
     * Read the data from the resources directory - should work for an executable Jar as
     * well as through direct execution
     */
    private Map<String, List<String>> setUpData() {
        Map<String, List<String>> reports = new HashMap<>();
        PathMatchingResourcePatternResolver path = new PathMatchingResourcePatternResolver();

        try {
            for (Resource nextFile : path.getResources("tracks/*")) {
                URL resource = nextFile.getURL();
                File f = new File(resource.getFile());
                String vehicleName = VehicleNameUtils.prettifyName(f.getName());
                Optional<List<String>> vehicleReports = getInstance().getVehiclePositions("/tracks/" + f.getName());

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
