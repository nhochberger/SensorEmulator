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
        this.points[x][z] = elevation;
    }

    public double get(final int x, final int z) {
        return this.points[x][z];
    }

    public int getDimension() {
        return this.dimension;
    }
}
