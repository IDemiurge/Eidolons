package eidolons.ability.effects.oneshot.unit;

import eidolons.ability.InventoryTransactionManager;
import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.ability.effects.Effects;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STD_UNDEAD_TYPES;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.Formula;

public class RaiseEffect extends SummonEffect {

    private static final KEYS CORPSE_REF = KEYS.TARGET2;

    private static final String BUFF_NAME = "Raised ";

    private static final String MSTR_FACTOR = "0.02";

    private static final String SP_FACTOR = "0.02";

    private static final String MAX_RAISE_FACTOR = "0.5"; // certainly
    // boosted
    // by
    // skills
    // :)

    private static final String XP_FACTOR = "-0.75"; // soul?

    boolean preserveItems;
    private RAISE_TYPE raiseType;
    private RAISE_MODIFIER raiseMod;

    private Obj corpse;

    private boolean humanoid;

    // preserve items equipped
    // use corpse's strength
    //

    public RaiseEffect(String name) {
        super(name);
    }

    public RaiseEffect(RAISE_TYPE type) {
        this(type, null);
    }

    public RaiseEffect(String RAISE_TYPE, String RAISE_MODIFIER) {
        super(null);
        this.raiseType = new EnumMaster<RAISE_TYPE>().retrieveEnumConst(
         RAISE_TYPE.class, RAISE_TYPE, true);
        if (!StringMaster.isEmpty(RAISE_MODIFIER)) {
            this.raiseMod = new EnumMaster<RAISE_MODIFIER>().retrieveEnumConst(
             RAISE_MODIFIER.class, RAISE_MODIFIER, true);
        }
    }

    public RaiseEffect(RAISE_TYPE type, RAISE_MODIFIER mod) {
        super(null);
        this.raiseMod = mod;
        this.raiseType = type;
    }

    @Override
    protected Formula getXpFormula(Obj spell) {
        // TODO perhaps related to the corpse? Limit max xp by corpse's xp, e.g.
        return super.getXpFormula(spell).getAppendedByFactor(XP_FACTOR);
    }

    public void initCorpse() {
        corpse = ref.getGame().getGraveyardManager()
         .getTopDeadUnit(ref.getTargetObj().getCoordinates());
        if (corpse != null) {
            humanoid = corpse.checkProperty(G_PROPS.CLASSIFICATIONS, ""
             + UnitEnums.CLASSIFICATIONS.HUMANOID);
        }
    }

    @Override
    public boolean applyThis() {
        corpse = ref.getGame().getGraveyardManager()
         .destroyTopCorpse(ref.getTargetObj().getCoordinates());

        humanoid = corpse.checkProperty(G_PROPS.CLASSIFICATIONS, ""
         + UnitEnums.CLASSIFICATIONS.HUMANOID);
        initUnitType();

        super.applyThis();
        // upkeep/sickness
        // perhaps it should be done via resurrect then...!
        Ref REF = Ref.getSelfTargetingRefCopy(unit);
        REF.setID(CORPSE_REF, corpse.getId());
        getRevenantBuff().apply(REF);
        if (isEquipItems(raiseType)) {
            equipOriginalItems();
        }
        // skeleton mage should have same spells!
        return true;
    }

    private AddBuffEffect getRevenantBuff() {
        Effects effect = new Effects();
        // based on RAISE_TYPE?
        // for (PARAMETER portrait : getModifiedParam()) {
        // TODO gotta make sure that the corpse is *RESET* well and has
        // *ACTUAL* values!

        for (String s : StringMaster.open(getModifiedParam())) {
            String varPart = VariableManager.getVarPart(s);
            String valueName = s.replace(varPart, "");
            String formula = StringMaster.cropParenthesises(varPart) + "*"
             + getParamModFormula(valueName);// MAX?
            effect.add(new ModifyValueEffect(valueName,
             MOD.MODIFY_BY_CONST, formula));
        }
        BuffType buffType = new BuffType(
         DataManager.getType(BUFF_NAME));
        buffType.setName(BUFF_NAME + corpse.getName());
        AddBuffEffect e = new AddBuffEffect(buffType, effect);

        return e;
    }

    private String getParamModFormula(String valueName) {
        return "{"
         + CORPSE_REF.toString()
         + "_"
         + valueName
         + "}*min("
         + MAX_RAISE_FACTOR
         + ",("
         + SP_FACTOR
         + StringMaster.getValueRef(KEYS.SUMMONER, PARAMS.SPELLPOWER)
         + "+"
         + MSTR_FACTOR
         + " *"
         + StringMaster.getValueRef(KEYS.SUMMONER,
         PARAMS.NECROMANCY_MASTERY) + "))";
    }

    private String getModifiedParam() {
        switch (raiseType) {
            case GHOST:
                return "Wisdom(1);Spellpower(1);Willpower(1);Knowledge(1)";
            case GHOUL:
                return "Strength(0.6);Vitality(0.6);Dexterity(0.35);Agility(0.5)";
            case LICH:
                return "Strength(0.4);Vitality(0.4);Dexterity(0.1);Agility(0.1);Willpower(0.5)"
                 + "Wisdom(0.75);Intelligence(1);Spellpower(1.5);Knowledge(1)";
            case SKELETON:
                return "Strength(0.75);Vitality(0.35);Dexterity(0.25);Agility(0.15)";
            case ZOMBIE:
                return "Strength(0.6);Vitality(1);";
            case VAMPIRE:
                return "Strength(0.75);Vitality(0.5);Dexterity(0.65);Agility(0.75);Willpower(0.75)"
                 + "Wisdom(0.5);Intelligence(0.75);Spellpower(0.75);Knowledge(0.75)";
            case WRAITH_LORD:
                return "Strength(1.25);Vitality(0.75);Willpower(1.75);Dexterity(0.25);Agility(0.35)"
                 + "Intelligence(0.75);Wisdom(1.25);Spellpower(1.75);Knowledge(0.75)";

        }
        return null;
    }

    private void equipOriginalItems() {
        InventoryTransactionManager.equipOriginalItems((Unit) unit, corpse);
        // TODO ideally, they should remain *on* unless looted...

    }

    private boolean isEquipItems(RAISE_TYPE raiseType) {
        if (!humanoid) {
            return false;
        }
        if (raiseType == null) {
            return true;
        }
        switch (raiseType) {
            case GHOST:
            case GHOUL:
                return false;
        }
        return true;
    }

    private void initUnitType() {
        typeName = getUnitType();
    }

    public String getUnitType() {
        if (corpse == null) {
            initCorpse();
        }
        if (raiseType == null) {
            return typeName;
        }
        switch (raiseType) { // TODO ALT TYPE as per corpse type? UPGRADED as
            // per Spellpower/Mastery?
            case GHOST:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.WRAITH_BEAST.toString();
                }
                if (raiseMod == null) // ++ init raiseMod from skills?
                {
                    return STD_UNDEAD_TYPES.GHOST.toString();
                }
            case GHOUL:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.UNDEAD_BEAST.toString(); // ++
                }
                // monstrocity!
                return STD_UNDEAD_TYPES.GHOUL.toString();
            case LICH:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.WRAITH_BEAST.toString();
                }
                return STD_UNDEAD_TYPES.LICH.toString();
            case SKELETON:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.SKELETAL_BEAST.toString();
                }
                return STD_UNDEAD_TYPES.SKELETON.toString();
            case VAMPIRE:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.VAMPIRE_BEAST.toString();
                }
                return STD_UNDEAD_TYPES.VAMPIRE.toString();
            case WRAITH_LORD:
                return STD_UNDEAD_TYPES.WRAITH_MONSTROCITY.toString();
            case ZOMBIE:
                if (!humanoid) {
                    return STD_UNDEAD_TYPES.ZOMBIE_BEAST.toString();
                }
                return STD_UNDEAD_TYPES.ZOMBIE.toString();
        }
        // depending on corpse type? Skeleton Warrior/Mage/Rogue?
        // advanced necromancers should retain masteries/skills/spells of the
        // slain
        if (typeName == null) {
            return corpse.getType().getName();
            // clone type and add "NO_SHOPPING" bool!
        }
        return typeName;

    }

    public boolean isPreserveItems() {
        return preserveItems;
    }

    public RAISE_TYPE getRaiseType() {
        return raiseType;
    }

    public RAISE_MODIFIER getRaiseMod() {
        return raiseMod;
    }

    public Obj getCorpse() {
        return corpse;
    }

    public boolean isHumanoid() {
        return humanoid;
    }
}
