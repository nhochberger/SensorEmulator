package model.importer;

import java.io.File;
import java.io.IOException;

import controller.events.ImportFailedEvent;
import controller.events.ImportFinishedEvent;
import controller.events.ImportTerrainEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import model.SurfaceMap;

public abstract class TerrainImporter extends SessionBasedObject implements EventReceiver<ImportTerrainEvent> {

    private SurfaceMap map;

    public TerrainImporter(final BasicSession session) {
        super(session);
        this.map = new SurfaceMap(0);
    }

    public abstract SurfaceMap importTerrain(File file) throws IOException;

    @Override
    public void receive(final ImportTerrainEvent event) {
        handleImport(event.getFilePath());
    }

    private void handleImport(final String filepath) {
        logger().info("Trying to import " + filepath);
        final File file = new File(filepath);
        if (!file.exists()) {
            return;
        }
        logger().info("Importing " + file.length() + " bytes");
        try {
            this.map = importTerrain(file);
            logger().info("Import finished. Imported " + this.map.getXDimension() + " x " + this.map.getZDimension() + " elevation points.");
            session().getEventBus().publish(new ImportFinishedEvent(this.map));
        } catch (final IOException | NumberFormatException e) {
            logger().error("Error while importing File", e);
            session().getEventBus().publish(new ImportFailedEvent());
        }
    }
}
