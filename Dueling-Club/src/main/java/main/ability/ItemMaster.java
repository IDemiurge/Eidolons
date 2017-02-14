package main.ability;

import main.entity.item.DC_HeroItemObj;
import main.entity.item.DC_QuickItemObj;

public class ItemMaster {

    public static boolean isBreakable(DC_HeroItemObj item) {
        if (item instanceof DC_QuickItemObj) {
            DC_QuickItemObj q = (DC_QuickItemObj) item;
            if (q.isCoating()) {
                return true;
            }
            if (q.isConcoction()) {
                return true;
            }
            if (q.isPotion()) {
                return true;
            }
        }
        // material - glass?

        return false;
    }

}
