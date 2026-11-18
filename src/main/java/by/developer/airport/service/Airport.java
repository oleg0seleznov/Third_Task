package by.developer.airport.service;

import by.developer.airport.config.AirportConfiguration;
import by.developer.airport.config.AirportDataLoader;
import by.developer.airport.resource.Gate;
import by.developer.airport.resource.Terminal;
import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class Airport {
    private static final Airport INSTANCE = new Airport();

    private final AirportConfiguration configuration;
    private final ArrayDeque<Gate> availableGates;
    private final ReentrantLock gateSelectionLock = new ReentrantLock(true);
    private final Condition gateAvailable = gateSelectionLock.newCondition();
    private final Terminal terminal;

    private Airport() {
        configuration = AirportDataLoader.load();
        availableGates = new ArrayDeque<>(configuration.gateCount());
        for (int number = 1; number <= configuration.gateCount(); number++) {
            availableGates.addLast(new Gate(number));
        }
        terminal = new Terminal(configuration.terminalCapacity());
    }

    public static Airport getInstance() {
        return INSTANCE;
    }

    public AirportConfiguration configuration() {
        return configuration;
    }

    public Terminal terminal() {
        return terminal;
    }

    public Gate acquireGate() throws InterruptedException {
        Gate gate;
        gateSelectionLock.lockInterruptibly();
        try {
            while (availableGates.isEmpty()) {
                gateAvailable.await();
            }
            gate = availableGates.removeFirst();
        } finally {
            gateSelectionLock.unlock();
        }
        gate.lock();
        return gate;
    }

    public void releaseGate(Gate gate) {
        gateSelectionLock.lock();
        try {
            gate.unlock();
            availableGates.addLast(gate);
            gateAvailable.signal();
        } finally {
            gateSelectionLock.unlock();
        }
    }
}
