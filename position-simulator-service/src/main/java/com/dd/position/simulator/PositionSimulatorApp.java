package com.dd.position.simulator;


import com.dd.position.simulator.config.SwaggerConfig;
import com.dd.position.simulator.journey.JourneySimulator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SwaggerConfig.class)
public class PositionSimulatorApp {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(PositionSimulatorApp.class)) {
            final JourneySimulator simulator = ctx.getBean(JourneySimulator.class);

            Thread mainThread = new Thread(simulator);
            mainThread.start();
        }
    }
}
