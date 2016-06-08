package model.sensors;

import controller.events.ImportFinishedEvent;
import controller.events.LidarRequestEvent;
import controller.events.LidarResultEvent;
import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import model.Position;
import model.Vector3D;

public class SimpleRayTracingLidar extends SessionBasedObject implements Lifecycle {

    float[][] heightMap;

    public SimpleRayTracingLidar(final BasicSession session) {
        super(session);
        this.heightMap = new float[0][0];
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

    private double calculateDistance(final Position position, final Vector3D direction) {
        final Vector3D normalizedDirection = direction.normalizedVector();
        boolean found = false;
        int x = 0;
        int z = 0;
        for (int i = 0; !found; i++) {
            final Vector3D stepVector = normalizedDirection.multiply(i);
            final Position newPosition = position.addVector(stepVector);
            x = (int) newPosition.getX();
            z = (int) newPosition.getZ();
            found = newPosition.getY() <= this.heightMap[x][z];
        }
        final double deltaX = Math.abs(position.getX() - x);
        final double deltaY = Math.abs(position.getY() - this.heightMap[x][z]);
        final double deltaZ = Math.abs(position.getZ() - z);
        System.err.println("delta X: " + deltaX);
        System.err.println("delta Y: " + deltaY);
        System.err.println("delta Z: " + deltaZ);
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
