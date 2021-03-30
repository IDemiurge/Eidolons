package eidolons.entity.active.spaces;

import eidolons.entity.obj.unit.Unit;

public class ActiveSpaceDcHandler {

    public void addActiveToCurrent(String name, Unit unit){
        ActiveSpace space = unit.getActiveSpaces().getCurrent();

    }
}
