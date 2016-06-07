package view;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
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
    }

    @Override
    public void deactivate() {
        this.mainFrame.hide();
        logger().info("GUI deactivated");
    }
}
