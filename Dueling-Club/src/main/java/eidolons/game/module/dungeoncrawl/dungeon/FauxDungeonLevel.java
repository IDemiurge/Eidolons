package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.RestoredDungeonLevel;

public class FauxDungeonLevel extends RestoredDungeonLevel {

    @Override
    public boolean isVoid(int i, int j) {
        return super.isVoidExplicit(i, j);
    }
}
