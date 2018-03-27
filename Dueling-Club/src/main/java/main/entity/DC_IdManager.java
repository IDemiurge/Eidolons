package main.entity;

import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.MicroGame;
import main.system.entity.IdManager;

public class DC_IdManager implements IdManager {

    public static final int TIME_ID = Integer.MAX_VALUE;
    private Game game;
    private boolean host;
    private Integer ID = 0;
    private Integer typeID = 0;

    public DC_IdManager() {

    }

    public DC_IdManager(MicroGame game) {
        super();
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
