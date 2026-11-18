package by.developer.airport.resource;

import java.util.concurrent.Semaphore;

public record Terminal(Semaphore availablePlaces) {
    public Terminal {
        if (availablePlaces == null) {
            throw new IllegalArgumentException("Terminal semaphore must not be null");
        }
    }

    public Terminal(int capacity) {
        this(new Semaphore(capacity, true));
    }

    public void admitPassengers(int passengerCount) throws InterruptedException {
        availablePlaces.acquire(passengerCount);
    }

    public void releasePassengers(int passengerCount) {
        availablePlaces.release(passengerCount);
    }
}
