package model;

public class HeightMap {

    private final double[][] points;
    private final int dimension;

    public HeightMap(final int dimension) {
        super();
        this.dimension = dimension;
        this.points = new double[dimension][dimension];
    }

    public void set(final int x, final int z, final double elevation) {
        if (0 > x || this.dimension <= x || 0 > z || this.dimension <= z) {
            return;
        }
        this.points[x][z] = elevation;
    }

    /**
     *
     * @param x
     * @param z
     * @return the elevation at the specified point. Double.NEGATIVE_INFINITY if the point is outside the specified area
     */
    public double get(final int x, final int z) {
        if (0 > x || this.dimension <= x || 0 > z || this.dimension <= z) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.points[x][z];
    }

    public int getDimension() {
        return this.dimension;
    }
}
