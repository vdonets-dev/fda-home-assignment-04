package com.fda.home.util;

import com.fda.home.model.DrugApplication;

import java.util.Map;

public class DataTableTransformers {

    public static DrugApplication toDrugApplication(Map<String, String> row, ParameterResolver parameterResolver) {
        return new DrugApplication(
                row.get("application_number"),
                parameterResolver.csvString(row.get("manufacturer_names")),
                parameterResolver.csvString(row.get("substance_names")),
                parameterResolver.csvString(row.get("product_numbers"))
        );
    }
}
