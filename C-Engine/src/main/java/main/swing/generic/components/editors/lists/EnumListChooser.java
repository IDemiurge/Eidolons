package main.swing.generic.components.editors.lists;

import main.system.auxiliary.EnumMaster;

public class EnumListChooser<E> extends GenericListChooser<E> {

    public E chooseEnumConst(Class<E> ENUM_CLASS) {
        String s = ListChooser.chooseEnum(ENUM_CLASS);
        return new EnumMaster<E>().retrieveEnumConst(ENUM_CLASS, s);
    }
}
