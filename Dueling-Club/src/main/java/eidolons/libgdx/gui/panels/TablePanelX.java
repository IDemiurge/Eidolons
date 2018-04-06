package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;

/**
 * Created by JustMe on 2/11/2018.
 */
public class TablePanelX<T extends Actor> extends TablePanel<T> {
    @Override
    public <T extends Actor> Cell<T> add(T actor) {
        return super.add(actor);
    }
}
