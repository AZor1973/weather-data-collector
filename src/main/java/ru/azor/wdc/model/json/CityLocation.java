package ru.azor.wdc.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityLocation {
    private String name;
    private String region;
    private String country;
    private Double lat;
    private Double lon;
    @JsonProperty("tz_id")
    private String tzId;
    @JsonProperty("localtime_epoch")
    private Long localtimeEpoch;
    private String localtime;
}
//    Field	        Data Type	    Description
//    lat	           decimal	Latitude in decimal degree
//    lon	           decimal	Longitude in decimal degree
//    name	           string	Location name
//    region           string	Region or state of the location, if availa
//    country          string	Location country
//    tz_id	           string	Time zone name
//    localtime_epoch  int	    Local date and time in unix time
//    localtime	       string	Local date and time