package ru.azor.wdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WeatherDataCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherDataCollectorApplication.class, args);
    }

}
