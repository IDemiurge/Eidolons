package main.level_editor.gui.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.gui.generic.ValueContainer;

public class EditValueContainer extends ValueContainer {
    private static int max_length=15;
    /*
        default listeners?
         */
    Object edit_arg; //default path, number limit, enum class
    LevelStructure.EDIT_VALUE_TYPE type;

    public EditValueContainer(String name, Object value, Object edit_arg, LevelStructure.EDIT_VALUE_TYPE type) {
        super(getTexture(value), name, getString(value));
        this.edit_arg = edit_arg;
        this.type = type;
    }

    private static String getString(Object value) {
        return value instanceof String ? formatString(value.toString()) : null;
    }

    private static String formatString(String toString) {
        if (toString.length()>max_length) {
            return toString.substring(0, max_length) + "...";
        }
        return toString;
    }

    public Object getEdit_arg() {
        return edit_arg;
    }

    public LevelStructure.EDIT_VALUE_TYPE getType() {
        return type;
    }

    private static TextureRegion getTexture(Object value) {
        return value instanceof TextureRegion ? (TextureRegion) value : null;
    }
}
