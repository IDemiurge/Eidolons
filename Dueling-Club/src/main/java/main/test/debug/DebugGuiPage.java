package main.test.debug;

import main.game.core.game.DC_Game;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.StringMaster;
import main.test.debug.DebugMaster.DEBUG_FUNCTIONS;

import java.util.List;

public class DebugGuiPage extends G_Panel {

    public static final VISUALS BUTTON_VISUALS = VISUALS.BUTTON_NEW_SMALL;
    public static final int wrap = 4;
    public static final int gapAt = 2;
    public static final int middleGapWidth = VISUALS.MENU_BUTTON.getWidth();
    public static final int gapY = 1;
    public static final int gapX = 3;

    public DebugGuiPage(List<DEBUG_FUNCTIONS> list) {
        int column = 0;
        int i = 0;
        for (DEBUG_FUNCTIONS func : list) {
            if (func == null) {
                continue;
            }
            CustomButton b = createButton(func);
            if (i + 1 == wrap) {
                column++;
                i = 0;
            }
            int x = column * (BUTTON_VISUALS.getWidth() + gapX);
            int y = i * (BUTTON_VISUALS.getHeight() + gapX);
            if (column >= gapAt) {
                x += middleGapWidth;
            }
            Object pos = "pos " + x + " " + y;
            add(b, pos);
            i++;
        }

    }

    private CustomButton createButton(final DEBUG_FUNCTIONS func) {
        return new CustomButton(BUTTON_VISUALS, StringMaster
                .getWellFormattedString(func.toString())) {

            public void handleClick() {
                DC_Game.game.getDebugMaster().executeDebugFunctionNewThread(func);
            }

            @Override
            protected void handleRightClick() {
                handleAltClick();
            }

            @Override
            public void handleAltClick() {
                DebugMaster.setAltMode(true);
                try {
                    DC_Game.game.getDebugMaster().executeDebugFunctionNewThread(func);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                } finally {

                }
            }
        };
    }
}
