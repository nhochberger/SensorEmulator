package model.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import controller.events.ImportFailedEvent;
import controller.events.ImportFinishedEvent;
import controller.events.ImportTerrainEvent;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;

public class CSVTerrainImporter extends SessionBasedObject implements TerrainImporter, EventReceiver<ImportTerrainEvent> {

    private static final String DELIMITER = ", ";

    public CSVTerrainImporter(final BasicSession session) {
        super(session);
    }

    @Override
    public void importTerrain(final String filepath) {
        logger().info("Trying to import " + filepath);
        final File file = new File(filepath);
        if (!file.exists()) {
            return;
        }
        logger().info("Importing " + file.length() + " bytes");
        try {
            final List<String> lines = Files.readAllLines(file.toPath());
            final int dimension = lines.size();
            final float[][] heightMap = new float[dimension][dimension];
            for (int z = 0; z < dimension; z++) {
                final String line = lines.get(z);
                final String[] split = line.split(DELIMITER);
                for (int x = 0; x < split.length; x++) {
                    if (dimension != split.length) {
                        logger().error("Corrupt file. Dimensions do not fit.");
                        return;
                    }
                    final String elevation = split[x];
                    heightMap[x][z] = Float.parseFloat(elevation);
                }
            }
            logger().info("Import finished");
            session().getEventBus().publish(new ImportFinishedEvent(heightMap));
        } catch (final IOException | NumberFormatException e) {
            logger().error("Error while importing File", e);
            session().getEventBus().publish(new ImportFailedEvent());
        }
    }

    @Override
    public void receive(final ImportTerrainEvent event) {
        importTerrain(event.getFilePath());
    }
}
