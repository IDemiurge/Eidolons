package eidolons.entity.feat.spaces;

import eidolons.entity.unit.Unit;

public class FeatSpaceUiHandler {



    public void addActiveToCurrent(String name, Unit unit){
        FeatSpace space = unit.getSpellSpaces().getCurrent();

    }
}
