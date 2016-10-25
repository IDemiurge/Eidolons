package main.system.net;

import main.client.net.GameConnector;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.MicroGame;
import main.system.auxiliary.IdManager;

public class DC_IdManager implements IdManager {

    public static final int TIME_ID = Integer.MAX_VALUE;
    private GameConnector connector;
    private Game game;
    private boolean host;
    private Integer ID = 0;
    private Integer typeID = 0;

    public DC_IdManager() {

    }
    public DC_IdManager(GameConnector connector, MicroGame game) {
        super();
        this.connector = connector;
        this.game = game;
        this.setHost(game.isHost());
    }

    public synchronized Integer getNewId() {
        ID++;
        return ID;
        // initial party?
        // return (host) ? newId() : requestNewId();

    }

    @Override
    public Integer getNewTypeId() {
        typeID--;
        return typeID;
        // typeID++;
        // return typeID;
    }

    private Integer requestNewId() {
        connector.send(HOST_CLIENT_CODES.ID_REQUEST);
        String id = "";
        if (!new WaitingThread(HOST_CLIENT_CODES.ID_REQUEST).Wait()) {
            id = WaitingThread.getINPUT(HOST_CLIENT_CODES.ID_REQUEST);

        }
        try {
            ID = Integer.valueOf(id);
        } catch (Exception e) {

        }

        return ID;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    @Override
    public void setSpecialTypeId(ObjType type, int id) {
        typeID--;
        type.setId(id);
    }

    @Override
    public void setSpecialId(Entity e, int id) {
        ID--;
        e.setId(id);
    }

}
