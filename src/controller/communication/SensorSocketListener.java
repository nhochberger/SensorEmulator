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
import hochberger.utilities.threading.ThreadRunner;

public abstract class SensorSocketListener extends SessionBasedObject implements Lifecycle {

    private static final char REQUEST_DELIMITER = '?';
    private final int port;
    private boolean running;

    public SensorSocketListener(final BasicSession session, final int port) {
        super(session);
        this.port = port;
        this.running = false;
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
        return message.toString();
    }

    protected void writeToSocket(final String message, final Socket socket) throws IOException {
        final OutputStream outputStream = socket.getOutputStream();
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(message);
        writer.flush();
    }

    @Override
    public void stop() {
        this.running = false;

    }

    protected abstract void performCommunication(Socket clientSocket) throws IOException;
}
