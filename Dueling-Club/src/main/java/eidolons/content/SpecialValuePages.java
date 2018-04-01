package eidolons.content;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;

import java.util.ArrayList;
import java.util.List;

public class SpecialValuePages {

    public static List<List<VALUE>> getDynamicPages(OBJ_TYPE TYPE, boolean simulation) {
        List<List<VALUE>> list = new ArrayList<>();
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
