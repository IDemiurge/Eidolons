package main.client.game.gui;

import main.client.cc.logic.items.ItemGenerator;
import main.test.frontend.FAST_DC;

/**
 * Created by JustMe on 10/21/2016.
 */
public class GraphicsTestLauncher {

    public static void main(String[] args) {
        ItemGenerator.setGenerationOn(false);
        FAST_DC.main(new String[]{

                FAST_DC.PRESET_OPTION_ARG, "1"
        });
    }
}
