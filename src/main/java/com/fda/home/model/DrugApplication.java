package com.fda.home.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DrugApplication {
    private String applicationNumber;
    private List<String> manufacturerNames;
    private List<String> substanceNames;
    private List<String> productNumbers;
}
