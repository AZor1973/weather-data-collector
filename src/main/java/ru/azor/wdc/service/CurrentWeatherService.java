package ru.azor.wdc.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.wdc.enums.ErrorValues;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class CurrentWeatherService {
    private final WebClient webClientCurrentWeather;
    private final RedisService redisService;
    private final MongoDBService mongoDbService;
    private static final Map<String, String> savedWeather;
    public static final String DEFAULT_CITY_NAME = "tonkino";
    @Value("${api-key}")
    private String API_KEY;
    private String cityName;

    static {
        savedWeather = new HashMap<>();
    }


    public String getCurrentWeatherJsonString(String city) throws IOException {
        String key = getKeyByCityName(city);
        if (savedWeather.containsKey(key)) {
            log.info("From Map Weather Service: " + savedWeather.get(key));
            return savedWeather.get(key);
        }
        String json = redisService.getCurrentWeatherFromRedis(key);
        if (!json.equals(ErrorValues.NOT_FOUND.name())) {
            return json;
        }
        json = mongoDbService.getCurrentWeatherFromMongoDBAsString(key);
        if (!json.equals(ErrorValues.NOT_FOUND.name())) {
            redisService.setCurrentWeatherToRedis(key, json);
            return json;
        }
        json = getCurrentWeatherResponseFromGetRequest(city);
        if (! json.equals(ErrorValues.NOT_FOUND.name())){
            savedWeather.put(key, json);
            redisService.setCurrentWeatherToRedis(key, json);
            mongoDbService.saveCurrentWeatherToMongoDB(json, key);
        }
        return json;
    }

    @Scheduled(cron = "${get-current-weather-cron}")
    @Async
    public void getCurrentWeatherAsScheduled() throws IOException {
        String city = getCityName() == null ? DEFAULT_CITY_NAME : getCityName();
        String key = getKeyByCityName(city);
        String json;
        if (savedWeather.containsKey(key)) {
            json = savedWeather.get(key);
        } else {
            json = redisService.getCurrentWeatherFromRedis(key);
            if (json.equals(ErrorValues.NOT_FOUND.name())) {
                json = mongoDbService.getCurrentWeatherFromMongoDBAsString(key);
            }
            if (json.equals(ErrorValues.NOT_FOUND.name())) {
                json = getCurrentWeatherResponseFromGetRequest(city);
            }
            if (! json.equals(ErrorValues.NOT_FOUND.name())) {
                savedWeather.put(key, json);
            }
        }
        if (! json.equals(ErrorValues.NOT_FOUND.name())) {
            redisService.setCurrentWeatherToRedis(key, json);
            mongoDbService.saveCurrentWeatherToMongoDB(json, key);
        }
    }

    public String getCurrentWeatherResponseFromGetRequest(String city) {
        try {
            return webClientCurrentWeather
                    .get()
                    .uri("?key=" + API_KEY + "&q=" + city)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ErrorValues.NOT_FOUND.name();
        }
    }

    private String getKeyByCityName(String city) {
        city = city.toUpperCase();
        return city + ": " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:00"));
    }
}
