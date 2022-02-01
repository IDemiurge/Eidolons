package eidolons.entity.active.spaces;

import eidolons.entity.unit.Unit;
import main.content.enums.entity.NewRpgEnums;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatSpace {

    protected int index;
    protected NewRpgEnums.FeatSpaceType type;
    protected NewRpgEnums.FeatSpaceMode mode;
    //protected  List<ActiveSpaceModifier> modifiers;
    protected String name;
    protected boolean locked, hidden, active;
    protected Map<Integer, Feat> featMap;
    protected Unit owner;

    public FeatSpace(int index, String name, Unit owner, NewRpgEnums.FeatSpaceType type,
                     NewRpgEnums.FeatSpaceMode mode, Map<Integer, Feat> featMap) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.index = index;
        this.mode = mode;
        this.featMap = featMap;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public static class ActiveSpaceMeta {
        //for visuals?
        String description;
        NewRpgEnums.FeatSpaceSkin skin;
        boolean darkened;
        boolean grayOut;

        public ActiveSpaceMeta(String description, NewRpgEnums.FeatSpaceSkin skin, boolean darkened, boolean grayOut) {
            this.description = description;
            this.skin = skin;
            this.darkened = darkened;
            this.grayOut = grayOut;
        }
    }

    public Feat get(int slot) {
        return featMap.get(slot);
    }

    public NewRpgEnums.FeatSpaceType getType() {
        return type;
    }

    public NewRpgEnums.FeatSpaceMode getMode() {
        return mode;
    }

    public List<Feat> getFeats() {
        return new ArrayList<>(featMap.values());
    }

    public Map<Integer, Feat> getFeatMap() {
        return featMap;
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

    public void setFeatMap(Map<Integer, Feat> featMap) {
        this.featMap = featMap;
    }

    public Unit getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return name + ":" + type + "/" + mode + "/" + "\n - " + featMap;
    }
}
