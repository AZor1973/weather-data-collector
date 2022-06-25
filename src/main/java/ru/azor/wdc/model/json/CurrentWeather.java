package ru.azor.wdc.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrentWeather {
    @JsonProperty("last_updated_epoch")
    private Long lastUpdatedEpoch;
    @JsonProperty("last_updated")
    private String lastUpdated;
    @JsonProperty("temp_c")
    private Double tempC;
    @JsonProperty("temp_f")
    private Double tempF;
    @JsonProperty("is_day")
    private Long isDay;
    private Condition condition;
    @JsonProperty("wind_mph")
    private Double windMph;
    @JsonProperty("wind_kph")
    private Double windKph;
    @JsonProperty("wind_degree")
    private Long windDegree;
    @JsonProperty("wind_dir")
    private String windDir;
    @JsonProperty("pressure_mb")
    private Double pressureMb;
    @JsonProperty("pressure_in")
    private Double pressureIn;
    @JsonProperty("precip_mm")
    private Double precipMm;
    @JsonProperty("precip_in")
    private Double precipIn;
    private Long humidity;
    private Long cloud;
    @JsonProperty("feelslike_c")
    private Double feelslikeC;
    @JsonProperty("feelslike_f")
    private Double feelslikeF;
    @JsonProperty("vis_km")
    private Double visKm;
    @JsonProperty("vis_miles")
    private Double visMiles;
    private Double uv;
    @JsonProperty("gust_mph")
    private Double gustMph;
    @JsonProperty("gust_kph")
    private Double gustKph;
}

//        Field	              Data Type	                Description
//        last_updated	        string	   Local time when the real time
//                                         data was updated.
//        last_updated_epoch	int	       Local time when the real time
//                                         data was updated in unix time.
//        temp_c	            decimal	   Temperature in celsius
//        temp_f	            decimal	   Temperature in fahrenheit
//        feelslike_c	        decimal	   Feels like temperature in celsius
//        feelslike_f	        decimal	   Feels like temperature in fahrenheit
//        condition:text	    string	   Weather condition text
//        condition:icon	    string	   Weather icon url
//        condition:code	    int	       Weather condition unique code.
//        wind_mph	            decimal	   Wind speed in miles per hour
//        wind_kph	            decimal	   Wind speed in kilometer per hour
//        wind_degree	        int	       Wind direction in degrees
//        wind_dir	            string	   Wind direction as 16 point compass. e.g.: NSW
//        pressure_mb	        decimal	   Pressure in millibars
//        pressure_in	        decimal	   Pressure in inches
//        precip_mm	            decimal	   Precipitation amount in millimeters
//        precip_in	            decimal	   Precipitation amount in inches
//        humidity	            int	       Humidity as percentage
//        cloud	                int	       Cloud cover as percentage
//        is_day	            int	       1 = Yes 0 = No
//                                         Whether to show day condition icon or
//                                         night icon
//        uv	                decimal	   UV Index
//        gust_mph	            decimal	   Wind gust in miles per hour
//        gust_kph	            decimal	   Wind gust in kilometer per hour