package model;

public class HeightMapToStringConverter {

    public HeightMapToStringConverter() {
        super();
    }

    public String convert(final HeightMap map) {
        final StringBuffer buffer = new StringBuffer();
        for (int x = 0; x < map.getDimension(); x++) {
            for (int z = 0; z < map.getDimension(); z++) {
                buffer.append(map.get(x, z));
                if (z < map.getDimension() - 1) {
                    buffer.append(",");
                }
            }
            buffer.append(";");
        }
        return buffer.toString();
    }
}
