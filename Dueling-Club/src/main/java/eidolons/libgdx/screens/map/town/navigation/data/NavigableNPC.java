package eidolons.libgdx.screens.map.town.navigation.data;

import eidolons.game.module.adventure.entity.npc.NPC;

/**
 * Created by JustMe on 11/21/2018.
 */
public class NavigableNPC extends NavigableWrapper<NPC> {

    public NavigableNPC(NPC obj) {
        super(obj);
    }

    @Override
    public void interact() {

    }

    @Override
    public String getDefaultActionTip() {
        return "Talk to ";
    }
}
