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
        }
        final int[] result = { x, z };
        return result;
    }

    public double calculateDistance(final Position position, final Vector3D direction) {
        final int[] targetCoordinates = determineTargetCoordinates(position, direction);
        final int x = targetCoordinates[0];
        final int z = targetCoordinates[1];
        final double deltaX = Math.abs(position.getX() - x);
        final double deltaY = Math.abs(position.getY() - this.heightMap.get(x, z));
        final double deltaZ = Math.abs(position.getZ() - z);
        final double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        return distance;
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
            final double distance = calculateDistance(event.getPosition(), event.getDirection());
            session().getEventBus().publish(new LidarResultEvent(distance));
        }
    }
}
