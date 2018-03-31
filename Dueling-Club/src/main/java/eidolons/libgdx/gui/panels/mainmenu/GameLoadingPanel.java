package eidolons.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.gui.SimpleClickListener;
import eidolons.libgdx.gui.panels.dc.TablePanel;

import static eidolons.libgdx.StyleHolder.getDefaultLabelStyle;
import static eidolons.libgdx.StyleHolder.getMainMenuButton;


public class GameLoadingPanel extends TablePanel {
    private final TextButton back;

    public GameLoadingPanel() {
        left().bottom();

        back = getMainMenuButton("back");

        final Label label = new Label("game loading under construction", getDefaultLabelStyle());
        label.setFontScale(2);
        add(label);
        row();
        add(back);
    }

    public void setBackCallback(Runnable backCallback) {
        back.addListener(new SimpleClickListener(backCallback));
    }
}
