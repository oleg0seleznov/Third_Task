package by.developer.airport.config;

public record FlightData(
        String number,
        int capacity,
        int arrivingPassengers,
        int departingPassengers) {
    public FlightData {
        if (number == null || number.isBlank()
                || capacity < 1
                || arrivingPassengers < 0
                || departingPassengers < 0
                || arrivingPassengers > capacity
                || departingPassengers > capacity) {
            throw new IllegalArgumentException("Invalid data for flight: " + number);
        }
    }
}
