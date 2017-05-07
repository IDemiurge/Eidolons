package main.game.module.adventure.effect;

import main.game.module.adventure.MacroGame;

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
