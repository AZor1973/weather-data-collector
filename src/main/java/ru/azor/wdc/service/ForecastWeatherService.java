package ru.azor.wdc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.azor.wdc.enums.ErrorValues;
import ru.azor.wdc.model.json.ForecastDay;
import ru.azor.wdc.model.json.ForecastWeather;
import ru.azor.wdc.model.json.ForecastWeatherRoot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class ForecastWeatherService {
    private final WebClient webClientForecastWeather;
    private final MongoDBService mongoDBService;
    private static final Map<String, ForecastWeatherRoot> savedForecast;
    @Value("${api-key}")
    private String API_KEY;
    @Value("${forecast-days}")
    private Integer days;
    private String cityName;

    static {
        savedForecast = new HashMap<>();
    }

    public String getForecastWeatherResponseFromGetRequest(String city) {
        try {
            return webClientForecastWeather
                    .get()
                    .uri("?key=" + API_KEY + "&q=" + city + "&days=" + days)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ErrorValues.NOT_FOUND.name();
        }
    }

    public ForecastWeatherRoot getForecastWeatherAsObject(String city) throws JsonProcessingException {
        String key = getKeyByCityName(city);
        ForecastWeatherRoot forecast;
        if (savedForecast.containsKey(key)) {
            forecast = savedForecast.get(key);
            log.info("From savedForecast: " + forecast);
            return forecast;
        }
        forecast = mongoDBService.getForecastWeatherFromMongoDBAsObject(key);
        if (!forecast.getKey().equals(ErrorValues.NOT_FOUND.name())) {
            log.info("From MongoDB: " + forecast);
            return forecast;
        }
        String json = getForecastWeatherResponseFromGetRequest(city);
        if (json.equals(ErrorValues.NOT_FOUND.name())) {
            return new ForecastWeatherRoot(null,
                    null, null,
                    new ForecastWeather(List.of(new ForecastDay(ErrorValues.NOT_FOUND.name(),
                            null, null, null, null))));
        }
        ObjectMapper mapper = new ObjectMapper();
        forecast = mapper.readValue(json, ForecastWeatherRoot.class);
        log.info("Received forecast: " + forecast.getLocation().getName() +
                " " + forecast.getForecast().getForecastday().size() + " days");
        forecast.setKey(key);
        savedForecast.put(key, forecast);
        mongoDBService.saveForecastWeatherToMongoDB(forecast, key);
        return forecast;
    }

    private String getKeyByCityName(String city) {
        city = city.toUpperCase();
        return city + ": " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
