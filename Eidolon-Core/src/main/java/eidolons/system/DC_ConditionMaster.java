package eidolons.system;

import eidolons.ability.conditions.*;
import eidolons.ability.conditions.req.CellCondition;
import eidolons.ability.conditions.req.CostCondition;
import eidolons.ability.conditions.req.ItemCondition;
import eidolons.ability.conditions.shortcut.*;
import eidolons.ability.conditions.special.*;
import eidolons.ability.conditions.special.SpellCondition.SPELL_CHECK;
import eidolons.entity.unit.Unit;
import eidolons.game.exploration.handlers.ExplorationMaster;
import main.content.CONTENT_CONSTS.RETAIN_CONDITIONS;
import main.content.CONTENT_CONSTS.SPECIAL_REQUIREMENTS;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.*;
import main.content.enums.entity.AbilityEnums.TARGETING_MODIFIERS;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.*;
import main.elements.conditions.standard.*;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.bf.directions.UNIT_DIRECTION;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

import java.util.List;

public class DC_ConditionMaster extends ConditionMaster {
    public static Condition getMoveFilterCondition() {
        return new MoveCondition();
    }

    public static Condition getAliveFilterCondition() {
        return new NotCondition(new StatusCheckCondition(UnitEnums.STATUS.DEAD));
    }

    private static Condition findTargetingModifierCondition(String string) {
        TARGETING_MODIFIERS TARGETING_MODIFIERS = new EnumMaster<TARGETING_MODIFIERS>()
                .retrieveEnumConst(TARGETING_MODIFIERS.class, string);
        if (TARGETING_MODIFIERS != null) {
            return getTargetingModConditions(TARGETING_MODIFIERS);
        }

        return null;
    }

    public static Condition getTargetingModConditions(TARGETING_MODIFIERS MOD) {
        String obj_ref = KEYS.MATCH.toString();
        switch (MOD) {

            case FREE_CELL:
                return new CellCondition(true);
            case PUSHABLE:
                return new PushableCondition();

            case NO_STEALTH:
                return new NotCondition(new SneakingCondition());
            case SPACE:
                return new SpaceCondition();
            case RANGE:
                return new RangeCondition();
            case VISION:
                return new VisibilityCondition(UNIT_VISION.IN_SIGHT);
            case NO_VISION:
                return new NotCondition(new VisibilityCondition(UNIT_VISION.IN_SIGHT));
            case SNEAKY:
                return new SneakCondition();
            case ONLY_LIVING:
                return ConditionMaster.getLivingMatchCondition();
            case NO_EVIL:
                return new NotCondition(new OrConditions(new ClassificationCondition(
                        UnitEnums.CLASSIFICATIONS.UNDEAD),
                        new ClassificationCondition(UnitEnums.CLASSIFICATIONS.DEMON), new PropCondition(
                        G_PROPS.PRINCIPLES, RpgEnums.PRINCIPLES.TREACHERY)));
            case ONLY_EVIL:
                return new OrConditions(
                        new ClassificationCondition(UnitEnums.CLASSIFICATIONS.UNDEAD),
                        new ClassificationCondition(UnitEnums.CLASSIFICATIONS.DEMON), new PropCondition(
                        G_PROPS.PRINCIPLES, RpgEnums.PRINCIPLES.TREACHERY));

            case ONLY_UNDEAD:
                return new ClassificationCondition(UnitEnums.CLASSIFICATIONS.UNDEAD);
            case CLEAR_SHOT:
                return getClearShotCondition(obj_ref);
            case NOT_SELF:
                return new NotCondition(new RefCondition(KEYS.MATCH, KEYS.SOURCE));
            case NO_FRIENDLY_FIRE:
                return new NotCondition(new OwnershipCondition(KEYS.MATCH, KEYS.SOURCE));
            case NO_ENEMIES:
                return new NotCondition(new OwnershipCondition(true, KEYS.MATCH + "", KEYS.SOURCE
                        + ""));
            case NO_WALLS:
                return new NotCondition(
                        new PropCondition(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.WALL + ""));

            case NO_WATER:
                return new NotCondition(
                        new PropCondition(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.WATER + ""));

            case NO_NEUTRALS:
                return new NotCondition(new OwnershipCondition(KEYS.MATCH + "", true));
            case NO_LIVING:
                return new NotCondition(ConditionMaster.getLivingMatchCondition());
            case NO_UNDEAD:
                return new NotCondition(new ClassificationCondition(UnitEnums.CLASSIFICATIONS.UNDEAD));
            default:
                break;
        }
        return null;
    }

    public static Requirement getSpecialReq(String subString, Entity entity) {

        SPECIAL_REQUIREMENTS CONST = new EnumMaster<SPECIAL_REQUIREMENTS>().retrieveEnumConst(
                SPECIAL_REQUIREMENTS.class, VariableManager.removeVarPart(subString));

        if (CONST == null) {
            return null;
        }
        Object[] variables = null;
        if (VariableManager.checkVar(subString)) {
            variables = VariableManager.getVariables(subString);
        }
        Condition condition = null;
        switch (CONST) {
            case COMBAT_ONLY:
                condition = new CustomCondition() {
                    @Override
                    public boolean check(Ref ref) {
                        return !ExplorationMaster.isExplorationOn();
                    }
                };
                break;
            case PARAM:
            case COUNTER:
                condition = new NumericCondition(
                        StringMaster.getValueRef(KEYS.SOURCE.toString(), variables[0].toString()), variables[1]
                        .toString());
                break;
            case CUSTOM:
                condition = ConditionMaster.toConditions(variables[0].toString());
                break;

            // boolean var for NOT
            case NON_VOID:
            case FREE_CELL:
                condition = getFreeCellCondition(KEYS.SOURCE.toString(), variables[0].toString());
                break;
            case ITEM:
                condition = getItemCondition(variables[0].toString(), variables[1].toString(),
                        variables[2].toString(), KEYS.SOURCE.toString());
                break;
            case NOT_ITEM:
                condition = new NotCondition(getItemCondition(variables[0].toString(), variables[1]
                        .toString(), variables[2].toString(), KEYS.SOURCE.toString()));
                break;
            case REF_NOT_EMPTY: {
                condition = new RefNotEmptyCondition(variables[0].toString(), variables[1]
                        .toString());
                break;
            }
            case REF_EMPTY: {
                condition = new NotCondition(
                        new RefNotEmptyCondition(variables[0].toString(), variables[1]
                                .toString()));
                break;
            }
            default:
                break;
        }
        if (condition == null) {
            return null;
        }
        return new Requirement(condition, CONST.getText(variables));
    }

    private static Condition getItemCondition(String slot, String prop, String val, String obj_ref) {
        return new ItemCondition(obj_ref, slot, prop, val);
    }

    public static Condition getFreeCellCondition(String obj_ref, String direction) {
        UNIT_DIRECTION CONST = new EnumMaster<UNIT_DIRECTION>().retrieveEnumConst(
                UNIT_DIRECTION.class, direction);
        if (CONST == null) {
            return null;
        }
        return new CellCondition(obj_ref, CONST);
    }

    public static Condition getSelectiveTargetingTemplateConditions(
            SELECTIVE_TARGETING_TEMPLATES template) {
        return getInstance().getSelectiveTemplateConditions(template);
    }

    public Condition getSelectiveTemplateConditions(
            SELECTIVE_TARGETING_TEMPLATES template) {
        Conditions c = new Conditions();

        if ((template.isDependentOnZ())) {
            c.add(new ZLevelCondition(true));// source and match?
        }

        switch (template) {
            case ANY_ARMOR: {
                c.add(new ObjTypeComparison(DC_TYPE.ARMOR));
                break;
            }
            case ANY_WEAPON:
            case MY_WEAPON: {
                c.add(new NotCondition(new PropCondition(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.NATURAL
                        .toString())));
                c.add(new OrConditions(new ObjTypeComparison(DC_TYPE.WEAPONS), new Conditions(
                        new PropCondition(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.WRAPPED_ITEM),
                        new ObjTypeComparison(DC_TYPE.ITEMS))));
                break;
            }
            case ANY_ITEM:
            case MY_ITEM: {
                c.add(new ObjTypeComparison(DC_TYPE.ITEMS));
                break;
            }
            case ENEMY_ARMOR: {
                c.add(new ObjTypeComparison(DC_TYPE.ARMOR));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }
            case ENEMY_ITEM: {
                c.add(new ObjTypeComparison(DC_TYPE.ITEMS));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }
            case ENEMY_WEAPON: {
                c.add(new NotCondition(new PropCondition(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.NATURAL
                        .toString())));
                c.add(new OrConditions(new ObjTypeComparison(DC_TYPE.ITEMS),
                        new ObjTypeComparison(DC_TYPE.WEAPONS)));
                c.add(ConditionMaster.getEnemyCondition());
                break;
            }

            case MY_SPELLBOOK:
                c.add(ConditionMaster.getTYPECondition(DC_TYPE.SPELLS));
                c.add(new SpellCondition(SPELL_CHECK.ACTIVE));
                c.add(new RefCondition(KEYS.MATCH_SOURCE, KEYS.SOURCE));
                break;

            case MY_ARMOR: {
                // c.add(new ObjTypeComparison(OBJ_TYPES.ARMOR));
                c.add(new RefCondition(KEYS.ARMOR, KEYS.MATCH));
                break;
            }// c.add(new RefCondition(KEYS.MATCH_SOURCE, KEYS.SOURCE));
// c.add(new RefCondition(KEYS.WEAPON, KEYS.MATCH));
            case CELL: {
                c.add(ConditionMaster.getTYPECondition(DC_TYPE.TERRAIN));
                c.add(getRangeCondition());
                c.add(new CellCondition(true));
                break;
            }
            case BLAST: {
                c.add(ConditionMaster.getUnit_Char_BfObj_TerrainTypeCondition());
                c.add(getRangeCondition());
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                break;
            }
            case BLIND_SHOT:
            case SHOT:
                c.add(ConditionMaster.getUnit_Char_BfObjTypeCondition());
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                c.add(getRangeCondition());
                c.add(new OrConditions(new StdPassiveCondition(UnitEnums.STANDARD_PASSIVES.DARKVISION),
                        new NotCondition(new VisibilityCondition(UNIT_VISION.CONCEALED))));
                c.add(getClearShotCondition(KEYS.MATCH.name())); //
                // c.add(new NotCondition(new VisibilityCondition(
                // UNIT_TO_PLAYER_VISION.UNKNOWN))); // ??? TODO PERHAPS MAKE
                // ALL SHOT LIKE THIS!
                break;
            case PRECISE_SHOT:
                c.add(new VisibilityCondition(UNIT_VISION.IN_PLAIN_SIGHT));
                break;
            case UNOBSTRUCTED_SHOT:
                c.add(new NotCondition(ConditionMaster.getSelfFilterCondition()));
                c.add(getRangeCondition());
                break;
            case RAY:
                c.add(ConditionMaster.getDistanceFilterCondition(Ref.KEYS.SOURCE.name(), 1));
                break;
            case MOVE:
                c.add(DC_ConditionMaster.getMoveFilterCondition());
                break;
            case BF_OBJ:
                c.add(ConditionMaster.getTYPECondition(DC_TYPE.BF_OBJ));
                break;
            case ANY_ALLY:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                c.add(ConditionMaster.getOwnershipFilterCondition(Ref.KEYS.SOURCE.name(), true));
                break;
            case ANY_ENEMY:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                c.add(ConditionMaster.getOwnershipFilterCondition(Ref.KEYS.SOURCE.name(), false));
                break;
            case ANY_UNIT:
                c.add(ConditionMaster.getUnit_CharTypeCondition());
                break;
            case KEY:
                c.add(new OrConditions(
                        new PropCondition(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.LOCK.toString(), true),
                        new PropCondition(G_PROPS.BF_OBJECT_GROUP, BF_OBJECT_GROUP.DOOR.toString(), true)));
                break;
            case ATTACK:
                c.add(new VisibilityCondition(UNIT_VISION.IN_SIGHT));
                c.add(new OrConditions(
                        new StatusCheckCondition(KEYS.SOURCE.toString(), UnitEnums.STATUS.DEFENDING)
//                        , TODO DC Review - do we support these passives?
//                        new Conditions(new FacingCondition(UnitEnums.FACING_SINGLE.IN_FRONT, UnitEnums.FACING_SINGLE.BEHIND),
//                                new StringComparison(StringMaster.getValueRef(KEYS.SOURCE,
//                                        G_PROPS.STANDARD_PASSIVES), UnitEnums.STANDARD_PASSIVES.HIND_REACH + "",
//                                        false)),
//                        new Conditions(new FacingCondition(UnitEnums.FACING_SINGLE.IN_FRONT,
//                                UnitEnums.FACING_SINGLE.TO_THE_SIDE), new StringComparison(StringMaster.getValueRef(
//                                KEYS.SOURCE, G_PROPS.STANDARD_PASSIVES),
//                                UnitEnums.STANDARD_PASSIVES.BROAD_REACH + "", false))

                ));
                c.add(ConditionMaster.getAttackConditions());
                c.add(getClearShotCondition(KEYS.MATCH.name()));
                break;
            case GRAVE_CELL:
                c.add(new GraveCondition());
                break;


            case CLAIM: {

                c.add(new NotCondition(new StatusCheckCondition( UnitEnums.STATUS.CHANNELED)));
                c.add(new NotCondition(new OwnershipCondition(KEYS.SOURCE, KEYS.MATCH)));
                c.add(ConditionMaster.getTYPECondition(DC_TYPE.BF_OBJ));
                c.add(ConditionMaster.getDistanceFilterCondition(Ref.KEYS.SOURCE.name(),
                        "{ACTIVE_RANGE}"));

                break;
            }
            default:
                break;

        }
        return c;
    }

    public static Condition getRetainConditionsFromTemplate(RETAIN_CONDITIONS template, Ref ref) {
        switch (template) {
            case CASTER_ALIVE:
                new NotCondition(new StringComparison("{SOURCE_STATUS}", "" + UnitEnums.STATUS.DEAD, false));
                break;
            case CASTER_CONSCIOUS:
                new MicroCondition() {
                    public boolean check(Ref ref) {
                        Unit hero = (Unit) ref.getObj(KEYS.SOURCE);
                        return !hero.checkUncontrollable();
                    }
                };
                break;
            default:
                break;

        }
        return null;
    }

    public static RangeCondition getRangeCondition() {
        return new RangeCondition();
    }

    public static Condition getAutoTargetingTemplateConditions(AUTO_TARGETING_TEMPLATES template) {
        switch (template) {
            case CELL:
                return new PositionCondition(KEYS.TARGET.toString());

            case PARTY:
                return new RefCondition("source_party", "match_party");

            case ACTIONS:
                return new Conditions(ConditionMaster.getTYPECondition(DC_TYPE.ACTIONS),
                        new RefCondition(KEYS.SOURCE, KEYS.MATCH_SOURCE));

            case SPELLS:
                return new Conditions(ConditionMaster.getTYPECondition(DC_TYPE.SPELLS),
                        new RefCondition(KEYS.SOURCE, KEYS.MATCH_SOURCE));

            case ACTIVE_WEAPONS:
                return ConditionMaster.getActiveWeaponsCondition();

            case ALL:
                return (ConditionMaster.getUnit_CharTypeCondition());
            case ALL_ALLIES:
                return ConditionMaster.getAllyCondition().join(
                        (ConditionMaster.getUnit_CharTypeCondition()));
            case ALL_ENEMIES:
                return (ConditionMaster.getEnemyCondition());
            case SELF:
                return (ConditionMaster.getSelfFilterCondition());
            default:
                break;

        }
        return null;
    }

    public static boolean checkLiving(Unit hero) {

        return checkCondition(ConditionMaster.getLivingCondition(KEYS.SOURCE.toString()), hero
                .getRef());
    }

    public static boolean checkCondition(Condition condition, Ref ref) {
        return condition.preCheck(ref);
    }

    public static Condition getClearShotFilterCondition() {
        return getClearShotCondition("MATCH");
    }

    public static Condition getClearShotCondition(String obj_ref) {
        return new ClearShotCondition("SOURCE", obj_ref);
    }

    public Condition getDynamicCondition(String s) {
        List<String> list = ContainerUtils.openContainer(s);
        String name = list.get(0);
        String arg = null;
        if (list.size() > 1) {
            arg = list.get(1);
        }

        switch (name) {
            case "CostCondition":
                return new CostCondition(arg);
        }
        return null;
    }

    @Override
    public Condition getConditionFromTemplate(CONDITION_TEMPLATES template, String str1, String str2) {
        Condition result = null;
        {
            switch (template) {
                case ITEM: {
                    String prop = VariableManager.removeVarPart(str2);
                    String val = VariableManager.getVarPart(str2);
                    return new ItemCondition(KEYS.SOURCE.toString(), str1, prop, val);
                }
                case ENEMIES_LEFT:
                    Integer n = Integer.valueOf(str1);
                    return new DC_Condition() {
                        @Override
                        public boolean check(Ref ref) {
                            return getGame().getManager().getEnemies().size()<=n;
                        }
                    };

                case MAIN_HERO:
                    return new MainHeroCondition();
            }
        }
        if (result == null) {
            result = super.getConditionFromTemplate(template, str1, str2);
        }
        return result;
    }


}
