package main.entity.tools;

import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 2/15/2017.
 */
public class EntityHandler<E extends Entity> {
    protected E entity;
    protected EntityMaster<E> master;
    protected Game game;

    public EntityHandler(E entity, EntityMaster<E> entityMaster) {
        this.master = entityMaster;
        this.entity = entity;
        this.game = entity.getGame();
    }

    @Override
    public String toString() {
        return StringMaster.getPossessive(getEntity().getName()) + " " + getClass().getSimpleName();
    }

    public E getEntity() {
        return entity;
    }

    public EntityMaster<E> getMaster() {
        return master;
    }

    public Integer getIntParam(PARAMETER param) {
        return getEntity().getIntParam(param);
    }

    public String getProperty(PROPERTY prop) {
        return getEntity().getProperty(prop);
    }

    public Map<PROPERTY, Map<String, Boolean>> getPropCache(boolean base) {
        return getEntity().getPropCache(base);
    }

    public String getProperty(PROPERTY prop, boolean base) {
        return getEntity().getProperty(prop, base);
    }

    public HashMap<PROPERTY, Map<String, Boolean>> getPropCache() {
        return getEntity().getPropCache();
    }

    public boolean checkParameter(PARAMETER param, int value) {
        return getEntity().checkParameter(param, value);
    }

    public boolean checkProperty(PROPERTY p, String value) {
        return getEntity().checkProperty(p, value);
    }

    public Ref getRef() {
        return getEntity().getRef();
    }

    public ObjType getType() {
        return getEntity().getType();
    }

    public String getName() {
        return getEntity().getName();
    }

    public String getParam(String p) {
        return getEntity().getParam(p);
    }

    public String getParam(PARAMETER param) {
        return getEntity().getParam(param);
    }

    public Double getParamDouble(PARAMETER param) {
        return getEntity().getParamDouble(param);
    }

    public Double getParamDouble(PARAMETER param, boolean base) {
        return getEntity().getParamDouble(param, base);
    }

    public String getProperty(String prop) {
        return getEntity().getProperty(prop);
    }

    public String getProp(String prop) {
        return getEntity().getProp(prop);
    }

    public Integer getId() {
        return getEntity().getId();
    }

    public Game getGame() {
        return game;
    }
    //                                   <><><><><>

    public EntityAnimator getAnimator() {
        return getMaster().getAnimator();
    }

    public EntityLogger getLogger() {
        return getMaster().getLogger();
    }

    public EntityInitializer getInitializer() {
        return getMaster().getInitializer();
    }

    public EntityCalculator getCalculator() {
        return getMaster().getCalculator();
    }

    public EntityChecker getChecker() {
        return getMaster().getChecker();
    }

    public EntityHandler getHandler() {
        return getMaster().getHandler();
    }

    public EntityResetter getResetter() {
        return getMaster().getResetter();
    }
}
