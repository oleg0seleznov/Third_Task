package by.developer.airport.model.state;

public enum PlaneState {
    ARRIVING("arriving"),
    UNLOADING("unloading"),
    BOARDING("boarding"),
    DEPARTED("departed");

    private final String name;

    PlaneState(String name) {
        this.name = name;
    }

    public String displayName() {
        return name;
    }

    public PlaneState next() {
        return switch (this) {
            case ARRIVING -> UNLOADING;
            case UNLOADING -> BOARDING;
            case BOARDING -> DEPARTED;
            case DEPARTED -> throw new IllegalStateException("A departed airplane cannot change state");
        };
    }
}
