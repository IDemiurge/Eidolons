package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.DC_Obj;
import main.data.XLinkedMap;

import java.util.Map;

/**
 * Created by JustMe on 4/1/2018.
 */
public class GenericMapper<O, T> {

    Map<O, Map<DC_Obj, T>> map = new XLinkedMap<>();

    public GenericMapper() {
    }

    public void reset() {
        if (isClearRequired())
            map.clear();
    }

    protected boolean isClearRequired() {
        return false;
    }

    public void log() {
        for (O unit : map.keySet()) {
            log(unit);
        }
    }

    public void log(O unit) {
        log(unit, map.get(unit).keySet().toArray(new DC_Obj[map.get(unit).size()]));
    }

    public void log(O unit, DC_Obj... objects) {

        main.system.auxiliary.log.LogMaster.log(1, unit + "'s " + toString());
        for (DC_Obj object : objects) {
            try {
                main.system.auxiliary.log.LogMaster.log(1, object.getNameAndCoordinate() + " has "
                 + map.get(unit).get(object));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    public void set(O source, DC_Obj object,
                    T t) {
        if (t == getNullEquivalent()) {
            getMap(source).remove(object);
        }
        getMap(source).put(object, t);
    }

    public T get(O source, DC_Obj object) {
        T result = getMap(source).get(object);
        if (result == null)
            return getNullEquivalent();
        return result;
    }

    protected T getNullEquivalent() {
        return null;
    }

    protected Map<DC_Obj, T> getMap(O source) {
        Map<DC_Obj, T> map = this.map.get(source);
        if (map == null) {
            map = new XLinkedMap<>();
            this.map.put(source, map);
        }
        return map;
    }

}