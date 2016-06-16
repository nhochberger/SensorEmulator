package model.sensors;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.HeightMap;
import model.Position;

public class WholeMapLidar extends Lidar {

    public WholeMapLidar(final BasicSession session) {
        super(session);
        // TODO Auto-generated constructor stub
    }

    @Override
    public HeightMap createTargetHeightMap(final Position position, final Vector3D direction) {
        return heightMap();
    }

    @Override
    protected int beamRadius() {
        // not necessary here
        return 0;
    }
}
