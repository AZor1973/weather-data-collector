package ru.azor.wdc.model.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CurrentWeatherRoot {
    @Id
    @JsonIgnore
    private String key;
    private CityLocation location;
    private CurrentWeather current;
}
