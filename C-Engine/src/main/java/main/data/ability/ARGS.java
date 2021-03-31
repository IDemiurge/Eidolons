package main.data.ability;

import main.ability.Ability;
import main.ability.effects.Effect;
import main.ability.effects.Effect.*;
import main.content.CONTENT_CONSTS.SPECIAL_REQUIREMENTS;
import main.content.CONTENT_CONSTS2.INJURY;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.entity.AbilityEnums;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.COUNTER_INTERACTION;
import main.content.enums.entity.UnitEnums.COUNTER_OPERATION;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.system.AiEnums.ORDER_PRIORITY_MODS;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.dialogue.DataString;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.elements.conditions.Condition;
import main.elements.targeting.AutoTargeting.AUTO_TARGETING_TEMPLATES;
import main.elements.targeting.SelectiveTargeting.SELECTIVE_TARGETING_TEMPLATES;
import main.elements.targeting.Targeting;
import main.elements.triggers.Trigger;
import main.entity.Ref;
import main.game.bf.MovementManager.MOVE_MODIFIER;
import main.game.bf.MovementManager.MOVE_TEMPLATES;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.math.Formula;
import main.system.math.PositionMaster;

import java.util.Arrays;

public enum ARGS implements Argument {
    // MORE CONCRETE CLASSES LIKE NUMERIC CONDITION?

    // Problem - what if I need something that is not a class, but an enum
    // constant? Like DMG_TYPE? It's a string but...

    // WHAT OF COMPONENT_ENUM's replacement?

    // ACTION,
    // // ABILITY, can't i used ARGS?
    // REPLY,
    // ACTOR,

    EFFECT(Effect.class),
    // ACTIVE_ABILITY(AbilityObj.class),
    // PASSIVE_ABILITY(PassiveAbility.class),
    ABILITY(Ability.class),
    TARGETING(Targeting.class),
    TRIGGER(Trigger.class),
    CONDITION(Condition.class),

    PARAM(PARAMETER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING) {
        @Override
        public Object[] getEnumList() {
            return EnumMaster.getParamEnumConstants();
        }
    },
    PROP(PROPERTY.class, AE_ELEMENT_TYPE.ENUM_CHOOSING) {
        @Override
        public Object[] getEnumList() {
            return EnumMaster.getPropEnumConstants();
        }
    },
    // primitive: String int ...
    FORMULA(Formula.class, true, AE_ELEMENT_TYPE.TEXT),
    STRING(String.class, true, AE_ELEMENT_TYPE.TEXT),
    INTEGER(Integer.class, true, AE_ELEMENT_TYPE.TEXT),
    BOOLEAN(Boolean.class, true, AE_ELEMENT_TYPE.BOOLEAN) {
        @Override
        public String getEmptyName() {
            return "FALSE";
        }
    },

    // ENUMS
    STANDARD_EVENT_TYPE(STANDARD_EVENT_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    STD_COUNTERS(COUNTER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    STANDARD_PASSIVES(UnitEnums.STANDARD_PASSIVES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    MODVAL_TYPE(MOD.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    DAMAGE_TYPE(GenericEnums.DAMAGE_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    STATUS(UnitEnums.STATUS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    REF_KEY(Ref.KEYS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    MOD_PROP_TYPE(MOD_PROP_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    AUTO_TARGETING_TEMPLATES(AUTO_TARGETING_TEMPLATES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    SELECTIVE_TARGETING_TEMPLATES(SELECTIVE_TARGETING_TEMPLATES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    SPECIAL_EFFECTS_CASE(SPECIAL_EFFECTS_CASE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    SPELL_MANIPULATION(SPELL_MANIPULATION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    ABSORB_TYPES(ABSORB_TYPES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    ABILITY_MANIPULATION(ABILITY_MANIPULATION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    BLOCK_TYPES(BLOCK_TYPES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    BIND_FILTER(BIND_FILTER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    DIRECTION(DIRECTION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    MOVE_MODIFIER(MOVE_MODIFIER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    MOVE_TEMPLATES(MOVE_TEMPLATES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    STD_MODES(STD_MODES.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    BIND_TYPE(BIND_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    FACING_TEMPLATE(FACING_SINGLE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    TARGETING_MODE(AbilityEnums.TARGETING_MODE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    EFFECTS_WRAP(AbilityEnums.EFFECTS_WRAP.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    CLASSIFICATIONS(UnitEnums.CLASSIFICATIONS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    UNIT_DIRECTION(UNIT_DIRECTION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    SPECIAL_REQUIREMENTS(SPECIAL_REQUIREMENTS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    TARGETING_MODIFIERS(AbilityEnums.TARGETING_MODIFIERS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    MATERIAL(ItemEnums.MATERIAL.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    UNIT_TO_UNIT_VISION(UNIT_VISION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    UNIT_TO_PLAYER_VISION(PLAYER_VISION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    DAMAGE_MODIFIER(GenericEnums.DAMAGE_MODIFIER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    OBJ_TYPES(DC_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    // ZONE_EFFECT_TEMPLATES(ZONE_EFFECT_TEMPLATES.class,
    // AE_ELEMENT_TYPE.ENUM_CHOOSING), TODO freaking depency!
    UPKEEP_FAIL_ACTION(UPKEEP_FAIL_ACTION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    ROLL_TYPES(GenericEnums.RollType.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    ITEM_SLOT(ItemEnums.ITEM_SLOT.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    RAISE_MODIFIER(RAISE_MODIFIER.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    RAISE_TYPE(RAISE_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    // GENERATE THESE FROM CONTENT_CONSTS ALREADY!!!!!

    ORDER_TYPE(main.content.CONTENT_CONSTS2.ORDER_TYPE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    INJURY(INJURY.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    DAMAGE_CASE(DAMAGE_CASE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    COUNTER_INTERACTION_TYPE(COUNTER_INTERACTION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    COUNTER_OPERATION(COUNTER_OPERATION.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    ORDER_PRIORITY_MODS(ORDER_PRIORITY_MODS.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),

    DATA_TYPE(SPEECH_VALUE.class, AE_ELEMENT_TYPE.ENUM_CHOOSING),
    DATA_STRING(DataString.class, AE_ELEMENT_TYPE.ITEM_CHOOSING),
    SHAPE(PositionMaster.SHAPE.class, AE_ELEMENT_TYPE.ITEM_CHOOSING),
//    SPEECH(SpeechInterface.class, AE_ELEMENT_TYPE.ITEM_CHOOSING),
//    SPEECHDATA(SpeechData.class, AE_ELEMENT_TYPE.ITEM_CHOOSING),

    UNKNOWN(Object.class),;

    private AE_ELEMENT_TYPE ELEMENT_TYPE;
    private boolean primitive = false;
    private boolean container = false;
    private Class<?> coreClass;

    ARGS(Class<?> c, AE_ELEMENT_TYPE type) {
        this(c, false, type);
    }

    ARGS(Class<?> c, boolean primitive, AE_ELEMENT_TYPE type) {
        this(c);
        this.setPrimitive(primitive);
        this.ELEMENT_TYPE = type;
    }

    ARGS(Class<?> c) {
        this.container = Arrays.asList(Mapper.CONTAINER_CLASSES).contains(coreClass);
        String name = StringMaster.format(name());
        this.coreClass = c;
        setPrimitive(false);
        this.ELEMENT_TYPE = AE_ELEMENT_TYPE.ITEM_CHOOSING;
    }

    public static Argument[] getArgs() {
        return values();
    }

    @Override
    public Class<?> getCoreClass() {

        return coreClass;
    }

    @Override
    public AE_ELEMENT_TYPE getElementType() {
        return ELEMENT_TYPE;
    }

    @Override
    public String getEmptyName() {
        return Strings.VAR_STRING;
//        return "<<< " + name + " >>>"; // TODO empty node HANDLED!
    }

    @Override
    public boolean isPrimitive() {
        return primitive;
    }

    @Override
    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    @Override
    public boolean isENUM() {
        return (ELEMENT_TYPE == AE_ELEMENT_TYPE.ENUM_CHOOSING);
    }

    @Override
    public Object[] getEnumList() {

        return coreClass.getEnumConstants();
    }

    public boolean isContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }
}
