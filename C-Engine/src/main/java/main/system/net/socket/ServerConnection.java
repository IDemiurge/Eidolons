package main.system.net.socket;

import main.system.auxiliary.log.Err;
import main.system.net.Viewer;
import main.system.net.socket.ServerConnector.CODES;
import main.system.net.socket.ServerConnector.NetCode;

import java.net.Socket;

public abstract class ServerConnection extends GenericConnection {

    private Viewer viewer;

    public ServerConnection(Socket socket1) {
        super(socket1, CODES.class);

    }

    public ServerConnection() {

    }

    @Override
    public boolean loop() {

        if (!pinged) {
            unresponded++;
            if (unresponded > 1000) {
                Err.warn("SERVER NOT RESPONDING!" + getClass().getSimpleName());
                return false;
            }
        } else {
            pinged = false;
            unresponded = 0;
        }

        return super.loop();
    }

    public abstract void handleInput(String input);

    public abstract void handleInputCode(NetCode code);

    @Override
    public void send(Object o) {
        super.send(o);
        if (viewer != null) {
            viewer.output(String.valueOf(o));
        }

    }

}
