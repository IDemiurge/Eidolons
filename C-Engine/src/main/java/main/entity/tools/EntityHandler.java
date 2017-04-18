package main.entity.tools;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.List;
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
    protected void log(String string, boolean gameLog) {
        if (gameLog) {
            getGame().getLogManager().log(string);
        } else {
            main.system.auxiliary.log.LogMaster.log(getLogChannel(), string);
        }
    }

    protected int getLogChannel() {
        return 1;
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

    public boolean checkProperty(PROPERTY p ) {
        return getEntity().checkProperty(p );
    }
    public boolean checkProperty(PROPERTY p, String value) {
        return getEntity().checkProperty(p, value);
    }

    public Ref getRef() {
        return getEntity().getRef();
    }

    public void setRef(Ref ref) {
        getEntity().setRef(ref);
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

    public String getValue(String param) {
        return getEntity().getValue(param);
    }
    public String getValue(VALUE param) {
        return getEntity().getValue(param);
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


    //                                   <><><><><>
    public void setParam(PARAMETER p, int i) {
        getEntity().setParam(p, i);
    }

    public void setParam(String param, int i) {
        getEntity().setParam(param, i);
    }

    public void setParamMax(PARAMETER p, int i) {
        getEntity().setParamMax(p, i);
    }

    public void setParamMin(PARAMETER p, int i) {
        getEntity().setParamMin(p, i);
    }

    public boolean setParam(PARAMETER param, String value, boolean quiety) {
        return getEntity().setParam(param, value, quiety);
    }

    public boolean setParam(PARAMETER param, String value) {
        return getEntity().setParam(param, value);
    }

    public void setProperty(PROPERTY name, String value, boolean base) {
        getEntity().setProperty(name, value, base);
    }

    public void setProperty(String prop, String value) {
        getEntity().setProperty(prop, value);
    }

    public void setProperty(PROPERTY prop, String value) {
        getEntity().setProperty(prop, value);
    }

    public void setValue(VALUE valName, String value) {
        getEntity().setValue(valName, value);
    }

    public void setValue(VALUE valName, String value, boolean base) {
        getEntity().setValue(valName, value, base);
    }

    public void setValue(String name, String value) {
        getEntity().setValue(name, value);
    }

    public void setValue(String name, String value, boolean base) {
        getEntity().setValue(name, value, base);
    }

    public void setPassivesReady(boolean passivesReady) {
        getEntity().setPassivesReady(passivesReady);
    }

    public void setRawValues(XLinkedMap<VALUE, String> rawValues) {
        getEntity().setRawValues(rawValues);
    }

    public void setParamMap(ParamMap paramMap) {
        getEntity().setParamMap(paramMap);
    }

    public void setPropMap(PropMap propMap) {
        getEntity().setPropMap(propMap);
    }

    public void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
        getEntity().setParam(param, i, quietly, base);
    }

    public void setParam(PARAMETER param, int i, boolean quietly) {
        getEntity().setParam(param, i, quietly);
    }

    public void setParamDouble(PARAMETER param, double i, boolean quietly) {
        getEntity().setParamDouble(param, i, quietly);
    }

    public void setParameter(PARAMETER param, int i) {
        getEntity().setParameter(param, i);
    }

    public boolean addProperty(PROPERTY prop, String value) {
        return getEntity().addProperty(prop, value);
    }

    public boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
        return getEntity().addProperty(prop, values, noDuplicates);
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
        return getEntity().addProperty(prop, value, noDuplicates);
    }

    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {
        return getEntity().addProperty(prop, value, noDuplicates, addInFront);
    }

    public void addProperty(String prop, String value) {
        getEntity().addProperty(prop, value);
    }

    public void addParam(PARAMETER parameter, String param, boolean base) {
        getEntity().addParam(parameter, param, base);
    }
}
