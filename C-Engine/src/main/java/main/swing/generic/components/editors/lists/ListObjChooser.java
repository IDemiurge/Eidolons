package main.swing.generic.components.editors.lists;

import main.entity.Entity;
import main.swing.generic.components.editors.lists.GenericListChooser.LC_MODS;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class ListObjChooser<T> {

    public ListObjChooser() {

    }

    private boolean isEntity(List<T> list) {
        return list.get(0) instanceof Entity;
    }

    public List<T> selectMulti(List<T> list) {
        return select(list, SELECTION_MODE.MULTIPLE);
    }

    public List<T> select(List<T> list, SELECTION_MODE mode) {
        ListChooser.addMod(LC_MODS.TEXT_DISPLAYED);
        String result = null;
        if (isEntity(list)) {
            result = ListChooser.chooseObj(list, mode);
        } else {
            result = ListChooser.chooseStrings(ListMaster.toStringList(list));
        }

        List<T> selection = new LinkedList<>();
        // for (T item : list) {
        // if (StringMaster.contains(result, item.toString())) {
        // selection.add(item);
        // result = result.replaceFirst(
        // StringMaster.getWellFormattedString(item.toString())+";",
        // "");
        // }
        // }
        LinkedList<T> items = new LinkedList<>(list);
        for (String substring : StringMaster.openContainer(result)) {
            for (T item : new LinkedList<>(items)) {
                if (StringMaster.compare(substring, item.toString(), true)) {
                    selection.add(item);
                    items.remove(item);
                    break;
                }
            }
        }
        return selection;
    }

}
