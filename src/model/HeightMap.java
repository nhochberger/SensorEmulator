package model;

public class HeightMap {

    private final double[][] points;
    private final int xDimension;
    private final int zDimension;

    public HeightMap(final int dimension) {
        this(dimension, dimension);
    }

    public HeightMap(final int xDimension, final int zDimension) {
        super();
        this.xDimension = xDimension;
        this.zDimension = zDimension;
        this.points = new double[xDimension][zDimension];
    }

    public void set(final int x, final int z, final double elevation) {
        if (0 > x || this.xDimension <= x || 0 > z || this.zDimension <= z) {
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
        if (0 > x || this.xDimension <= x || 0 > z || this.zDimension <= z) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.points[x][z];
    }

    public int getXDimension() {
        return this.xDimension;
    }

    public int getZDimension() {
        return this.zDimension;
    }
}
