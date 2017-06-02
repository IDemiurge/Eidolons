package main.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.libgdx.gui.SimpleClickListener;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.screens.ScreenData;

import java.util.List;
import java.util.function.Consumer;

import static main.libgdx.StyleHolder.getMainMenuButton;

public class NewGamePanel extends TablePanel {
    private final TextButton back;
    private final TextButton startDemoScenario;
    private Consumer<ScreenData> choiceCallback;

    public NewGamePanel() {
        left().bottom();

        startDemoScenario = getMainMenuButton("start demo scenario");

        add(startDemoScenario);
        row();

        back = getMainMenuButton("back");
        add(back);
    }

    @Override
    public void updateAct(float delta) {
        final List<ScreenData> dataList = (List<ScreenData>) getUserObject();
        dataList.forEach(data -> {
            TextButton button = getMainMenuButton(data.getName());
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (choiceCallback != null) {
                        choiceCallback.accept(data);
                    }
                }
            });
        });
    }

    public void setBackCallback(Runnable callback) {
        back.addListener(new SimpleClickListener(callback));
    }

    public void setStartDemoScenarioCallback(Consumer<ScreenData> callback) {
        choiceCallback = callback;
    }
}
