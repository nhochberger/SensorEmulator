package controller.communication;

import java.io.IOException;
import java.net.Socket;

import hochberger.utilities.application.session.BasicSession;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;
import view.SensorEmulatorGui;

public class OpticalSensorSocketListener extends SensorSocketListener {

    private final SensorEmulatorGui gui;

    public OpticalSensorSocketListener(final BasicSession session, final int port, final SensorEmulatorGui gui) {
        super(session, port);
        this.gui = gui;
    }

    @Override
    protected void performCommunication(final Socket clientSocket) throws IOException {
        final String request = readFromSocket(clientSocket);
        if (!validator().isValid(request)) {
            error(request, clientSocket);
        }
        final Position position = parsePosition(request);
        final Vector3D direction = parseDirection(request);
        final String file = this.gui.screenshot(position, direction);
        writeToSocket(file, clientSocket);
    }
}
