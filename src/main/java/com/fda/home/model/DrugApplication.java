package com.fda.home.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DrugApplication {
    @JsonProperty("application_number")
    private String applicationNumber;

    @JsonProperty("manufacturer_names")
    private List<String> manufacturerNames;

    @JsonProperty("substance_names")
    private List<String> substanceNames;

    @JsonProperty("product_numbers")
    private List<String> productNumbers;
}
