package controller.communication;

import java.net.Socket;

import hochberger.utilities.application.session.BasicSession;

public class LidarSocketListener extends SensorSocketListener {

    public LidarSocketListener(final BasicSession session, final int port) {
        super(session, port);
    }

    @Override
    protected void performCommunication(final Socket clientSocket) {

    }
}
