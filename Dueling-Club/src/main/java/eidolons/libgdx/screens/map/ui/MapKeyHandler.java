package eidolons.libgdx.screens.map.ui;

import eidolons.macro.MacroGame;
import eidolons.system.controls.GlobalController;

/**
 * Created by JustMe on 2/25/2018.
 */
public class MapKeyHandler extends GlobalController{



    public boolean handleKeyTyped(char character) {

        switch (character) {
            case ' ':
                MacroGame.getGame().getLoop().togglePaused();
                return true;
        }

        if (charTyped(character))
            return true;
        return false;

    }
}
