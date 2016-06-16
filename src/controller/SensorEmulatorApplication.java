package controller;

import controller.communication.LidarSocketListener;
import controller.communication.OpticalSensorSocketListener;
import controller.communication.SensorSocketListener;
import controller.events.ImportTerrainEvent;
import hochberger.utilities.application.ApplicationProperties;
import hochberger.utilities.application.ApplicationShutdownEvent;
import hochberger.utilities.application.ApplicationShutdownEventReceiver;
import hochberger.utilities.application.BasicLoggedApplication;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.eventbus.SimpleEventBus;
import model.importer.CSVTerrainImporter;
import model.sensors.SimpleRayTracingLidar;
import model.sensors.TrigonometryAwareSimpleRayTracingLidar;
import view.SensorEmulatorGui;

public class SensorEmulatorApplication extends BasicLoggedApplication {

    private final BasicSession session;
    private final SensorEmulatorGui gui;
    private final SimpleRayTracingLidar lidar;
    private final SensorSocketListener lidarSocketListener;
    private final OpticalSensorSocketListener opticalSensorSocketListener;

    public static void main(final String[] args) {
        setUpLoggingServices(SensorEmulatorApplication.class);
        try {
            final ApplicationProperties applicationProperties = new ApplicationProperties();
            final SensorEmulatorApplication application = new SensorEmulatorApplication(applicationProperties);
            application.start();
        } catch (final Exception e) {
            getLogger().fatal("Error while starting application. Shutting down.", e);
            System.exit(0);
        }
    }

    public SensorEmulatorApplication(final ApplicationProperties applicationProperties) {
        super();
        this.session = new BasicSession(applicationProperties, new SimpleEventBus(), getLogger());
        this.gui = new SensorEmulatorGui(this.session);
        this.lidar = new TrigonometryAwareSimpleRayTracingLidar(this.session);// new SimpleRayTracingLidar(this.session);
        this.lidarSocketListener = new LidarSocketListener(this.session, this.lidar, 50000);
        this.opticalSensorSocketListener = new OpticalSensorSocketListener(this.session, 50001, this.gui);
    }

    @Override
    public void start() {
        super.start();
        this.session.getEventBus().register(new CSVTerrainImporter(this.session), ImportTerrainEvent.class);
        this.session.getEventBus().register(new ApplicationShutdownEventReceiver(this.session, this), ApplicationShutdownEvent.class);
        this.lidar.start();
        this.lidarSocketListener.start();
        this.gui.activate();
        this.opticalSensorSocketListener.start();
    }

    @Override
    public void stop() {
        this.gui.deactivate();
        this.lidar.stop();
        this.lidarSocketListener.stop();
        super.stop();
    }
}
