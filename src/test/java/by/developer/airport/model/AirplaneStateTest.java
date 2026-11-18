package by.developer.airport.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import by.developer.airport.config.FlightData;
import org.junit.jupiter.api.Test;

class AirplaneStateTest {
    @Test
    void advancesThroughServiceStatesAndRejectsTransitionAfterDeparture() {
        Airplane airplane = new Airplane(new FlightData("SU101", 10, 5, 6));

        assertEquals("arriving", airplane.stateName());
        airplane.advance();
        assertEquals("unloading", airplane.stateName());
        airplane.advance();
        assertEquals("boarding", airplane.stateName());
        airplane.advance();
        assertEquals("departed", airplane.stateName());
        assertThrows(IllegalStateException.class, airplane::advance);
    }
}
