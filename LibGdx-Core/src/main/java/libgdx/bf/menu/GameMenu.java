package libgdx.bf.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import libgdx.gui.dungeon.panels.headquarters.town.TownPanel;
import libgdx.screens.menu.GenericMenu;
import libgdx.screens.menu.MenuItem;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/24/2017.
 */
public class GameMenu extends GenericMenu<GAME_MENU_ITEM> {
    public static boolean menuOpen;
    GameMenuHandler handler;

    public GameMenu() {
        super();
        setVisible(false);
    }

    @Override
    protected String getBgPath() {
        return StrPathBuilder.build(
                "ui", "components", "generic", "game menu", "background.png");
    }

    @Override
    protected void addButtons() {
        super.addButtons();
    }

    protected GameMenuHandler initHandler() {
        return new GameMenuHandler(this);
    }

    public void toggle() {
        if (GameMenu.menuOpen)
            close();
        else open();
    }

    @Override
    public void open() {
        getLoop().setPaused(true);
        GameMenu.menuOpen = true;
        super.open();
    }

    protected GameLoop getLoop() {
        return DC_Game.game.getLoop();
    }

    @Override
    public void close() {
        getLoop().setPaused(false);
        GameMenu.menuOpen = false;
        super.close();
    }

    @Override
    protected Boolean clicked(MenuItem item) {
        if (item.getItems().length > 0)
            return true;
        if (item instanceof GAME_MENU_ITEM) {
            clicked();
            return getHandler().clicked((GAME_MENU_ITEM) item);
        }

        return null;
    }


    public GameMenuHandler getHandler() {
        if (handler == null)
            handler = initHandler();
        return handler;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram bufferedShader = batch.getShader();
        if (batch.getShader() != null)
            batch.setShader(null);
        super.draw(batch, parentAlpha);
        if (batch.getShader() != null)
            batch.setShader(bufferedShader);
    }


    @Override
    protected List<MenuItem<GAME_MENU_ITEM>> getFullItemList() {
        return new ArrayList<>(Arrays.asList(GAME_MENU_ITEM.values()));
    }

    protected boolean isHidden(GAME_MENU_ITEM item) {
        if (TownPanel.getActiveInstance() != null) {
            if (item == GAME_MENU_ITEM.MAP_INFO
            ) {
                return true;
            }
        }
        if (item == GAME_MENU_ITEM.RETREAT)
            return !Flags.isMacro();
        return item.hidden;
    }

    @Override
    protected float getBottonPadding(int size) {
        return 200;
    }

    @Override
    protected float getTopPadding(int size) {
        return 280;
    }


    @Override
    protected FONT getFontStyle() {
        return FONT.MAGIC;
    }

    @Override
    protected ButtonStyled.STD_BUTTON getButtonStyle() {
        return ButtonStyled.STD_BUTTON.MENU;
    }


    public enum GAME_MENU_ITEM implements MenuItem<GAME_MENU_ITEM> {
        QUICK_HELP(true),
        HERO_INFO(true),
        MAP_INFO(true),
        TUTORIAL_RECAP(false),
        MANUAL(false),
        //        SEND_FEEDBACK,
//        SEND_LOG,
//        QUICK_RATE(), //WILL BE HIGHLIGHTED, OR SENT TO MY MAIN ADDRESS...
//        FEEDBACK(QUICK_RATE, SEND_FEEDBACK, SEND_LOG),
        OPTIONS,
        RESTART(true),
        PASS_TIME(true),
        //        QUESTS(),
        ACHIEVEMENTS(),
        RETREAT(true),
        SAVE(true),
        //        LOAD(true),
        RESUME,
        WEBSITE(true),
        ABOUT(true), LAUNCH_GAME(true),
        MAIN_MENU(true),
        OUTER_WORLD(true),
        EXIT(), //MAIN_MENU, OUTER_WORLD),
//        INFO(QUICK_HELP, TUTORIAL_RECAP, MANUAL),
        ;
        boolean hidden;
        private final GAME_MENU_ITEM[] items;

        GAME_MENU_ITEM(boolean hidden, GAME_MENU_ITEM... items) {
            this(items);
            this.hidden = hidden;
        }

        GAME_MENU_ITEM(GAME_MENU_ITEM... items) {
            this.items = items;
            for (GAME_MENU_ITEM sub : items) {
                sub.hidden = true;
            }
        }

        @Override
        public GAME_MENU_ITEM[] getItems() {
            return items;
        }
    }
}
