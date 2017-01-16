package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;
import model.SurfaceMap;

public class SimpleRayTracingLidar extends Lidar {

    public SimpleRayTracingLidar(final BasicSession session) {
        super(session);
    }

    @Override
    public SurfaceMap createTargetHeightMap(final Position position, final Vector3D direction) {
        final SurfaceMap resultMap = new SurfaceMap(2 * numberOfBeams() + 1);
        final int[] target = determineTargetCoordinates(position, direction);
        final int targetX = target[0];
        final int targetZ = target[1];
        for (int z = 0; z <= 2 * numberOfBeams() + 1; z++) {
            for (int x = 0; x <= 2 * numberOfBeams() + 1; x++) {
                final int desiredX = targetX - numberOfBeams() + x;
                final int desiredZ = targetZ - numberOfBeams() + z;
                final double trueElevation = heightMap().get(desiredX, desiredZ);
                resultMap.set(x, z, trueElevation);
            }
        }
        return resultMap;
    }

    protected int[] determineTargetCoordinates(final Position position, final Vector3D direction) {
        final Vector3D normalizedDirection = direction.normalizedVector();
        logger().info("Lidar: position: " + position + ", direction: " + direction + "(normalized: " + normalizedDirection + ")");
        boolean found = false;
        int x = 0;
        int z = 0;
        int i;
        for (i = 0; !found; i++) {
            final Vector3D stepVector = normalizedDirection.multiply(i);
            final Position newPosition = position.addVector(stepVector);
            x = (int) newPosition.getX();
            z = (int) newPosition.getZ();
            found = newPosition.getY() <= heightMap().get(x, z);
            if (i >= 100000) {
                logger().info("Lidar was unable to gather information. direction of Lidar beams.");
                break;
            }
        }
        logger().info("Lidar center beam hits surface at [" + x + ", " + z + "]");
        final int[] result = { x, z };
        return result;
    }

    @Override
    protected int numberOfBeams() {
        return 5;
    }
}
