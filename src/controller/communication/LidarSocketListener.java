package controller.communication;

import java.io.IOException;
import java.net.Socket;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.HeightMap;
import model.Position;
import model.sensors.Lidar;

public class LidarSocketListener extends SensorSocketListener {

    private final Lidar lidar;

    public LidarSocketListener(final BasicSession session, final Lidar lidar, final int port) {
        super(session, port);
        this.lidar = lidar;
    }

    @Override
    protected void performCommunication(final Socket clientSocket) throws IOException {
        final String request = readFromSocket(clientSocket);
        if (!validator().isValid(request)) {
            error(request, clientSocket);
        }
        final Position position = parsePosition(request);
        final Vector3D direction = parseDirection(request);
        final HeightMap map = this.lidar.createTargetHeightMap(position, direction);
        writeToSocket(converter().convert(map), clientSocket);
    }
}
