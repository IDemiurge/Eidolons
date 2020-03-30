package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import main.level_editor.gui.components.EditValueContainer;

public abstract  class EditDialog<T > extends ChooserDialog<T, EditValueContainer> {

    public EditDialog(int size) {
        super(2, size);
    }
    @Override
    protected EditValueContainer createElement_(T datum) {

        return    new EditValueContainer(getName(datum), getVal(datum),
                getArg(datum),
                getType(datum));
    }

    @Override
    protected EventListener createItemSelectListener(EditValueContainer actor, T item) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selected = item;
                edit(item);
            }
        };
    }

    protected abstract void edit(T item);


    @Override
    protected Vector2 getElementSize() {
        return new Vector2(360, 64);
    }

    protected abstract LevelStructure.EDIT_VALUE_TYPE getType(T datum);

    protected abstract Object getArg(T datum);

    protected abstract Object getVal(T datum);

    protected abstract String getName(T datum);

    @Override
    protected EditValueContainer[] initActorArray() {
        return new EditValueContainer[0];
    }

}
