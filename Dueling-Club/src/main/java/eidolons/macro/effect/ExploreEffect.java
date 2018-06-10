package eidolons.macro.effect;

import eidolons.macro.MacroGame;

public class ExploreEffect extends MacroEffect {

	/*
     * Intersecting Routes When In Progress on a Route,
	 * 
	 * Each route/place should have Concealment level Consider distance as well
	 */

    @Override
    public boolean applyThis() {

        MacroGame.getGame().getWorld();

        return false;
    }

}
