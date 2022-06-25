package ru.azor.wdc.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.azor.wdc.model.json.ForecastWeatherRoot;

public interface ForecastWeatherRootDao extends MongoRepository<ForecastWeatherRoot, String> {
}
