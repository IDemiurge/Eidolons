package eidolons.entity.active.spaces;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.NewRpgEnums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveSpace {

    protected int index;
    protected NewRpgEnums.ACTIVE_SPACE_TYPE type;
    protected NewRpgEnums.ACTIVE_SPACE_MODE mode;
    //protected  List<ActiveSpaceModifier> modifiers;
    protected String name;
    protected boolean locked;
    protected boolean hidden;
    protected Map<Integer, DC_ActiveObj> activesMap;
    protected Unit owner;

    public ActiveSpace(int index, String name, Unit owner, NewRpgEnums.ACTIVE_SPACE_TYPE type,
                       NewRpgEnums.ACTIVE_SPACE_MODE mode, Map<Integer, DC_ActiveObj> activesMap) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.index = index;
        this.mode = mode;
        this.activesMap = activesMap;
    }


    public static class ActiveSpaceMeta {
        //for visuals?
        String description;
        NewRpgEnums.ACTIVE_SPACE_SKIN skin;
        boolean darkened;
        boolean grayOut;

        public ActiveSpaceMeta(String description, NewRpgEnums.ACTIVE_SPACE_SKIN skin, boolean darkened, boolean grayOut) {
            this.description = description;
            this.skin = skin;
            this.darkened = darkened;
            this.grayOut = grayOut;
        }
    }

    public DC_ActiveObj get(int slot) {
        return activesMap.get(slot);
    }

    public NewRpgEnums.ACTIVE_SPACE_TYPE getType() {
        return type;
    }

    public NewRpgEnums.ACTIVE_SPACE_MODE getMode() {
        return mode;
    }

    public List<DC_ActiveObj> getDisplayedActives() {
        return new ArrayList<>(activesMap.values());
    }

    public Map<Integer, DC_ActiveObj> getActivesMap() {
        return activesMap;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setActivesMap(Map<Integer, DC_ActiveObj> activesMap) {
        this.activesMap = activesMap;
    }

    public Unit getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return name + ":" + type + "/" + mode + "/" + "\n - " + activesMap;
    }
}
