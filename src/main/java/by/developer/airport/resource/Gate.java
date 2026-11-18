package by.developer.airport.resource;

import java.util.concurrent.locks.ReentrantLock;

public final class Gate {
    private final int number;
    private final ReentrantLock occupancyLock = new ReentrantLock();

    public Gate(int number) {
        this.number = number;
    }

    public int number() {
        return number;
    }

    public void lock() {
        occupancyLock.lock();
    }

    public void unlock() {
        occupancyLock.unlock();
    }
}
