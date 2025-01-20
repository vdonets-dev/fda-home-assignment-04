package com.fda.home.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ParameterResolver {

    /**
     * Converts a JSON-like array string into a list of strings.
     * Example: ["Pfizer", "Hikma"] -> List<String> ["Pfizer", "Hikma"]
     * Removes brackets, double quotes, and trims the values.
     */
    public List<String> csvString(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return List.of();
        }
        return Arrays.stream(commaSeparated.replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .map(s -> s.replace("\"", ""))
                .collect(Collectors.toList());
    }
}
