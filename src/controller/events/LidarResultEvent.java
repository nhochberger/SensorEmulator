package controller.events;

import hochberger.utilities.eventbus.Event;
import model.SurfaceMap;

public class LidarResultEvent implements Event {

    private final SurfaceMap heightMap;

    public LidarResultEvent(final SurfaceMap heightMap) {
        super();
        this.heightMap = heightMap;
    }

    public SurfaceMap getHeightMap() {
        return this.heightMap;
    }

    @Override
    public void performEvent() {
    }
}
