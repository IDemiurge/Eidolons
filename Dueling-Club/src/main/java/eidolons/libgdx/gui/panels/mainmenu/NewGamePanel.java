package eidolons.libgdx.gui.panels.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.gui.SimpleClickListener;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.screens.ScreenData;

import java.util.List;
import java.util.function.Consumer;

import static eidolons.libgdx.StyleHolder.getMainMenuButton;

public class NewGamePanel extends TablePanel {
    private TextButton back;
    private Consumer<ScreenData> choiceCallback;
    private SimpleClickListener backListener;

    public NewGamePanel() {
        left().bottom();
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

            add(button);
            row();
        });

        addEmptyRow(0, 30);
        back = getMainMenuButton("back");
        if (backListener != null) {
            back.addListener(backListener);
        }
        add(back);
    }

    public void setBackCallback(Runnable callback) {
        backListener = new SimpleClickListener(callback);
    }

    public void setStartDemoScenarioCallback(Consumer<ScreenData> callback) {
        choiceCallback = callback;
    }
}
