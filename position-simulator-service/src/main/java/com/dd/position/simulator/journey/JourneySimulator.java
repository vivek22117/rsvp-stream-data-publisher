package com.dd.position.simulator.journey;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
        while (stillRunning)
        {
            List<Callable<Object>> calls = new ArrayList<>();

            for (String vehicleName : reports.keySet())
            {
                // kick off a message sending thread for this vehicle.
            }
        }
    }
}
