package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;
import model.SurfaceMap;

public class WholeMapLidar extends Lidar {

    public WholeMapLidar(final BasicSession session) {
        super(session);
        // TODO Auto-generated constructor stub
    }

    @Override
    public SurfaceMap createTargetHeightMap(final Position position, final Vector3D direction) {
        return heightMap();
    }

    @Override
    protected int numberOfBeams() {
        // not necessary here
        return 0;
    }
}
