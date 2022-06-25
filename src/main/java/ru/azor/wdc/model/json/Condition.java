package ru.azor.wdc.model.json;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Condition {
    private String text;
    private String icon;
    private Long code;
}
//  text	    string	   Weather condition text
//  icon	    string	   Weather icon url
//  code	    int	       Weather condition unique code.