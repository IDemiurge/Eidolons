package main.game.core.game;

import main.ability.effects.EffectManager;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.ValueManager;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.GenericItemGenerator;
import main.data.xml.XML_Reader;
import main.elements.conditions.RequirementsManager;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.GenericVisionManager;
import main.game.bf.GraveyardManager;
import main.game.bf.MovementManager;
import main.game.core.state.GameState;
import main.game.logic.battle.turn.TurnManager;
import main.game.logic.event.Event;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.Log;
import main.system.entity.ConditionMaster;
import main.system.entity.IdManager;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.text.LogManager;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class Game implements Serializable {
    public static Game game;
    protected GameObjMaster master;
    protected GameState state;
    protected IdManager idManager;
    protected GameManager manager;
    protected GenericItemGenerator itemGenerator;
    protected MathMaster mathManager;
    protected EffectManager effectManager;

    protected ConditionMaster conditionMaster;
    protected RequirementsManager requirementsManager;
    protected ValueManager valueManager;
    protected LogManager logManager;
    protected Log log;

    protected boolean simulation;
    protected boolean debugMode;
    protected boolean offline = true;
    protected boolean initialized = false;
    protected boolean started = false;
    private boolean running;
    private boolean dummyMode;
    private boolean cloningMode;

    public Game() {

    }


    public Obj getObjectById(Integer id) {
        return master.getObjectById(id);
    }

    public boolean fireEvent(Event event) {
        return manager.handleEvent(event);
    }

    public GameObjMaster getMaster() {
        return master;
    }

    public Set<Integer> getObjectIds() {
        return getState().getObjects().keySet();
    }

    public Collection<Obj> getObjects() {
        return getState().getObjects().values();
    }

    public IdManager getIdManager() {
        return idManager;
    }

    public void setIdManager(IdManager idManager) {
        this.idManager = idManager;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public abstract void init();

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public GameState getState() {
        return state;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public abstract void start(boolean host);

    public ActionManager getActionManager() {
        return null;
    }

    public GenericItemGenerator getItemGenerator() {
        return itemGenerator;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;

    }

    public MathMaster getMathManager() {
        return mathManager;
    }


    public GameManager getManager() {
        return manager;
    }

    public void setManager(GameManager gameManager) {
        this.manager = gameManager;
    }


    public ObjType getTypeById(Integer id) {
        return getMaster().getTypeById(id);
    }

    public void initObjTypes() {
        for (OBJ_TYPE TYPE : getOBJ_TYPES()) {
            if (TYPE.getCode() == -1) {
                continue;
            }
            initTYPE(TYPE);
        }

    }

    protected OBJ_TYPE[] getOBJ_TYPES() {
        if (CoreEngine.isArcaneVault())
            if (XML_Reader.isMacro())
                return MACRO_OBJ_TYPES.values();
        return DC_TYPE.values();
    }

    protected void initTYPE(OBJ_TYPE TYPE) {
        try {
            for (ObjType type : DataManager.getTypes(TYPE)) {
                try {
                    initType(type);

                } catch (Exception e) {
                    LogMaster.log(1, "type init failed: " + type);
                    main.system.ExceptionMaster.printStackTrace(e);
                    continue;
                }
            }
        } catch (Exception e) {
            // main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    @Override
    public String toString() {
        return (simulation) ? "simulation-" + hashCode() : "game-" + hashCode();
    }

    public void initType(ObjType type) {

        if (type.isInitialized() && type.getGame() != null) {
            if (type.getOBJ_TYPE_ENUM() == DC_TYPE.CHARS
             || type.getOBJ_TYPE_ENUM() == DC_TYPE.PARTY) {
                LogMaster.log(0, type + " already initialized for " + type.getGame() + " with id: "
                 + type.getId());
            }
            type.setGame(this);
            return;
        }
        type.setId(idManager.getNewTypeId());
        getState().getTypeMap().put(type.getId(), type);
        type.setGame(this);
        type.setRef(new Ref(this, type.getId()));
        state.getTypeMap().put(type.getId(), type);
        type.setInitialized(true);
    }

    public MovementManager getMovementManager() {
        return null;
    }

    public TurnManager getTurnManager() {
        return null;
    }

    public GraveyardManager getGraveyardManager() {
        return null;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }


    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        LogMaster.log(1, "simulation= " + simulation);
        this.simulation = simulation;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public RequirementsManager getRequirementsManager() {
        return requirementsManager;
    }

    public void setRequirementsManager(RequirementsManager requirementsManager) {
        this.requirementsManager = requirementsManager;
    }

    public abstract Obj getCellByCoordinate(Coordinates coordinates);

    public Collection<Obj> getObjects(OBJ_TYPE TYPE) {
        if (TYPE instanceof DC_TYPE) {
            return new ArrayList<>(getState().getObjMaps().get(TYPE).values());
        }
        Collection<Obj> list = new ArrayList<>();
        if (TYPE instanceof C_OBJ_TYPE) {
            for (DC_TYPE type : ((C_OBJ_TYPE) TYPE).getTypes()) {
                list.addAll(getState().getObjMaps().get(type).values());
            }
        }
        return list;
    }

    public boolean isDummyMode() {
        return dummyMode;
    }

    public void setDummyMode(boolean dummyMode) {
        this.dummyMode = dummyMode;
    }

    public JFrame getWindow() {
        return null;
    }


    public ValueManager getValueManager() {
        return valueManager;
    }

    public ConditionMaster getConditionMaster() {
        return conditionMaster;
    }

    public GenericVisionManager getVisionMaster() {
        return null;
    }



    public boolean isOnline() {
        return !isOffline();
    }

    public boolean isCloningMode() {
        return cloningMode;
    }

    public void setCloningMode(boolean cloningMode) {
        this.cloningMode = cloningMode;
    }

    public void removed(Obj obj) {

    }
}
