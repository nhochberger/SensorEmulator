package controller.communication;

import java.net.Socket;

import hochberger.utilities.application.session.BasicSession;

public class OpticalSensorSocketListener extends SensorSocketListener {

    public OpticalSensorSocketListener(final BasicSession session, final int port) {
        super(session, port);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void performCommunication(final Socket clientSocket) {
        // TODO Auto-generated method stub
    }
}
