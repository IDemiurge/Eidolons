package eidolons.entity.active.spaces;

import eidolons.entity.obj.unit.Unit;

public class FeatSpaceDcHandler {

    public void addActiveToCurrent(String name, Unit unit){
        FeatSpace space = unit.getSpellSpaces().getCurrent();

    }
}
