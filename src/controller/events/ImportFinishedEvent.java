package controller.events;

import hochberger.utilities.eventbus.Event;

public class ImportFinishedEvent implements Event {

    private final float[][] heightMap;

    public ImportFinishedEvent(final float[][] heightMap) {
        super();
        this.heightMap = heightMap;
    }

    public float[][] getHeightMap() {
        return this.heightMap;
    }

    @Override
    public void performEvent() {
        // TODO Auto-generated method stub
    }
}
