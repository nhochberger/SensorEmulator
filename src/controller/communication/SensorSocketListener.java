package controller.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;
import hochberger.utilities.threading.ThreadRunner;

public abstract class SensorSocketListener extends SessionBasedObject implements Lifecycle {

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
                while (SensorSocketListener.this.running) {
                    try {
                        serverSocket = new ServerSocket(SensorSocketListener.this.port);
                        final Socket clientSocket = serverSocket.accept();
                        ThreadRunner.startThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    performCommunication(clientSocket);
                                } finally {
                                    Closer.close(clientSocket);
                                }
                            }
                        });
                    } catch (final IOException e) {
                        logger().error("Error with socket communication", e);
                    }
                }
            }
        }, "Port " + this.port + " thread");
    }

    @Override
    public void stop() {
        this.running = false;
    }

    protected abstract void performCommunication(Socket clientSocket);
}
