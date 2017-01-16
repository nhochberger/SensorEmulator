package model.sensors;

import controller.events.ImportFinishedEvent;
import controller.events.LidarRequestEvent;
import controller.events.LidarResultEvent;
import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;
import model.SurfaceMap;

public abstract class Lidar extends SessionBasedObject implements Lifecycle {

    private SurfaceMap heightMap;

    public Lidar(final BasicSession session) {
        super(session);
        this.heightMap = new SurfaceMap(0);
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

    protected void heightMap(final SurfaceMap heightMap) {
        this.heightMap = heightMap;
    }

    protected SurfaceMap heightMap() {
        return this.heightMap;
    }

    public abstract SurfaceMap createTargetHeightMap(final Position position, final Vector3D direction);

    protected abstract int numberOfBeams();

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
            final SurfaceMap map = createTargetHeightMap(event.getPosition(), event.getDirection());
            session().getEventBus().publish(new LidarResultEvent(map));
        }
    }

}
