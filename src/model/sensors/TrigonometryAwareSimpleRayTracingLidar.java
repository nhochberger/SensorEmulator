package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.HeightMap;
import model.Position;

public class TrigonometryAwareSimpleRayTracingLidar extends SimpleRayTracingLidar {

    public TrigonometryAwareSimpleRayTracingLidar(final BasicSession session) {
        super(session);
    }

    @Override
    protected int beamRadius() {
        return 50;
    }

    @Override
    public HeightMap createTargetHeightMap(final Position position, final Vector3D direction) {
        final HeightMap map = new HeightMap(2 * beamRadius() + 1);

        final int[] targetCoordinates = determineTargetCoordinates(position, direction);
        final Vector3D vectorInXDirection = new Vector3D(direction.getX(), direction.getY(), 0);
        final double stepSizeX = calculateStepSize(position, vectorInXDirection);
        final Vector3D vectorInZDirection = new Vector3D(0, direction.getY(), direction.getZ());
        final double stepSizeZ = calculateStepSize(position, vectorInZDirection);
        logger().info("Step size in x-direction: " + stepSizeX + ", in z-direction: " + stepSizeZ);

        return map;
    }

    private double calculateStepSize(final Position position, final Vector3D vectorInDirection) {
        final Vector3D soundingLineVector = new Vector3D(0, -1, 0);
        final double angle = soundingLineVector.angleTo(vectorInDirection);
        final double angle1 = angle + 15.0;
        final double angle2 = angle - 15.0;
        logger().info("Angle between " + soundingLineVector + " and " + vectorInDirection + ": " + angle);
        final double deltaX1 = Math.tan(Math.toRadians(angle1)) * position.getY();
        final double deltaX2 = Math.tan(Math.toRadians(angle2)) * position.getY();
        final double stepSize = (Math.abs(deltaX1 - deltaX2)) / (2 * beamRadius() + 1);
        return stepSize;
    }
}