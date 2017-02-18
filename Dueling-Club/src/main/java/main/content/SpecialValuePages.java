package main.content;

import java.util.LinkedList;
import java.util.List;

public class SpecialValuePages {

    public static List<List<VALUE>> getDynamicPages(OBJ_TYPE TYPE, boolean simulation) {
        List<List<VALUE>> list = new LinkedList<>();
        if (TYPE instanceof DC_TYPE) {
            switch ((DC_TYPE) TYPE) {
                case ARMOR:
                    list.add(DC_ContentManager.getArmorGradeMultiParams());
                    break;
            }
        }

        return list;
    }

}
