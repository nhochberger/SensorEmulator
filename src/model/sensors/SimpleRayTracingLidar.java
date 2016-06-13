package model.sensors;

import controller.events.ImportFinishedEvent;
import controller.events.LidarRequestEvent;
import controller.events.LidarResultEvent;
import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import model.HeightMap;
import model.Position;
import model.Vector3D;

public class SimpleRayTracingLidar extends SessionBasedObject implements Lifecycle {

    public static final int BEAM_RADIUS = 5;

    HeightMap heightMap;

    public SimpleRayTracingLidar(final BasicSession session) {
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

    public int[] determineTargetCoordinates(final Position position, final Vector3D direction) {
        final Vector3D normalizedDirection = direction.normalizedVector();
        boolean found = false;
        int x = 0;
        int z = 0;
        for (int i = 0; !found; i++) {
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
        final int[] result = { x, z };
        return result;
    }

    public HeightMap createTargetHeightMap(final Position position, final Vector3D direction) {
        final HeightMap resultMap = new HeightMap(2 * BEAM_RADIUS + 1);
        final int[] target = determineTargetCoordinates(position, direction);
        final int targetX = target[0];
        final int targetZ = target[1];
        for (int z = 0; z <= 2 * BEAM_RADIUS + 1; z++) {
            for (int x = 0; x <= 2 * BEAM_RADIUS + 1; x++) {
                final int desiredX = targetX - BEAM_RADIUS + x;
                final int desiredZ = targetZ - BEAM_RADIUS + z;
                final double trueElevation = this.heightMap.get(desiredX, desiredZ);
                resultMap.set(x, z, trueElevation);
            }
        }
        return resultMap;
    }

    private final class TerrainImportEventHandler implements EventReceiver<ImportFinishedEvent> {

        public TerrainImportEventHandler() {
            super();
        }

        @Override
        public void receive(final ImportFinishedEvent event) {
            SimpleRayTracingLidar.this.heightMap = event.getHeightMap();
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
