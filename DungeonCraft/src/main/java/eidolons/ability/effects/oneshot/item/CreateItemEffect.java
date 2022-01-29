package eidolons.ability.effects.oneshot.item;

import eidolons.content.PARAMS;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.handlers.ItemGenerator;
import main.ability.effects.MicroEffect;
import main.ability.effects.OneshotEffect;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.MATERIAL;
import main.data.DataManager;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.log.LogMaster;
import main.system.math.Formula;

import java.util.List;

public class CreateItemEffect extends MicroEffect implements OneshotEffect {

    private static final String STD_ARMOR_ITEMS = "Chain Shirt;Cuirass;Half Plate;Full Plate";
    private static final String STD_WEAPON_ITEMS = "Great Sword;Claymore;Long Sword;Broad Sword; Falchion;Short Sword;Dagger;Knife"
     + "Maul;Mace;Battle Hammer;Flail;";
    private static final String EXTENDED_ARMOR_ITEMS = "";
    private static final String EXTENDED_WEAPON_ITEMS = "";

    String prefix = "Conjured ";
    private MATERIAL material;
    private Formula durabilityFormula;
    private boolean groupInitialized;
    private boolean weapon;
    private OBJ_TYPE TYPE;
    private List<String> typeList;
    private Boolean extended;
    private boolean quick;
    private Boolean potions;

    public CreateItemEffect(Boolean weapon, Boolean potions) {
        // quick item!
        quick = true;
        this.potions = potions;
        this.weapon = weapon;
    }

    public CreateItemEffect(Boolean weapon, Boolean extended,
                            Formula durabilityFormula, MATERIAL material) {
        this.durabilityFormula = durabilityFormula;
        this.material = material;
        this.weapon = weapon;
        this.extended = extended;
        LogMaster.log(1, durabilityFormula + ""
         + material);
    }

    private void initGroup() {
        if (quick) {
            if (weapon) {
                // daggers
            } else {
                if (potions) {

                } else {
                    // concoctions!
                }

            }
        }
        if (!weapon) {

            TYPE = DC_TYPE.ARMOR;

            typeList = ContainerUtils.openContainer(STD_ARMOR_ITEMS);
            if (extended) {
                typeList.addAll(ContainerUtils
                 .openContainer(EXTENDED_ARMOR_ITEMS));
            }
        } else {
            TYPE = DC_TYPE.WEAPONS;
            typeList = ContainerUtils.openContainer(STD_WEAPON_ITEMS);
            if (extended) {
                typeList.addAll(ContainerUtils
                 .openContainer(EXTENDED_WEAPON_ITEMS));
            }
        }

        groupInitialized = true;
    }

    @Override
    public boolean applyThis() {
        if (!groupInitialized) {
            initGroup();
        }
        String typeName = ListChooser.chooseType(typeList, TYPE);
        typeName = typeName.trim();
        if (!DataManager.isTypeName(material.getName() + " " + typeName)) {
            ItemGenerator.getDefaultGenerator().generateItem(
             ItemEnums.QUALITY_LEVEL.NORMAL, material,
             DataManager.getType(typeName, TYPE));
        }

        typeName = material.getName() + " " + typeName;

        ObjType type = new ObjType(DataManager.getType(typeName, TYPE));

        Integer durability = (type.getIntParam(PARAMS.DURABILITY) * durabilityFormula
         .getInt(ref)) / 100;

        type.setParam(PARAMS.DURABILITY, durability);

        HeroItem item = ItemGenerator.getDefaultGenerator().createItem(
         type, ref, false); // init

        String itemName = prefix + item.getName();
        item.setName(itemName);

        // Entity hero = ref.getTargetObj();
        // if (!game.getRequirementsManager().preCheck(hero, item))
        // // if cannot use, add to inventory
        // return new ManipulateInventoryEffect(INVENTORY_ACTIONS.ADD)
        // .apply(ref);

        EquipEffect equipEffect = new EquipEffect(item);

        ref.setID((weapon) ? KEYS.WEAPON : KEYS.ARMOR, item.getId());

        return equipEffect.apply(ref);

    }

}
