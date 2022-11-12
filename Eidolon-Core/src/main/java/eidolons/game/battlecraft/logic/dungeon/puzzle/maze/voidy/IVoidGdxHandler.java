package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.entity.unit.Unit;

public interface IVoidGdxHandler {
    void initVisuals();

    void toggleAutoOn(Unit mainHero);

    void ended();

    void cleanUp();

    void setCollapsePeriod(float collapsePeriod);

    void setCanDropHero(boolean b);

    void setUnmark(boolean b);

    void setPuzzle(VoidMaze puzzle);
}
