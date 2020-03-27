package main.level_editor.gui.dialog;

import main.content.values.ValuePair;
import main.entity.Entity;
import main.level_editor.gui.components.EditValueContainer;

public class EntityEditDiag extends EditDialog<ValuePair> {
    private final Entity entity;

    public EntityEditDiag(Entity entity) {
        super(0);
        this.entity = entity;

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    @Override
    protected EditValueContainer.EDIT_VALUE_TYPE getType(ValuePair datum) {
        return null;
    }

    @Override
    protected Object getArg(ValuePair datum) {
        return null;
    }

    @Override
    protected String getVal(ValuePair datum) {
        return null;
    }

    @Override
    protected String getName(ValuePair datum) {
        return null;
    }

    @Override
    protected ValuePair[] initDataArray() {
        return new ValuePair[0];
    }
}
