package main.libgdx.screens.menu;

import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import main.system.graphics.FontMaster.FONT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 11/28/2017.
 */
public class MainMenu extends GenericMenu<MAIN_MENU_ITEM> {
    MainMenuHandler handler;

    public MainMenu( ) {
        super();
        this.handler = new MainMenuHandler();
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
        return  new ArrayList<>(Arrays.asList(MAIN_MENU_ITEM.values()));
    }
    @Override
    protected boolean isHidden(MAIN_MENU_ITEM item) {
        return item.secondary;
    }
    @Override
    protected FONT getFontStyle() {
        return FONT.AVQ;
    }

    @Override
    protected STD_BUTTON getButtonStyle() {
        return STD_BUTTON.GAME_MENU;
    }

    @Override
    protected Boolean clicked(MenuItem sub) {
        if (sub instanceof MAIN_MENU_ITEM)
            return handler.handle((MAIN_MENU_ITEM) sub);
        return null;
    }

    public enum MAIN_MENU_ITEM implements MenuItem<MAIN_MENU_ITEM> {
        CRAWL(), STANDOFF(), SKIRMISH(),  NEW_GAME(CRAWL, STANDOFF, SKIRMISH),
        OPTIONS,//(GAMEPLAY, AUDIO, ),
        MANUAL,
        ABOUT,
        EXIT, ;
        boolean secondary;
        private MAIN_MENU_ITEM[] items;

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
