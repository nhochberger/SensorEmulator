package controller.communication;

import java.io.IOException;
import java.net.ServerSocket;

import hochberger.utilities.application.Lifecycle;
import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.application.session.SessionBasedObject;
import hochberger.utilities.files.Closer;

public class SocketCommunicationHandler extends SessionBasedObject implements Lifecycle {

    public SocketCommunicationHandler(final BasicSession session) {
        super(session);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void start() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(50000);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            Closer.close(socket);
        }
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

}
