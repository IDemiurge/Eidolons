package main.level_editor.gui.panels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;

public class ClosablePanel extends TablePanelX {
    SmartButton linkedButton;
    public void setLinkedButton(SmartButton linkedButton) {
        this.linkedButton = linkedButton;
        linkedButton.setRunnable(()-> toggle());
    }
    public ClosablePanel(   ) {
        addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getTapCount()>1) {
                    close();
                }
            }
        });
    }

    private void toggle() {
        if (isVisible()) {
            close();
        } else {
            open();
        }
    }

    public void close() {
        if (linkedButton != null) {
        linkedButton.setChecked(false);
                    }
        fadeOut();
    }

    public void open() {
        if (linkedButton != null) {
            linkedButton.setChecked(true);
        }
        fadeIn();
    }
}
