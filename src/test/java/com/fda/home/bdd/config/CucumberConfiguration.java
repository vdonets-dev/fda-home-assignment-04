package com.fda.home.bdd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.util.Map;

@AllArgsConstructor
public class CucumberConfiguration {

    private final ObjectMapper objectMapper;

    @DefaultDataTableEntryTransformer
    public Object defaultDataTableEntry(Map<String, String> entry, Type type) {
        return objectMapper.convertValue(entry, objectMapper.constructType(type));
    }
}
