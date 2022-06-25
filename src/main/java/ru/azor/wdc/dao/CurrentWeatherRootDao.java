package ru.azor.wdc.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.azor.wdc.model.json.CurrentWeatherRoot;

public interface CurrentWeatherRootDao extends MongoRepository<CurrentWeatherRoot, String> {
}