package view;

import java.text.DecimalFormat;

import controller.events.LidarResultEvent;
import edt.EDT;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.eventbus.EventReceiver;
import hochberger.utilities.gui.ApplicationGui;
import hochberger.utilities.gui.WindowClosedApplicationShutdownEventPublisher;

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
    }

    @Override
    public void deactivate() {
        this.mainFrame.hide();
        logger().info("GUI deactivated");
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
                    final DecimalFormat formatter = new DecimalFormat("0.00000");
                    // SensorEmulatorGui.this.mainFrame.setLidarResultText(formatter.format(event.getHeightMap()) + " u");
                }
            });
        }
    }
}
