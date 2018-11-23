package eidolons.libgdx.screens.map.town.navigation.data;

import eidolons.macro.entity.town.TownPlace;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigableTownPlace extends NavigableWrapper<TownPlace> {


    public NavigableTownPlace(TownPlace obj) {
        super(obj);
    }

    @Override
    public void interact() {

    }

    @Override
    public String getDefaultActionTip() {
        return "Enter ";
    }
}
