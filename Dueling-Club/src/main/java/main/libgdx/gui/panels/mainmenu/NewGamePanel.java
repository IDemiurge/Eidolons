package main.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.SimpleClickListener;
import main.libgdx.gui.panels.dc.TablePanel;

import java.util.function.Consumer;

import static main.libgdx.StyleHolder.getMainMenuButton;

public class NewGamePanel extends TablePanel {
    private final TextButton back;
    private final TextButton startDemoScenario;

    public NewGamePanel() {
        left().bottom();

        startDemoScenario = getMainMenuButton("start demo scenario");

        add(startDemoScenario);
        row();

        back = getMainMenuButton("back");
        add(back);
    }

    public void setBackCallback(Runnable callback) {
        back.addListener(new SimpleClickListener(callback));
    }

    public void setStartDemoScenarioCallback(Consumer<String> callback) {
        startDemoScenario.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callback.accept("demo");
            }
        });
    }
}
