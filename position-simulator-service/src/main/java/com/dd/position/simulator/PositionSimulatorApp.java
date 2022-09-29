package com.dd.position.simulator;


import com.dd.position.simulator.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SwaggerConfig.class)
public class PositionSimulatorApp {
    public static void main(String[] args) {
        SpringApplication.run(PositionSimulatorApp.class, args);
    }
}
