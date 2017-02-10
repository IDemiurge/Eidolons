package main.ability.conditions.req;

import main.content.CONTENT_CONSTS.ITEM_SLOT;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.MicroCondition;
import main.elements.conditions.StringComparison;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.EnumMaster;

public class ItemCondition extends MicroCondition {

    private String obj_ref;
    private String slot;
    private String prop;
    private String val;
    private Boolean strict = true;
    private boolean weapon;

    public ItemCondition(String obj_ref, String slot) {
        this.obj_ref = obj_ref;
        this.slot = slot;
    }

    public ItemCondition(String obj_ref, ITEM_SLOT slot) {

        this.obj_ref = obj_ref;
        this.slot = slot.toString();
    }

    @AE_ConstrArgs(argNames = {"obj_ref", "slot", "prop", "val"})
    public ItemCondition(String obj_ref, String slot, String prop, String val) {
        this(obj_ref, slot);
        this.prop = prop;
        this.val = val;
    }

    public ItemCondition(String obj_ref, Boolean weapon) {
        this.obj_ref = obj_ref;
        this.weapon = weapon;

    }

    @Override
    public boolean check() {
        if (ref.getGame().isSimulation()) {
            return true;
        }
        if (slot == null) {
            slot = (weapon) ? ITEM_SLOT.MAIN_HAND.toString() : ITEM_SLOT.ARMOR
                    .toString();
        }
        Entity item;
        DC_HeroObj unit = (DC_HeroObj) ref.getObj(obj_ref);
        if (slot.equalsIgnoreCase(KEYS.RANGED.toString())) {
            item = unit.getRef().getObj(KEYS.RANGED);
        } else {
            ITEM_SLOT slotConst = new EnumMaster<ITEM_SLOT>()
                    .retrieveEnumConst(ITEM_SLOT.class, slot, true);
            if (unit == null) {
                return false;
            }
            item = unit.getItem(slotConst);
        }

        if (item == null) {
            return false;
        }
        if (prop == null) // any item in the slot
        {
            return true;
        }
        String string = item.getProp(prop);
        return new StringComparison(string, val, strict).check(ref);

        // String prop2 = ref.getObj(obj_ref).getProp(slot);
        // if (StringMaster.isEmpty(prop2)) {
        // try {
        // prop2 = "" + ref.getObj(obj_ref).getRef().getObj(slot).getId();
        // } catch (Exception e) {
        // return false;
        // }
        // }
        // item = DataManager.getType(prop2, C_OBJ_TYPE.ITEMS);
        // return StringMaster.compare(string, val, true);
    }
}
