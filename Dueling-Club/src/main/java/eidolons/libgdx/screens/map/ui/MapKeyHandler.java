package eidolons.libgdx.screens.map.ui;

import eidolons.game.module.adventure.MacroGame;

/**
 * Created by JustMe on 2/25/2018.
 */
public class MapKeyHandler {

    public boolean keyDown(int keycode) {

        return false;
    }

    public boolean handleKeyTyped(char character) {
        switch (character) {
            case ' ':
                MacroGame.getGame().getLoop().togglePaused();
                return true;
        }

        return false;

    }
}
