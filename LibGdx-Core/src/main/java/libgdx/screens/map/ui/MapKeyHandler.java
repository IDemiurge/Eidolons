package libgdx.screens.map.ui;

import eidolons.macro.MacroGame;
import libgdx.controls.GlobalController;

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

        return charTyped(character);

    }
}
