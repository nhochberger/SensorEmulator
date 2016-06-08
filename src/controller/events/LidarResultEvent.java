package controller.events;

import hochberger.utilities.eventbus.Event;

public class LidarResultEvent implements Event {

    private final double distance;

    public LidarResultEvent(final double distance) {
        super();
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public void performEvent() {
    }
}
