package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
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
    protected Vector2 getElementSize() {
        return new Vector2(360, 64);
    }

    protected abstract EditValueContainer.EDIT_VALUE_TYPE getType(T datum);

    protected abstract Object getArg(T datum);

    protected abstract String getVal(T datum);

    protected abstract String getName(T datum);

    @Override
    protected EditValueContainer[] initActorArray() {
        return new EditValueContainer[0];
    }

}
