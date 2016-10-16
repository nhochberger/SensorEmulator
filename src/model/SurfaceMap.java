package model;

import java.util.LinkedList;
import java.util.List;

public class SurfaceMap {

    private final double[][] points;
    private final int xDimension;
    private final int zDimension;
    private final List<Boulder> boulders;

    public SurfaceMap(final int dimension) {
        this(dimension, dimension);
    }

    public SurfaceMap(final int xDimension, final int zDimension) {
        super();
        this.xDimension = xDimension;
        this.zDimension = zDimension;
        this.points = new double[xDimension][zDimension];
        this.boulders = new LinkedList<>();
    }

    public void set(final int x, final int z, final double elevation) {
        this.points[x][z] = elevation;
    }

    public double get(final int x, final int z) {
        return this.points[x][z];
    }

    public int getXDimension() {
        return this.xDimension;
    }

    public int getZDimension() {
        return this.zDimension;
    }

    public void addBoulder(final Boulder boulder) {
        this.boulders.add(boulder);
    }

    public List<Boulder> getBoulders() {
        return this.boulders;
    }
}
