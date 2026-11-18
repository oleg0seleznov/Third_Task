package by.developer.airport.model;

import by.developer.airport.config.FlightData;
import by.developer.airport.model.state.PlaneState;
import java.util.concurrent.locks.ReentrantLock;

public final class Airplane {
    private final FlightData flightData;
    private final ReentrantLock stateLock = new ReentrantLock();
    private PlaneState state = PlaneState.ARRIVING;

    public Airplane(FlightData flightData) {
        this.flightData = flightData;
    }

    public String number() {
        return flightData.number();
    }

    public int arrivingPassengers() {
        return flightData.arrivingPassengers();
    }

    public int departingPassengers() {
        return flightData.departingPassengers();
    }

    public String stateName() {
        stateLock.lock();
        try {
            return state.displayName();
        } finally {
            stateLock.unlock();
        }
    }

    public void advance() {
        stateLock.lock();
        try {
            state = state.next();
        } finally {
            stateLock.unlock();
        }
    }
}
