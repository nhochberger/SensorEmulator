package model.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import hochberger.utilities.application.session.BasicSession;
import model.Boulder;
import model.SurfaceMap;

public class CSVTerrainImporter extends TerrainImporter {

    public CSVTerrainImporter(final BasicSession session) {
        super(session);
    }

    @Override
    public SurfaceMap importTerrain(final File file) throws IOException {
        final List<String> lines = Files.readAllLines(file.toPath());
        logger().info("Read " + lines.size() + " lines");
        final SurfaceMap map = createMap(lines);
        readTerrain(lines, map);
        readObstacles(lines, map);
        return map;
    }

    private SurfaceMap createMap(final List<String> lines) {
        int zDimension = 0;
        int xDimension = 0;
        for (int z = 0; z < lines.size(); z++) {
            final String line = lines.get(z);
            zDimension = z;
            if (SerializationConstants.SECTION_DELIMITER.equals(line)) {
                break;
            }
            final String[] split = line.split(SerializationConstants.VALUE_DELIMITER);
            if (xDimension <= split.length) {
                xDimension = split.length;
            }
        }
        final SurfaceMap map = new SurfaceMap(xDimension, zDimension);
        logger().info("Determined Dimension: " + xDimension + ", " + zDimension);
        return map;
    }

    protected void readTerrain(final List<String> lines, final SurfaceMap map) {
        for (int z = 0; z < map.getZDimension(); z++) {
            final String line = lines.get(z);
            if (SerializationConstants.SECTION_DELIMITER.equals(line)) {
                return;
            }
            final String[] split = line.split(SerializationConstants.VALUE_DELIMITER);
            for (int x = 0; x < map.getXDimension(); x++) {
                final String elevation = split[x];
                try {
                    map.set(x, z, Double.parseDouble(elevation));
                } catch (final NumberFormatException e) {
                    map.set(x, z, 0d);
                }
            }
        }
        return;
    }

    private void readObstacles(final List<String> lines, final SurfaceMap map) {
        final int linesToSkip = map.getZDimension() + 2;
        logger().info("Importing " + (lines.size() - linesToSkip) + " obstacles");
        for (int i = linesToSkip; i < lines.size(); i++) {
            final String[] line = lines.get(i).split(SerializationConstants.VALUE_DELIMITER);
            try {
                final double x = Double.parseDouble(line[0]);
                final double y = Double.parseDouble(line[1]);
                final double z = Double.parseDouble(line[2]);
                final double r = Double.parseDouble(line[3]);
                map.addBoulder(new Boulder(x, y, z, r));
            } catch (final NumberFormatException e) {
                logger().error("Error while importing obstacle. Skipping this entry", e);
            }
        }
        logger().info("Obstacle import finished");
    }
}
