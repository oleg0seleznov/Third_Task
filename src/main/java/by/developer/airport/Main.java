package by.developer.airport;

import by.developer.airport.service.Airport;
import by.developer.airport.service.AirportSimulation;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        new AirportSimulation(Airport.getInstance()).run();
    }
}
