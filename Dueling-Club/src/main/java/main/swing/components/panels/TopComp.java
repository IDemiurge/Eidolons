package main.swing.components.panels;

import main.game.core.game.DC_Game;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;

import java.awt.*;
import java.util.Arrays;

public class TopComp extends WrappedTextComp {

    private static final String PREFIX = "Round ";
    private static final int DEFAULT_Y = 70;
    private static final int X_OFFSET = 35;
    private static final int FONT_SIZE = 17;
    private DC_Game game;

    public TopComp(DC_Game game) {
        super((game.isSimulation()) ? VISUALS.TOP_HC : VISUALS.TOP);
        this.game = game;

    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getDefaultFont(FONT_SIZE);
    }

    @Override
    protected int getDefaultY() {
        return DEFAULT_Y;
    }

    @Override
    protected boolean isCentering() {
        return true;
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    // @Override
    // protected int recalculateX() {
    // return super.recalculateX() - X_OFFSET;
    // }

    @Override
    public void refresh() {
        if (!game.isSimulation()) {
            String name = "";
            try {
                name = StringMaster.getWellFormattedString(game.getDungeonMaster().getDungeonWrapper()
                        .getName());
            } catch (Exception e) {

            }
            if (name.isEmpty()) {
//                name = StringMaster.getWellFormattedString(game.getBattleMaster()
//                 .getMapName());
            }

            if (game.isDebugMode()) {
                name += StringMaster.wrapInCurlyBraces("" + game.getDungeon().getZ());
            }

            setTextLines(Arrays.asList(name, getRoundString()));
        }
        super.refresh();
    }

    private String getRoundString() {
        return getPrefix() + (game.getState().getRoundDisplayedNumber());
    }

}
