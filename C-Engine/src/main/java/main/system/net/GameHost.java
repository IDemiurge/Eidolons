package main.system.net;

import java.net.ServerSocket;

public abstract class GameHost {
    protected ServerSocket hostServer;

    protected abstract void listenForUserConnection();

}
