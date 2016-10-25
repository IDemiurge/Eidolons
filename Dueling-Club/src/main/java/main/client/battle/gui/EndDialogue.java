package main.client.battle.gui;

import main.client.battle.BattleManager;
import main.client.cc.CharacterCreator;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.game.DC_Game;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.misc.G_VisualComponent;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;

public class EndDialogue extends G_Dialog {
    private static final String EXIT = "Exit";
    private static final String CONTINUE = "Continue";
    private static final String STATS = "Stats";

    private static final String VICTORY_MESSAGE = "Victory, the day is yours!"; // Glorious
    // victory
    private static final String DEFEAT_MESSAGE = "You have been defeated...";
    private static final String VICTORY_PICTURE = "UI\\big\\victory.png";
    private static final String DEFEAT_PICTURE = "UI\\big\\defeat.jpg";

    private static final float FONT_SIZE_BUTTON = 17;
    private static final float FONT_SIZE_MESSAGE = 22;
    private static final int OFFSET_X = 21;
    private static final int OFFSET_Y = 15;
    VISUALS V = VISUALS.END_PANEL;
    private Boolean victory;
    private Integer glory;
    private G_Panel panel;
    // private GraphicComponent gloryImgComp;
    // private TextComp gloryComp;
    private TextCompDC messageComp;
    private G_VisualComponent pictureComp;
    private CustomButton continueButton;
    private CustomButton statsButton;
    private CustomButton exitButton;
    private DC_Game game;

    public EndDialogue(BattleManager battleManager) {
        victory = battleManager.getOutcome(); // TODO
        glory = battleManager.getGlory();
        game = battleManager.getGame();
        init();
    }

    public void init() {
        // gloryImgComp = new GraphicComponent(STD_COMP_IMAGES.GLORY);
        // gloryComp = new TextComp(VISUALS.POOL, "+ " + glory);
        messageComp = new TextCompDC(null, getMessage()) {
            protected Font getDefaultFont() {
                return FontMaster.getFont(FONT.AVQ, FONT_SIZE_MESSAGE, Font.PLAIN);
            }
        };

        pictureComp = new G_VisualComponent(getPicturePath());

        continueButton = new EndButton(VISUALS.BUTTON, CONTINUE) {
            public void handleClick() {

                initHC();
            }
        };

        statsButton = new EndButton(VISUALS.BUTTON, STATS) {
            public void handleClick() {
                initStatsScreen();
            }
        };

        exitButton = new EndButton(VISUALS.BUTTON, EXIT) {

            public void handleClick() {
                exit();
            }
        };
        super.init();

        // frame.setAlwaysOnTop(false);
    }

    @Override
    public Component createComponent() {
        panel = new G_Panel(V);

        panel.add(new RewardComponent(glory, STD_COMP_IMAGES.GLORY) {
            protected String getText(int value) {
                String s = super.getText(value);
                if (glory > 0)
                    try {
                        s += "/" + game.getParty().getMembers().size() + "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return s;

            }

            ;
        }, "@id gp, pos pc.x2 center_y")

        ;

        panel.add(messageComp, "@id mc, pos center_x max_top");
        panel.add(pictureComp, "@id pc, pos " + OFFSET_X + " center_y");
        if (CharacterCreator.isArcadeMode() || Launcher.DEV_MODE)
            panel.add(continueButton, "@id cb, pos max_right-" + OFFSET_X + " max_bottom-"
                    + OFFSET_Y);
        panel.add(exitButton, "@id eb, pos " + OFFSET_X + " max_bottom-" + OFFSET_Y);
        panel.add(statsButton, "@id sb, pos center_x max_bottom-" + OFFSET_Y);
        Shape s;
        return panel;
    }

    protected Font getButtonFont() {
        return FontMaster.getFont(FONT.DARK, FONT_SIZE_BUTTON, Font.PLAIN);
    }

    protected void exit() {
        if (Launcher.isRunning())
            Launcher.getMainManager().exitToMainMenu();
        close();
    }

    protected void initStatsScreen() {
        // Launcher.setView(statsScreen VIEWS.STATS);
        // close();
    }

    protected void initHC() {
        Launcher.resetView(VIEWS.HC);
        // reload the party!
        close();
    }

    private String getPicturePath() {
        return victory ? VICTORY_PICTURE : DEFEAT_PICTURE;
    }

    private String getMessage() {
        return victory ? VICTORY_MESSAGE : DEFEAT_MESSAGE;
    }

    @Override
    public String getTitle() {
        return (victory) ? "Victory!" : "Defeat!";
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    @Override
    public boolean isCentered() {
        return true;
    }

    @Override
    public Dimension getSize() {
        return V.getSize();
    }

    public abstract class EndButton extends CustomButton {

        public EndButton(VISUALS v, String text) {
            super(v, text);
        }

        @Override
        protected int getDefaultY() {
            return super.getDefaultY() * 7 / 4;
        }

        protected Font getDefaultFont() {
            return getButtonFont();
        }
    }

    public class RewardComponent extends G_Panel {
        public RewardComponent(int value, STD_COMP_IMAGES img) {
            TextCompDC comp = new TextCompDC(VISUALS.POOL, getText(value)) {
                protected int getDefaultFontSize() {
                    return 15;
                }
            };
            add(comp, "id c, pos ic.x2-4 ic.y+6");
            add(new GraphicComponent(img), "id ic, pos 0 0");

        }

        protected String getText(int value) {
            if (value < 0)
                return value + "";
            return "+ " + value;
        }
    }

}
