package ru.azor.wdc.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDay {
    private String date;
    @JsonProperty("date_epoch")
    private Long dateEpoch;
    private Day day;
    private Astro astro;
    private List<Hour> hour;
}
