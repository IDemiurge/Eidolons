package libgdx.screens.menu;

import libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class MainMenu extends GenericMenu<MAIN_MENU_ITEM> {
    private static MainMenu instance;
    MainMenuHandler handler;

    private MainMenu() {
        super();
        instance = this;
        this.handler = new MainMenuHandler(this);
    }

    @Override
    protected String getBgPath() {
        return null;
    }

    public static MainMenu getInstance() {
        if (instance != null) return instance;

        instance = new MainMenu();
        return instance;
    }


    @Override
    protected float getBottonPadding(int size) {
        return 200;
    }

    @Override
    protected float getTopPadding(int size) {
        return 200;
    }

    @Override
    protected List<MenuItem<MAIN_MENU_ITEM>> getFullItemList() {
        return new ArrayList<>(Arrays.asList(MAIN_MENU_ITEM.values()));
    }

    @Override
    protected boolean isHidden(MAIN_MENU_ITEM item) {
//        if (CoreEngine.isIggDemo())
//        {
//            switch (item) {
//                case RANDOM_SCENARIO:
//                case SELECT_SCENARIO:
//                case CUSTOM_LAUNCH:
//                case PLAY:
//                case DEMO:
//                case EXIT:
//                case OPTIONS:
//                   return false;
//            }
//            return true;
//        }
        return item.secondary;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.MAGIC;
    }

    @Override
    protected STD_BUTTON getButtonStyle() {
        return STD_BUTTON.MENU;
    }

    @Override
    protected Boolean clicked(MenuItem sub) {
        if (sub instanceof MAIN_MENU_ITEM)
        {
            clicked();
            return handler.handle((MAIN_MENU_ITEM) sub);
        }
        setVisible(true);
        return false;
    }

    public MainMenuHandler getHandler() {
        return handler;
    }



    public enum MAIN_MENU_ITEM implements MenuItem<MAIN_MENU_ITEM> {
        TEST_MODE(false),
        CONTINUE(true),
        CUSTOM_DUNGEON(false),
        // STANDOFF(true), PART OF CD?
        // SKIRMISH(true),
        RANDOM_SCENARIO(false),
        SELECT_SCENARIO(false),
        PLAY(TEST_MODE, SELECT_SCENARIO, RANDOM_SCENARIO, CUSTOM_DUNGEON),//NEXT_SCENARIO, ),
        MAP_PREVIEW(),
        LOAD(true),
        OPTIONS,
        CREDITS(true),
        MANUAL(true),
        ABOUT(true),
        EXIT;
        boolean secondary;
        private MAIN_MENU_ITEM[] items;

        MAIN_MENU_ITEM(boolean secondary) {
            this.secondary = secondary;
        }

        MAIN_MENU_ITEM(MAIN_MENU_ITEM... items) {
            this.items = items;
            for (MAIN_MENU_ITEM sub : items) {
                sub.secondary = true;
            }
        }

        @Override
        public MAIN_MENU_ITEM[] getItems() {
            return items;
        }
    }

}
