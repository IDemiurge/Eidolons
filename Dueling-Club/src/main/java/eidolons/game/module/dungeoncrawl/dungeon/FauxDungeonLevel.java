package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.RestoredDungeonLevel;

public class FauxDungeonLevel extends RestoredDungeonLevel {

    public FauxDungeonLevel(String name) {
        super(name);
    }

    @Override
    public boolean isVoid(int i, int j) {
        return super.isVoidExplicit(i, j);
    }
}
