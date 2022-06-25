package ru.azor.wdc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.azor.wdc.dao.CurrentWeatherRootDao;
import ru.azor.wdc.dao.ForecastWeatherRootDao;
import ru.azor.wdc.enums.ErrorValues;
import ru.azor.wdc.model.json.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MongoDBService {
    private final CurrentWeatherRootDao currentWeatherRootDao;
    private final ForecastWeatherRootDao forecastWeatherRootDao;

    public void saveCurrentWeatherToMongoDB(String json, String key) throws JsonProcessingException {
        if (currentWeatherRootDao.existsById(key) || json.equals(ErrorValues.NOT_FOUND.name())) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        CurrentWeatherRoot currentWeatherRoot = mapper.readValue(json, CurrentWeatherRoot.class);
        currentWeatherRoot.setKey(key);
        currentWeatherRootDao.save(currentWeatherRoot);
        log.info("Save to MongoDB: " + currentWeatherRoot);
    }

    public void saveForecastWeatherToMongoDB(ForecastWeatherRoot forecastWeatherRoot, String key) throws JsonProcessingException {
        if (forecastWeatherRootDao.existsById(key)) {
            log.error(key + " is already exists");
            return;
        }
        forecastWeatherRootDao.save(forecastWeatherRoot);
        log.info("Save to MongoDB: " + forecastWeatherRoot);
    }

    public ForecastWeatherRoot getForecastWeatherFromMongoDBAsObject(String id){
        return forecastWeatherRootDao.findById(id).orElseGet(() -> new ForecastWeatherRoot(ErrorValues.NOT_FOUND.name(),
                null, null, null));
    }

    public CurrentWeatherRoot getCurrentWeatherFromMongoDBAsObject(String id) {
        return currentWeatherRootDao.findById(id).orElseGet(() -> new CurrentWeatherRoot(ErrorValues.NOT_FOUND.name(), null, null));
    }

    public String getCurrentWeatherFromMongoDBAsString(String key) throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String weather = objectWriter.writeValueAsString(currentWeatherRootDao.findById(key).orElse(null));
        log.info("From MongoDB: " + weather);
        return weather.equals("null") ? ErrorValues.NOT_FOUND.name() : weather;
    }

    public String getCurrentWeatherFromMongoDBAsStringFormat(String key) {
        String formatWeather = formatCurrentWeatherString(currentWeatherRootDao.findById(key).orElse(null));
        log.info("From MongoDB: " + formatWeather);
        return formatWeather;
    }

    public List<String> getAllCitiesAndTimestamps() {
        List<String> weatherList = currentWeatherRootDao.findAll().stream().map(CurrentWeatherRoot::getKey).toList();
        log.info("From MongoDB list size: " + weatherList.size());
        return weatherList;
    }

    private String formatCurrentWeatherString(CurrentWeatherRoot root) {
        if (root == null) {
            return ErrorValues.NOT_FOUND.name();
        }
        CityLocation location = root.getLocation();
        CurrentWeather currentWeather = root.getCurrent();
        Condition condition = root.getCurrent().getCondition();
        String cityName = location.getName().toUpperCase();
        String countryName = location.getCountry();
        double lat = location.getLat();
        double lon = location.getLon();
        String time = currentWeather.getLastUpdated();
        String cond = condition.getText();
        double tempC = currentWeather.getTempC();
        double feelsLikeC = currentWeather.getFeelslikeC();
        long cloud = currentWeather.getCloud();
        double windSpeed = currentWeather.getWindKph();
        double windGust = currentWeather.getGustKph();
        String windDir = windDirectionFormatString(currentWeather.getWindDir());
        double pressure = currentWeather.getPressureMb() * 0.75f;
        double precipitation = currentWeather.getPrecipMm();
        long humidity = currentWeather.getHumidity();
        double uv = currentWeather.getUv();
        double visKm = currentWeather.getVisKm();
        return String
                .format("""
                                %s
                                %s
                                Latitude: %.2f, Longitude: %.2f
                                Localtime: %s
                                %s, Temp.: %.1f Feels like: %.1f
                                Cloud: %d
                                Wind speed: %.1f km/h Gusts: %.1f km/h
                                Wind direction: %s
                                Pressure: %.0f mmHg, Precipitation: %.0f mm
                                Humidity: %d %%
                                UV: %.1f Visibility: %.1f km""",
                        cityName, countryName, lat, lon, time, cond, tempC, feelsLikeC, cloud, windSpeed,
                        windGust, windDir, pressure, precipitation, humidity, uv, visKm);
    }

    private String windDirectionFormatString(String windDir) {
        List<String> directions = new ArrayList<>();
        for (int i = 0; i < windDir.length(); i++) {
            switch (windDir.charAt(i)) {
                case 'N' -> directions.add("North");
                case 'S' -> directions.add("South");
                case 'E' -> directions.add("East");
                case 'W' -> directions.add("West");
            }
        }
        return String.join("-", directions);
    }

    public boolean deleteWeatherById(String id) {
        currentWeatherRootDao.deleteById(id);
        if (currentWeatherRootDao.existsById(id)){
            log.error(id + " FAILED DELETION");
            return false;
        }
        log.info(id + " deleted");
        return true;
    }
}
