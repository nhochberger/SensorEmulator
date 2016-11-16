package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.SurfaceMap;
import model.Position;

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

    @Override
    protected int numberOfBeams() {
        return 5;
    }
}
