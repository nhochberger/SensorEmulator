package model;

public class SurfaceMapToStringConverter {

    public SurfaceMapToStringConverter() {
        super();
    }

    public String convert(final SurfaceMap map) {
        final StringBuffer buffer = new StringBuffer();
        for (int x = 0; x < map.getXDimension(); x++) {
            for (int z = 0; z < map.getZDimension(); z++) {
                buffer.append(map.get(x, z));
                if (z < map.getZDimension() - 1) {
                    buffer.append(",");
                }
            }
            buffer.append(";");
        }
        return buffer.toString();
    }
}
