package main.level_editor.gui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.GdxMaster;
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
                if (getTapCount()>1 &&
                        Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)                                ) {
                    close();
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (GdxMaster.getAncestors(toActor).contains(ClosablePanel.this)) {
                    return;
                }
                getStage().setScrollFocus(null );
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
