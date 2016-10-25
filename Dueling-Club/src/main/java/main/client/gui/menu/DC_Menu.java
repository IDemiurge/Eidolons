package main.client.gui.menu;

import main.client.DC_MainMenu;
import main.swing.generic.components.misc.G_ListMenu;

import java.util.Arrays;

public class DC_Menu extends G_ListMenu {

    public static final String[] mainItems = {"Play"};
    private DC_MainMenu mainMenu;

    public DC_Menu() {
        super(Arrays.asList(mainItems));
    }

    public DC_Menu(DC_MainMenu mainMenu) {
        this();
        this.mainMenu = mainMenu;

    }

    public void handleClick(String text) {
        switch (text) {
            case "Play": {
                // setView(new PlayMenu());
                mainMenu.setLoginView();
                return;
            }
        }
    }

}
