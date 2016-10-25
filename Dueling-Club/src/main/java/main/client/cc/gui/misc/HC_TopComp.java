package main.client.cc.gui.misc;

import main.game.DC_Game;
import main.swing.components.panels.TopComp;

import java.util.Arrays;

public class HC_TopComp extends TopComp {

    private static final String HERO = "Dungeon";
    private static final String CREATOR = "Vaults";

    public HC_TopComp(DC_Game game) {
        super(game);

        refresh();
    }

    @Override
    public void refresh() {
        setTextLines(Arrays.asList(HERO, CREATOR));
    }
}
