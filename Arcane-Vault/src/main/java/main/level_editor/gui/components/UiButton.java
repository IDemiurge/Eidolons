package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class UiButton extends VisTextButton {

    public UiButton(String title, Runnable runnable) {
        super(title, new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                runnable.run();
            }
        });

    }
}
