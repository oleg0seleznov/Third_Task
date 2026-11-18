package by.developer.airport.config;

import java.util.List;

public record AirportConfiguration(int gateCount, int terminalCapacity, List<FlightData> flights) {
    public AirportConfiguration {
        if (gateCount < 1 || terminalCapacity < 1 || flights.isEmpty()) {
            throw new IllegalArgumentException("Airport must have gates, terminal capacity and flights");
        }
        flights = List.copyOf(flights);
        for (FlightData flight : flights) {
            if (flight.arrivingPassengers() > terminalCapacity
                    || flight.departingPassengers() > terminalCapacity) {
                throw new IllegalArgumentException(
                        "Passenger count for flight " + flight.number() + " exceeds terminal capacity");
            }
        }
    }
}
