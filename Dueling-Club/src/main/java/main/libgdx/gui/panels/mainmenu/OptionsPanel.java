package main.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.SimpleClickListener;
import main.libgdx.gui.panels.dc.TablePanel;

import static main.libgdx.StyleHolder.getMainMenuButton;

public class OptionsPanel extends TablePanel {
    private final TextButton back;

    public OptionsPanel() {
        left().bottom();

        final TextButton button = getMainMenuButton("toggle full screen");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Gdx.graphics.isFullscreen()) {
                    Gdx.graphics.setWindowedMode(1600, 900);
                } else {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                }
            }
        });


        add(button);
        row();

        back = getMainMenuButton("back");
        add(back);
    }

    public void setBackCallback(Runnable callback) {
        back.addListener(new SimpleClickListener(callback));
    }
}
