package view;

import java.text.DecimalFormat;

import controller.events.ImportFinishedEvent;
import controller.events.LidarResultEvent;
import controller.events.OpticalSensorRequestEvent;
import edt.EDT;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import hochberger.utilities.gui.ApplicationGui;
import hochberger.utilities.gui.WindowClosedApplicationShutdownEventPublisher;
import hochberger.utilities.text.Text;
import model.HeightMap;
import model.Position;
import model.Vector3D;

public class SensorEmulatorGui extends SessionBasedObject implements ApplicationGui {

    private SensorEmulatorMainFrame mainFrame;

    public SensorEmulatorGui(final BasicSession session) {
        super(session);
    }

    @Override
    public void activate() {
        logger().info("GUI activated");
        this.mainFrame = new SensorEmulatorMainFrame(session());
        this.mainFrame.show();
        this.mainFrame.addWindowListener(new WindowClosedApplicationShutdownEventPublisher(session()));
        session().getEventBus().register(new LidarResultHandler(), LidarResultEvent.class);
        session().getEventBus().register(new ImportFinishedEventHandler(), ImportFinishedEvent.class);
        session().getEventBus().register(new OpticalSensorRequestHandler(), OpticalSensorRequestEvent.class);
    }

    @Override
    public void deactivate() {
        this.mainFrame.hide();
        logger().info("GUI deactivated");
    }

    public final class ImportFinishedEventHandler implements EventReceiver<ImportFinishedEvent> {

        public ImportFinishedEventHandler() {
            super();
        }

        @Override
        public void receive(final ImportFinishedEvent event) {
            SensorEmulatorGui.this.mainFrame.setHeightMap(event.getHeightMap());
        }
    }

    public final class LidarResultHandler implements EventReceiver<LidarResultEvent> {

        public LidarResultHandler() {
            super();
        }

        @Override
        public void receive(final LidarResultEvent event) {
            EDT.performBlocking(new Runnable() {
                @Override
                public void run() {
                    final DecimalFormat decimalFormatter = new DecimalFormat("##0.00");
                    final StringBuffer buffer = new StringBuffer();
                    final HeightMap heightMap = event.getHeightMap();
                    for (int z = 0; z < heightMap.getDimension(); z++) {
                        for (int x = 0; x < heightMap.getDimension(); x++) {
                            buffer.append(decimalFormatter.format(heightMap.get(x, z)));
                            buffer.append(Text.space(3));
                        }
                        buffer.append(System.lineSeparator());
                    }
                    SensorEmulatorGui.this.mainFrame.setLidarResultText(buffer.toString());
                }
            });
        }
    }

    public final class OpticalSensorRequestHandler implements EventReceiver<OpticalSensorRequestEvent> {

        public OpticalSensorRequestHandler() {
            super();
        }

        @Override
        public void receive(final OpticalSensorRequestEvent event) {

            final Position position = event.getPosition();
            final Vector3D direction = event.getDirection();

            final double n = (-position.getY()) / direction.getY();
            final double viewTargetX = position.getX() + n * direction.getX();
            final double viewTargetZ = position.getZ() + n * direction.getZ();

            final Position viewTargetPosition = new Position(viewTargetX, 0, viewTargetZ);
            System.err.println(viewTargetPosition);
            SensorEmulatorGui.this.mainFrame.setOpticalSensor(position, viewTargetPosition);
        }
    }
}
