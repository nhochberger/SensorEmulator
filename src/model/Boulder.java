package model;

public class Boulder {

    private final double x;
    private final double y;
    private final double z;
    private final double radius;

    public Boulder(final double x, final double y, final double z, final double radius) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getRadius() {
        return this.radius;
    }

    @Override
    public String toString() {
        return "(" + this.x + "|" + this.y + "|" + this.z + ") r=" + this.radius;
    }
}
