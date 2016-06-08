package controller.events;

import hochberger.utilities.eventbus.Event;

public class ImportTerrainEvent implements Event {

    private final String filePath;

    public ImportTerrainEvent(final String filePath) {
        super();
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void performEvent() {

    }
}
