package main.level_editor.gui.components;

import eidolons.libgdx.gui.generic.ValueContainer;

public class EditValueContainer extends ValueContainer {
    /*
    default listeners?
     */
    Object edit_arg; //default path, number limit, enum class
    EDIT_VALUE_TYPE type;

    public enum EDIT_VALUE_TYPE{
        text,
        number,
        enum_const,
        multi_enum_const,
        image,
        file,

    }

    public EditValueContainer(String name, String value, Object edit_arg, EDIT_VALUE_TYPE type) {
        super(name, value);
        this.edit_arg = edit_arg;
        this.type = type;
    }
}
