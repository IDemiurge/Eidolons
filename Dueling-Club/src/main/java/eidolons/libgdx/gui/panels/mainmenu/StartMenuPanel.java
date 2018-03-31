package eidolons.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.gui.SimpleClickListener;
import eidolons.libgdx.gui.panels.dc.TablePanel;

import static eidolons.libgdx.StyleHolder.getMainMenuButton;


public class StartMenuPanel extends TablePanel {
    private final TextButton loadGameButton;
    private final TextButton optionsButton;
    private final TextButton mainMenuButton;
    private final TextButton exit;
    private TextButton continueButton = null;

    public StartMenuPanel() {
        this(false);
    }

    public StartMenuPanel(boolean canContinue) {
        left().bottom();

        if (canContinue) {
            continueButton = getMainMenuButton("continue");
            add(continueButton);
            row();

            addEmptyRow(0, (int) continueButton.getPrefHeight());
        }

        mainMenuButton = getMainMenuButton("new");
        add(mainMenuButton);
        row();
        loadGameButton = getMainMenuButton("load");
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

    public void setContinueCallback(Runnable continueCallback) {
        if (continueButton != null) {
            continueButton.addListener(new SimpleClickListener(continueCallback));
        }
    }
}
