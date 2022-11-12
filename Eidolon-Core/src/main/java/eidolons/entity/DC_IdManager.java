package eidolons.entity;

import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.game.GenericGame;
import main.system.entity.IdManager;

public class DC_IdManager implements IdManager {

    public static final int TIME_ID = Integer.MAX_VALUE;
    protected Game game;
    protected boolean host;
    protected Integer ID = 0;
    protected Integer typeID = 0;

    public DC_IdManager() {

    }

    public DC_IdManager(GenericGame game) {
        super();
        this.game = game;
        this.setHost(game.isHost());
    }

    public   Integer getNewId() {
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
