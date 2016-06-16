package controller.events;

import hochberger.utilities.eventbus.Event;
import hochberger.utilities.mathematics.Vector3D;
import model.Position;

public class LidarRequestEvent implements Event {

    private final Vector3D direction;
    private final Position position;

    public LidarRequestEvent(final Position position, final Vector3D direction) {
        super();
        this.direction = direction;
        this.position = position;
    }

    public Vector3D getDirection() {
        return this.direction;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public void performEvent() {
        // TODO Auto-generated method stub
    }
}
