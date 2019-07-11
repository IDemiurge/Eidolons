package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.game.module.herocreator.logic.items.ItemTraits.ITEM_TRAIT;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 12/7/2018.
 */
public class ItemTrait {
    ITEM_TRAIT template;
    ITEM_LEVEL level;

    String arg;

    public ItemTrait(ITEM_TRAIT template, ITEM_LEVEL level) {
        this.template = template;
        this.level = level;
    }

    public ItemTrait(String substring) {
        template =
         new EnumMaster<ITEM_TRAIT>().
          retrieveEnumConst(ITEM_TRAIT.class, substring.split(" - ")[0]);
        int lvl = NumberUtils.getInteger(substring.split(" - ")[1]);
        level = ITEM_LEVEL.values()[lvl];
    }

    public ITEM_TRAIT getTemplate() {
        return template;
    }

    public ITEM_LEVEL getLevel() {
        return level;
    }

    public String getArg() {
        return arg;
    }

    public int getNamingPriority() {
        return 0;
    }

    //some templates are put before level!
    @Override
    public String toString() {
        return StringMaster.getWellFormattedString(template.toString()) + " " +
                StringMaster.wrapInParenthesis(level.toString());
    }
}
