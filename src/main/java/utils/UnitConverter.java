package utils;

import model.UnitType;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {

    private static final Map<String, Map<UnitType, Map<UnitType, Double>>> CONVERSIONS = new HashMap<>();

    private static void add(String ingredient, UnitType from, UnitType to, double factor) {
        CONVERSIONS
                .computeIfAbsent(ingredient, k -> new HashMap<>())
                .computeIfAbsent(from, k -> new HashMap<>())
                .put(to, factor);
    }

    public static Double convertTo(String ingredientName, UnitType from, UnitType to, Double value) {
        if (from == to) return value;

        ingredientName = ingredientName.trim();

        Map<UnitType, Map<UnitType, Double>> ingredientMap = CONVERSIONS.get(ingredientName);
        if (ingredientMap == null)
            throw new RuntimeException("Unknown ingredient: " + ingredientName);

        Map<UnitType, Double> fromMap = ingredientMap.get(from);
        if (fromMap == null || !fromMap.containsKey(to))
            throw new RuntimeException("Cannot convert from " + from + " to " + to);

        return value * fromMap.get(to);
    }
}