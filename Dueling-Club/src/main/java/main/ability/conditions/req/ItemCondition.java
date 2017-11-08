package main.ability.conditions.req;

import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.MicroCondition;
import main.elements.conditions.StringComparison;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
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
    public boolean check(Ref ref) {
        if (ref.getGame().isSimulation()) {
            return true;
        }
        if (slot == null) {
            slot = (weapon) ? ItemEnums.ITEM_SLOT.MAIN_HAND.toString() : ItemEnums.ITEM_SLOT.ARMOR
             .toString();
        }
        Entity item;
        Unit unit = (Unit) ref.getObj(obj_ref);
        if (unit == null) {
            return false;
        }
        if (slot.equalsIgnoreCase(KEYS.RANGED.toString())) {
            item = unit.getRef().getObj(KEYS.RANGED);
        } else {
            if (slot.equalsIgnoreCase("weapon"))
                item = unit.getItem(!ref.getActive().isOffhand() ? ITEM_SLOT.MAIN_HAND : ITEM_SLOT.OFF_HAND);
            else item = unit.getItem(new EnumMaster<ITEM_SLOT>()
             .retrieveEnumConst(ITEM_SLOT.class, slot, true));
        }

        if (item == null) {
            return false;
        }
        if (prop == null) // any item in the slot
        {
            return true;
        }
        String string = item.getProp(prop);
        return new StringComparison(string, val, strict).preCheck(ref);

        // String prop2 = ref.getObj(obj_string).getProp(slot);
        // if (StringMaster.isEmpty(prop2)) {
        // try {
        // prop2 = "" + ref.getObj(obj_string).getRef().getObj(slot).getId();
        // } catch (Exception e) {
        // return false;
        // }
        // }
        // item = DataManager.getType(prop2, C_OBJ_TYPE.ITEMS);
        // return StringMaster.compare(string, val, true);
    }
}
