package controller.events;

import hochberger.utilities.eventbus.Event;
import model.HeightMap;

public class LidarResultEvent implements Event {

    private final HeightMap heightMap;

    public LidarResultEvent(final HeightMap heightMap) {
        super();
        this.heightMap = heightMap;
    }

    public HeightMap getHeightMap() {
        return this.heightMap;
    }

    @Override
    public void performEvent() {
    }
}
