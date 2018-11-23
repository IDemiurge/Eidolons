package eidolons.libgdx.screens.map.town.navigation.data;

import eidolons.macro.map.Place;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigableDungeon extends NavigableWrapper<Place> {

    public NavigableDungeon(Place obj) {
        super(obj);
    }

    @Override
    public void interact() {
        obj.getGame().getLoop().enter(obj);
    }


    @Override
    public String getDefaultActionTip() {
        return "Enter " + obj.getName();
    }
}
