package main.logic;

import main.content.OBJ_TYPE;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.core.state.GameState;
import main.game.battlefield.Coordinates;
import main.session.Session;
import main.system.net.DC_IdManager;

import java.util.HashMap;
import java.util.Map;

public class AT_Simulation extends Game {
    Map<ObjType, ArcaneEntity> instanceMap = new HashMap<>();
    Map<Integer, ArcaneEntity> map = new HashMap<>();

    public AT_Simulation() {
        init();
        setSimulation(true);
    }

    @Override
    public void init() {
        game = this;
        idManager = new DC_IdManager();
        state = new AT_State(this);
    }

    @Override
    public void initType(ObjType type) {
        super.initType(type);
        // VersionMaster.setVersionToCurrent(type); TODO selectively!

    }

    @Override
    public AT_State getState() {
        return (AT_State) super.getState();
    }

    public ArcaneEntity getEntity(Integer integer) {
        return map.get(integer);
    }

    public ArcaneEntity getInstance(ObjType type) {
        ArcaneEntity instance = instanceMap.get(type);
        if (instance == null) {
            instance = createInstance(type);
            instanceMap.put(type, instance);
        }
        return instance;
    }

    protected OBJ_TYPE[] getOBJ_TYPES() {
        return AT_OBJ_TYPE.values();
    }

    private ArcaneEntity createInstance(ObjType type) {
        switch ((AT_OBJ_TYPE) type.getOBJ_TYPE_ENUM()) {
            case DIRECTION:
                return new Direction(type);
            case GOAL:
                return new Goal(type);
            case SESSION:
                return new Session(type);
            case TASK:
                return new Task(type);

        }
        return null;
    }

    @Override
    public void start(boolean host) {

    }

    public Map<ObjType, ArcaneEntity> getInstanceMap() {
        return instanceMap;
    }

    public Map<Integer, ArcaneEntity> getMap() {
        return map;
    }

    public void add(ArcaneEntity entity) {
        getMap().put(entity.getId(), entity);
    }

    @Override
    public Obj getCellByCoordinate(Coordinates coordinates) {
        return null;
    }

    public class AT_State extends GameState {

        public AT_State(Game game) {
            super(game);
        }

        @Override
        protected void initTypeMaps() {
            for (OBJ_TYPE TYPE : AT_OBJ_TYPE.values()) {
                getObjMaps().put(TYPE, new HashMap<>());
            }
        }


    }

}
