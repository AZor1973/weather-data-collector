package ru.azor.wdc.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Day {
    @JsonProperty("maxtemp_c")
    private Double maxtempC;
    @JsonProperty("maxtemp_f")
    private Double maxtempF;
    @JsonProperty("mintemp_c")
    private Double mintempC;
    @JsonProperty("mintemp_f")
    private Double mintempF;
    @JsonProperty("avgtemp_c")
    private Double avgtempC;
    @JsonProperty("avgtemp_f")
    private Double avgtempF;
    @JsonProperty("maxwind_mph")
    private Double maxwindMph;
    @JsonProperty("maxwind_kph")
    private Double maxwindKph;
    @JsonProperty("totalprecip_mm")
    private Double totalprecipMm;
    @JsonProperty("totalprecip_in")
    private Double totalprecipIn;
    @JsonProperty("avgvis_km")
    private Double avgvisKm;
    @JsonProperty("avgvis_miles")
    private Double avgvisMiles;
    @JsonProperty("avghumidity")
    private Double avgHumidity;
    @JsonProperty("daily_will_it_rain")
    private Long dailyWillItRain;
    @JsonProperty("daily_chance_of_rain")
    private Long dailyChanceOfRain;
    @JsonProperty("daily_will_it_snow")
    private Long dailyWillItSnow;
    @JsonProperty("daily_chance_of_snow")
    private Long dailyChanceOfSnow;
    private Condition condition;
    private Double uv;
}
