package eidolons.libgdx.bf.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.game.core.GameLoop;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.menu.GameMenu.GAME_MENU_ITEM;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.screens.menu.GenericMenu;
import eidolons.libgdx.screens.menu.MenuItem;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.CoreEngine;

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
        if (item instanceof GAME_MENU_ITEM)
        {
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
        if (item==GAME_MENU_ITEM.RUN)
            return !CoreEngine.isMacro();
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
    protected STD_BUTTON getButtonStyle() {
        return STD_BUTTON.GAME_MENU;
    }



    public enum GAME_MENU_ITEM implements MenuItem<GAME_MENU_ITEM> {
        QUICK_HELP,
        HERO_INFO,
        MANUAL,
        //        SEND_FEEDBACK,
//        SEND_LOG,
//        QUICK_RATE(), //WILL BE HIGHLIGHTED, OR SENT TO MY MAIN ADDRESS...
//        FEEDBACK(QUICK_RATE, SEND_FEEDBACK, SEND_LOG),
        OPTIONS,
        RESTART(true),
        PASS_TIME,
        RUN(true),
        SAVE(true),
        LOAD(true),
        RESUME,
        INFO(QUICK_HELP, HERO_INFO, MANUAL),
        WEBSITE(true),
        ABOUT(true), LAUNCH_GAME(true),
        MAIN_MENU(true),
        OUTER_WORLD(true),
        EXIT(), //MAIN_MENU, OUTER_WORLD),
        ;
        boolean hidden;
        private GAME_MENU_ITEM[] items;

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
