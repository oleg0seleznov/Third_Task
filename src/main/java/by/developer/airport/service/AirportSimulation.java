package by.developer.airport.service;

import by.developer.airport.config.FlightData;
import by.developer.airport.model.Airplane;
import by.developer.airport.resource.Gate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AirportSimulation {
    private static final Logger LOGGER = LogManager.getLogger(AirportSimulation.class);
    private static final long SERVICE_DELAY_MILLIS = 50;

    private final Airport airport;

    public AirportSimulation(Airport airport) {
        this.airport = airport;
    }

    public void run() throws InterruptedException {
        List<Future<Boolean>> flights = new ArrayList<>();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
        List<Exception> failures = new ArrayList<>();
        boolean interrupted = false;

        try {
            for (FlightData data : airport.configuration().flights()) {
                flights.add(executor.submit(new FlightService(airport, new Airplane(data))));
            }
            executor.shutdown();

            for (Future<Boolean> flight : flights) {
                boolean completed = false;
                while (!completed) {
                    try {
                        if (!flight.get()) {
                            failures.add(new IllegalStateException("A flight was not completed"));
                        }
                        completed = true;
                    } catch (InterruptedException exception) {
                        interrupted = true;
                    } catch (ExecutionException exception) {
                        failures.add(exception);
                        completed = true;
                    }
                }
            }
        } finally {
            executor.shutdown();
            boolean terminated = false;
            while (!terminated) {
                try {
                    terminated = executor.awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException exception) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }

        if (!failures.isEmpty()) {
            IllegalStateException exception = new IllegalStateException("One or more flights failed");
            failures.forEach(exception::addSuppressed);
            throw exception;
        }
        if (interrupted) {
            throw new InterruptedException("Interrupted after all flights were serviced");
        }
        LOGGER.info("All {} airplanes have been serviced", flights.size());
    }

    private static final class FlightService implements Callable<Boolean> {
        private final Airport airport;
        private final Airplane airplane;

        private FlightService(Airport airport, Airplane airplane) {
            this.airport = airport;
            this.airplane = airplane;
        }

        @Override
        public Boolean call() throws Exception {
            Gate gate = airport.acquireGate();
            try {
                LOGGER.info("Flight {} arrived at gate {}", airplane.number(), gate.number());
                airplane.advance();
                processPassengers(airplane.arrivingPassengers(), "disembarked");
                airplane.advance();
                processPassengers(airplane.departingPassengers(), "boarded");
                airplane.advance();
                LOGGER.info("Flight {} departed; state={}", airplane.number(), airplane.stateName());
                return true;
            } finally {
                airport.releaseGate(gate);
            }
        }

        private void processPassengers(int passengerCount, String action) throws InterruptedException {
            airport.terminal().admitPassengers(passengerCount);
            try {
                LOGGER.info("{} passengers {} flight {} through the terminal",
                        passengerCount, action, airplane.number());
                TimeUnit.MILLISECONDS.sleep(SERVICE_DELAY_MILLIS);
            } finally {
                airport.terminal().releasePassengers(passengerCount);
            }
        }
    }
}
