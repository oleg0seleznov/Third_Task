package by.developer.airport.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class AirportDataLoader {
    private static final String DATA_FILE = "/airport-data.properties";

    private AirportDataLoader() {
    }

    public static AirportConfiguration load() {
        try (InputStream input = AirportDataLoader.class.getResourceAsStream(DATA_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Airport data file was not found: " + DATA_FILE);
            }
            Properties properties = new Properties();
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));

            int flightCount = parseInt(properties, "flightCount");
            List<FlightData> flights = new ArrayList<>(flightCount);
            for (int index = 1; index <= flightCount; index++) {
                String[] fields = required(properties, "flight." + index).split(",", -1);
                if (fields.length != 3) {
                    throw new IllegalArgumentException("Invalid flight data at flight." + index);
                }
                flights.add(new FlightData(
                        fields[0].trim(),
                        parseInt(fields[1].trim(), "capacity for flight." + index),
                        parseInt(fields[2].trim(), "passengers for flight." + index),
                        parseInt(required(properties, "boarding." + index),
                                "boarding passengers for flight." + index)));
            }
            return new AirportConfiguration(
                    parseInt(properties, "gateCount"),
                    parseInt(properties, "terminalCapacity"),
                    flights);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read airport data file: " + DATA_FILE, exception);
        }
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing airport property: " + key);
        }
        return value.trim();
    }

    private static int parseInt(Properties properties, String key) {
        return parseInt(required(properties, key), key);
    }

    private static int parseInt(String value, String key) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer for airport property: " + key, exception);
        }
    }
}
