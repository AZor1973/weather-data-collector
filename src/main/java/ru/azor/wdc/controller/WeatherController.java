package ru.azor.wdc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.azor.wdc.enums.ErrorValues;
import ru.azor.wdc.model.dto.ResponseDto;
import ru.azor.wdc.model.json.CurrentWeatherRoot;
import ru.azor.wdc.model.json.ForecastWeatherRoot;
import ru.azor.wdc.service.ActiveMQArtemisService;
import ru.azor.wdc.service.ForecastWeatherService;
import ru.azor.wdc.service.MongoDBService;
import ru.azor.wdc.service.CurrentWeatherService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class WeatherController {
    private final MongoDBService mongoDBService;
    private final CurrentWeatherService currentWeatherService;
    private final ForecastWeatherService forecastWeatherService;
    private final ActiveMQArtemisService artemisService;

    @GetMapping("/city")
    public ResponseEntity<?> getCurrentCityName() {
        String city = currentWeatherService.getCityName() == null ? CurrentWeatherService.DEFAULT_CITY_NAME.toUpperCase()
                : currentWeatherService.getCityName().toUpperCase();
        ResponseDto responseDto = new ResponseDto(city, null);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/city")
    public ResponseEntity<?> setCurrentCityName(@RequestBody String newCity) throws IOException {
        ResponseDto response = new ResponseDto();
        if (currentWeatherService.getCurrentWeatherResponseFromGetRequest(newCity).equals(ErrorValues.NOT_FOUND.name())) {
            log.error("Invalid received city name");
            response.setError(ErrorValues.NOT_FOUND.name());
        } else {
            currentWeatherService.setCityName(newCity);
            artemisService.sendCityNameToActiveMQ();
            currentWeatherService.getCurrentWeatherAsScheduled();
            log.info("Received: " + newCity);
        }
        String city = currentWeatherService.getCityName() == null ? CurrentWeatherService.DEFAULT_CITY_NAME.toUpperCase()
                : currentWeatherService.getCityName().toUpperCase();
        response.setResponse(city);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWeatherFromMongoDBById(@PathVariable String id) {
        CurrentWeatherRoot weather = mongoDBService.getCurrentWeatherFromMongoDBAsObject(id);
        return new ResponseEntity<>(weather, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWeatherFromMongoDBById(@PathVariable String id) {
        ResponseDto response = new ResponseDto();
        if (mongoDBService.deleteWeatherById(id)){
            response.setResponse(id + " DELETED");
        }else {
            response.setError("FAILED DELETION " + id);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all_saved")
    public ResponseEntity<?> getAllSavedCurrentWeatherFromMongoDB() {
        return new ResponseEntity<>(mongoDBService.getAllCitiesAndTimestamps(), HttpStatus.OK);
    }

    @GetMapping("/forecast/{city}")
    public ResponseEntity<?> getForecast(@PathVariable String city) throws JsonProcessingException {
        ForecastWeatherRoot forecast = forecastWeatherService.getForecastWeatherAsObject(city);
        return new ResponseEntity<>(forecast, HttpStatus.OK);
    }

//    @GetMapping("/{savedCity}")
//    public ResponseEntity<?> getAllSavedCurrentWeatherByCityNameFromRedis(@PathVariable String savedCity) {
//        return new ResponseEntity<>(redisService.getAllWeatherByCityNameFromRedis(savedCity), HttpStatus.OK);
//    }

//    @GetMapping("/all_saved/{dateTime}")
//    public ResponseEntity<?> getAllSavedCurrentWeatherByDateTimeFromRedis(@PathVariable String dateTime){
//        return new ResponseEntity<>(redisService.getAllWeatherByDateTimeFromRedis(dateTime), HttpStatus.OK);
//    }

//    @GetMapping("/cities_dates")
//    public ResponseEntity<?> getAllSavedCitiesAndTimestampsFromRedis(){
//        return new ResponseEntity<>(redisService.getAllCitiesAndTimestampsFromRedis(), HttpStatus.OK);
//    }
}
