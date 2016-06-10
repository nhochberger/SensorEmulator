package controller.communication;

import java.io.IOException;
import java.net.Socket;

import hochberger.utilities.application.session.BasicSession;
import model.HeightMap;
import model.HeightMapToStringConverter;
import model.Position;
import model.Vector3D;
import model.sensors.SimpleRayTracingLidar;

public class LidarSocketListener extends SensorSocketListener {

    private static final String PART_SEPARATOR = ";";
    private static final String NUMBER_SEPARATOR = ",";
    private final SensorRequestFormatValidator validator;
    private final SimpleRayTracingLidar lidar;
    private final HeightMapToStringConverter converter;

    public LidarSocketListener(final BasicSession session, final SimpleRayTracingLidar lidar, final int port) {
        super(session, port);
        this.lidar = lidar;
        this.validator = new SensorRequestFormatValidator();
        this.converter = new HeightMapToStringConverter();
    }

    @Override
    protected void performCommunication(final Socket clientSocket) throws IOException {
        final String request = readFromSocket(clientSocket);
        if (!this.validator.isValid(request)) {
            error(request, clientSocket);
        }
        final String[] parts = request.split(PART_SEPARATOR);
        final String[] positionParts = parts[0].split(NUMBER_SEPARATOR);
        final String[] directionParts = parts[1].split(NUMBER_SEPARATOR);
        final Position position = parsePosition(positionParts);
        final Vector3D direction = parseDirection(directionParts);
        // final double calculatedDistance = this.lidar.calculateDistance(position, direction);
        final HeightMap map = this.lidar.createTargetHeightMap(position, direction);
        System.err.println(this.converter.convert(map));
        writeToSocket(this.converter.convert(map), clientSocket);
    }

    private Position parsePosition(final String[] positionParts) {
        final double positionX = Double.parseDouble(positionParts[0]);
        final double positionY = Double.parseDouble(positionParts[1]);
        final double positionZ = Double.parseDouble(positionParts[2]);
        final Position position = createCorrectedPosition(positionX, positionY, positionZ);
        return position;
    }

    private Vector3D parseDirection(final String[] directionParts) {
        final double directionX = Double.parseDouble(directionParts[0]);
        final double directionY = Double.parseDouble(directionParts[1]);
        final double directionZ = Double.parseDouble(directionParts[2]);
        final Vector3D direction = createCorrectedDirection(directionX, directionY, directionZ);
        return direction;
    }

    /**
     *
     * The client side of this application uses a coordinate system that slightly differs from the one used here (and in OpenGL). This method exchanges the y component with the z component in order to
     * provide the required format.
     *
     * @param positionX
     * @param positionY
     * @param positionZ
     * @return adjusted position
     */
    private Position createCorrectedPosition(final double positionX, final double positionY, final double positionZ) {
        return new Position(positionX, positionZ, positionY);
    }

    /**
     *
     * The client side of this application uses a coordinate system that slightly differs from the one used here (and in OpenGL). This method exchanges the y component with the z component in order to
     * provide the required format.
     *
     * @param directionX
     * @param directionY
     * @param directionZ
     * @return adjusted direction
     */
    private Vector3D createCorrectedDirection(final double directionX, final double directionY, final double directionZ) {
        return new Vector3D(directionX, directionZ, directionY);
    }

    private void error(final String request, final Socket clientSocket) throws IOException {
        logger().error("Incorect format of request " + request);
        writeToSocket("-1", clientSocket);
    }
}
