package main.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import main.libgdx.gui.SimpleClickListener;
import main.libgdx.gui.panels.dc.TablePanel;

import static main.libgdx.StyleHolder.getMainMenuButton;


public class MainMenuPanel extends TablePanel {
    private final TextButton loadGameButton;
    private final TextButton optionsButton;
    private final TextButton mainMenuButton;
    private final TextButton exit;

    public MainMenuPanel() {
        left().bottom();

        mainMenuButton = getMainMenuButton("new game");
        add(mainMenuButton);
        row();
        loadGameButton = getMainMenuButton("load game");
        add(loadGameButton);
        row();
        optionsButton = getMainMenuButton("options");
        add(optionsButton);
        row();

        exit = getMainMenuButton("exit");
        add(exit);
    }

    public void setExitCallback(Runnable exitCallback) {
        exit.addListener(new SimpleClickListener(exitCallback));
    }

    public void setLoadGameCallback(Runnable loadGameCallback) {
        loadGameButton.addListener(new SimpleClickListener(loadGameCallback));
    }

    public void setOptionsCallback(Runnable optionsCallback) {
        optionsButton.addListener(new SimpleClickListener(optionsCallback));
    }

    public void setNewGameCallback(Runnable newGameCallback) {
        mainMenuButton.addListener(new SimpleClickListener(newGameCallback));
    }
}
