package eidolons.entity.active.spaces;

import eidolons.entity.active.DC_ActiveObj;
import main.content.enums.entity.NewRpgEnums;

import java.util.List;

public class ActiveSpace {

    protected int index;
    protected NewRpgEnums.ACTIVE_SPACE_TYPE type;
    protected NewRpgEnums.ACTIVE_SPACE_MODE mode;
    //protected  List<ActiveSpaceModifier> modifiers;
    protected  List<DC_ActiveObj> actives;
    protected String name;
    protected  boolean locked;
    protected boolean hidden;

    public ActiveSpace(int index, String name, NewRpgEnums.ACTIVE_SPACE_TYPE type, NewRpgEnums.ACTIVE_SPACE_MODE mode, List<DC_ActiveObj> actives) {
        this.name = name;
        this.type = type;
        this.index = index;
        this.mode = mode;
        this.actives = actives;
    }

    public static class ActiveSpaceMeta{
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

    public NewRpgEnums.ACTIVE_SPACE_TYPE getType() {
        return type;
    }

    public NewRpgEnums.ACTIVE_SPACE_MODE getMode() {
        return mode;
    }

    public List<DC_ActiveObj> getActives() {
        return actives;
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
}
