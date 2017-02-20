package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;
import model.SurfaceMap;

public class TrigonometryAwareSimpleRayTracingLidar extends SimpleRayTracingLidar {

    public TrigonometryAwareSimpleRayTracingLidar(final BasicSession session) {
        super(session);
    }

    @Override
    protected int numberOfBeams() {
        return 100;
    }

    private double openingAngle() {
        return 15.0;
    }

    @Override
    public SurfaceMap createTargetHeightMap(final Position position, final Vector3D direction) {
        final SurfaceMap map = new SurfaceMap(numberOfBeams());
        final Vector3D centerBeam = direction.normalizedVector();
        final double singleRayAngleDifference = Math.toRadians(openingAngle() / numberOfBeams());
        final Vector3D cornerBeam = centerBeam.rotateVectorX(Math.toRadians(-openingAngle() / 2)).rotateVectorZ(Math.toRadians(-openingAngle() / 2));
        Vector3D currentBeam = cornerBeam;
        for (int z = 0; z < numberOfBeams(); z++) {
            for (int x = 0; x < numberOfBeams(); x++) {
                final int[] target = determineTargetCoordinates(position, currentBeam);
                map.set(x, z, heightMap().get(target[0], target[1]));
                currentBeam = currentBeam.rotateVectorX(singleRayAngleDifference);
            }
            currentBeam = cornerBeam.rotateVectorZ(z * singleRayAngleDifference);
        }
        return map;
    }
}
