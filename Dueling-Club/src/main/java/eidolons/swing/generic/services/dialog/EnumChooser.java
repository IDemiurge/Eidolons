package eidolons.swing.generic.services.dialog;

import main.game.bf.Coordinates.FACING_DIRECTION;

public class EnumChooser<E> {

    public FACING_DIRECTION choose(Class<FACING_DIRECTION> clazz) {
        int i = DialogMaster.optionChoice(clazz.getEnumConstants(), "Choose "
         + clazz.getSimpleName() + " constant");
        if (i == -1) {
            return null;
        }
        return clazz.getEnumConstants()[i];
    }

}
