package main.entity.group;

import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GroupImpl implements GROUP<Obj> {
    List<Obj> objList;
    List<Integer> idList;
    private boolean ignoreGroupTargeting;

    public GroupImpl(Game game, Collection<Integer> idList) {
        this(game, idList, false);
    }

    public GroupImpl(Game game, Collection<Integer> idList,
                     boolean ignoreGroupTargeting) {

        this.setIgnoreGroupTargeting(ignoreGroupTargeting);
        this.idList = Arrays.asList(idList
         .toArray(new Integer[idList.size()]));
        objList = new ArrayList<>();
        for (Integer id : idList) {
            objList.add(game.getObjectById(id));
        }
    }

    public GroupImpl(Collection<Obj> objects) {
        this(objects, false);
    }

    public GroupImpl(Collection<Obj> objects, boolean ignoreGroupTargeting) {
        this.setIgnoreGroupTargeting(ignoreGroupTargeting);
        objList = Arrays.asList(objects.toArray(new Obj[objects
         .size()]));
        idList = new ArrayList<>();
        for (Obj obj : objList) {
            idList.add(obj.getId());
        }
    }

    @Override
    public String toString() {

        if (getObjects() == null) {
            return getObjectIds().toString();
        }
        return "Group: " + StringMaster.joinStringList(
         StringMaster.convertToStringList(getObjects()), ", ");
    }

    @Override
    public List<Obj> getObjects() {
        return objList;
    }

    @Override
    public List<Integer> getObjectIds() {

        return idList;
    }

    public boolean isIgnoreGroupTargeting() {
        return ignoreGroupTargeting;
    }

    public void setIgnoreGroupTargeting(boolean ignoreGroupTargeting) {
        this.ignoreGroupTargeting = ignoreGroupTargeting;
    }

}
