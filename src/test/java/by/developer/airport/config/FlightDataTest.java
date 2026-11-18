package by.developer.airport.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FlightDataTest {
    @Test
    void rejectsPassengerCountAboveAircraftCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FlightData("SU101", 5, 6, 4));
    }
}
