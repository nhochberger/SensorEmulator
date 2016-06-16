package model.sensors;

import controller.events.ImportFinishedEvent;
import controller.events.LidarRequestEvent;
import controller.events.LidarResultEvent;
import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import hochberger.utilities.mathematics.Vector3D;
import model.HeightMap;
import model.Position;

public abstract class Lidar extends SessionBasedObject implements Lifecycle {

    private HeightMap heightMap;

    public Lidar(final BasicSession session) {
        super(session);
        this.heightMap = new HeightMap(0);
    }

    @Override
    public void start() {
        session().getEventBus().register(new TerrainImportEventHandler(), ImportFinishedEvent.class);
        session().getEventBus().register(new LidarRequestEventHandler(), LidarRequestEvent.class);
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
    }

    protected void heightMap(final HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    protected HeightMap heightMap() {
        return this.heightMap;
    }

    public int[] determineTargetCoordinates(final Position position, final Vector3D direction) {
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
            found = newPosition.getY() <= this.heightMap.get(x, z);
            if (i >= 100000) {
                logger().info("Lidar was unable to gather information. direction of Lidar beams.");
                break;
            }
        }
        logger().info("Lidar center beam hits surface at [" + x + ", " + z + "]");
        final int[] result = { x, z };
        return result;
    }

    public abstract HeightMap createTargetHeightMap(final Position position, final Vector3D direction);

    protected abstract int beamRadius();

    private final class TerrainImportEventHandler implements EventReceiver<ImportFinishedEvent> {

        public TerrainImportEventHandler() {
            super();
        }

        @Override
        public void receive(final ImportFinishedEvent event) {
            heightMap(event.getHeightMap());
        }
    }

    private final class LidarRequestEventHandler implements EventReceiver<LidarRequestEvent> {

        public LidarRequestEventHandler() {
            super();
        }

        @Override
        public void receive(final LidarRequestEvent event) {
            final HeightMap map = createTargetHeightMap(event.getPosition(), event.getDirection());
            session().getEventBus().publish(new LidarResultEvent(map));
        }
    }

}
