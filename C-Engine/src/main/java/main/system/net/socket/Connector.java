package main.system.net.socket;

import main.system.net.socket.ServerConnector.NetCode;

public interface Connector {
    void send(NetCode code);

    void send(Object o);
}
