package controller.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;
import hochberger.utilities.mathematics.Vector3D;
import hochberger.utilities.threading.ThreadRunner;
import model.HeightMapToStringConverter;
import model.Position;

public abstract class SensorSocketListener extends SessionBasedObject implements Lifecycle {

    private static final char REQUEST_DELIMITER = '?';
    private static final String ANSWER_DELIMITER = "!";
    private static final String PART_SEPARATOR = ";";
    private static final String NUMBER_SEPARATOR = ",";
    private final int port;
    private boolean running;
    private final SensorRequestFormatValidator validator;
    private final HeightMapToStringConverter converter;

    public SensorSocketListener(final BasicSession session, final int port) {
        super(session);
        this.port = port;
        this.running = false;
        this.validator = new SensorRequestFormatValidator();
        this.converter = new HeightMapToStringConverter();
    }

    @Override
    public void start() {
        this.running = true;
        ThreadRunner.startThread(new Runnable() {

            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(SensorSocketListener.this.port);
                } catch (final IOException e) {
                    logger().error("Error with socket communication", e);
                    Closer.close(serverSocket);
                    return;
                }
                while (SensorSocketListener.this.running) {
                    try {
                        final Socket clientSocket = serverSocket.accept();
                        ThreadRunner.startThread(new Runnable() {

                            @Override
                            public void run() {
                                logger().info("Listening to socket " + clientSocket);
                                try {
                                    performCommunication(clientSocket);
                                } catch (final IOException e) {
                                    logger().error("Error with socket communication", e);
                                } finally {
                                    Closer.close(clientSocket);
                                }
                                logger().info("Socket communication finished " + clientSocket);
                            }
                        }, "Port " + SensorSocketListener.this.port + " client thread");
                    } catch (final IOException e) {
                        logger().error("Error with socket communication", e);
                    }
                }
                Closer.close(serverSocket);
            }
        }, "Port " + this.port + " server thread");
    }

    protected String readFromSocket(final Socket socket) throws IOException {
        final StringBuffer message = new StringBuffer();
        final InputStream inputStream = socket.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int read;
        while (REQUEST_DELIMITER != (read = reader.read())) {
            message.append((char) read);
        }
        logger().info("Received request: " + message.toString());
        return message.toString();
    }

    protected void writeToSocket(final String message, final Socket socket) throws IOException {
        final OutputStream outputStream = socket.getOutputStream();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(message + ANSWER_DELIMITER);
        writer.flush();
    }

    protected Position parsePosition(final String request) {
        final String[] parts = request.split(PART_SEPARATOR);
        final String[] positionParts = parts[0].split(NUMBER_SEPARATOR);
        final double positionX = Double.parseDouble(positionParts[0]);
        final double positionY = Double.parseDouble(positionParts[1]);
        final double positionZ = Double.parseDouble(positionParts[2]);
        final Position position = createCorrectedPosition(positionX, positionY, positionZ);
        return position;
    }

    protected Vector3D parseDirection(final String request) {
        final String[] parts = request.split(PART_SEPARATOR);
        final String[] directionParts = parts[1].split(NUMBER_SEPARATOR);
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

    protected void error(final String request, final Socket clientSocket) throws IOException {
        logger().error("Incorect format of request " + request);
        writeToSocket("-1", clientSocket);
    }

    @Override
    public void stop() {
        this.running = false;

    }

    protected HeightMapToStringConverter converter() {
        return this.converter;
    }

    protected SensorRequestFormatValidator validator() {
        return this.validator;
    }

    protected abstract void performCommunication(Socket clientSocket) throws IOException;
}
