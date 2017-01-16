package view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import controller.events.ImportFinishedEvent;
import controller.events.LidarResultEvent;
import controller.events.OpticalSensorRequestEvent;
import edt.EDT;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import hochberger.utilities.gui.ApplicationGui;
import hochberger.utilities.gui.WindowClosedApplicationShutdownEventPublisher;
import hochberger.utilities.mathematics.Vector3D;
import hochberger.utilities.text.Text;
import model.Position;
import model.SurfaceMap;

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

    public String screenshot(final Position position, final Vector3D direction) {
        this.mainFrame.setOpticalSensor(position, calculateTargetPosition(position, direction));
        return this.mainFrame.prepareScreenshot();
    }

    private Position calculateTargetPosition(final Position position, final Vector3D direction) {
        final double n = (-position.getY()) / direction.getY();
        final double viewTargetX = position.getX() + n * direction.getX();
        final double viewTargetZ = position.getZ() + n * direction.getZ();
        final Position viewTargetPosition = new Position(viewTargetX, 0, viewTargetZ);
        return viewTargetPosition;
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
                    decimalFormatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
                    final StringBuffer buffer = new StringBuffer();
                    final SurfaceMap heightMap = event.getHeightMap();
                    for (int z = 0; z < heightMap.getZDimension(); z++) {
                        for (int x = 0; x < heightMap.getXDimension(); x++) {
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
            final Position viewTargetPosition = calculateTargetPosition(position, direction);
            logger().info("Optical sensor: position: " + position + ", view target position: " + viewTargetPosition);
            SensorEmulatorGui.this.mainFrame.setOpticalSensor(position, viewTargetPosition);
            SensorEmulatorGui.this.mainFrame.prepareScreenshot();
        }
    }
}
