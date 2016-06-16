package model;

import hochberger.utilities.mathematics.Vector3D;
import hochberger.utilities.text.Text;

public class Position {

    private final double x;
    private final double y;
    private final double z;

    public Position(final double x, final double y, final double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Position addVector(final Vector3D vector) {
        return new Position(getX() + vector.getX(), getY() + vector.getY(), getZ() + vector.getZ());
    }

    @Override
    public String toString() {
        return "[" + getX() + Text.space() + getY() + Text.space() + getZ() + "]";
    }
}
