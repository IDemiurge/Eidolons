package eidolons.game.core.master;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.ability.effects.oneshot.mechanic.RollEffect;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.core.game.DC_Game;
import main.ability.Abilities;
import main.ability.Ability;
import main.ability.AbilityType;
import main.ability.effects.ContainerEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effects;
import main.ability.effects.common.AddStatusEffect;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.obj.IActiveObj;
import main.entity.obj.Attachment;
import main.entity.obj.Obj;
import main.system.auxiliary.ClassMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.ListMaster;
import main.system.text.TextParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/16/2017.
 */
public class EffectMaster extends Master {
    public EffectMaster(DC_Game game) {
        super(game);
    }

    public static AttackEffect getAttackEffect(IActiveObj action) {
        return (AttackEffect) getEffectsOfClass((ActiveObj) action,
         AttackEffect.class).get(0);
    }

    public static Effect getDisablingEffect() {
        return new AddStatusEffect(UnitEnums.STATUS.DISABLED);
    }

    public static Effects getEffectsFromAbilities(Abilities abilities) {
        Effects effects = new Effects();
        if (abilities != null) {
            for (Ability a : abilities) {
                effects.addAll(addEffectsFromAbility(a.getEffects()));
            }
        }
        return effects;
    }

    public static Effects getEffectsFromSpell(IActiveObj active) {
        Effects effects = new Effects();
        if (!active.isConstructed())
            active.construct();
        if (active.getAbilities() != null) {
            for (Ability a : active.getAbilities()) {
                effects.addAll(addEffectsFromAbility(a.getEffects()));
            }
        } else {
            for (IActiveObj a : active.getActives()) {
                for (Ability a1 : a.getAbilities()) {
                    for (Effect e : a1.getEffects()) {
                        effects.add(e);
                    }
                }
            }
        }

        return effects;
        // TODO for each ability

    }

    private static List<Effect> addEffectsFromAbility(Effects effects) {
        List<Effect> list = new ArrayList<>();
        for (Effect e : effects) {
            if (e instanceof Effects) {
                addEffectsFromAbility((Effects) e);
            }
            if (e instanceof ContainerEffect) {
                ContainerEffect containerEffect = (ContainerEffect) e;
                if (containerEffect.getEffect() != null) {
                    list.add(containerEffect.getEffect());
                }
            }
            list.add(e);
        }
        return list;
    }

    public static List<Effect> getEffectsFromAbilityType(String abilName, Ref ref) {

        AbilityType type = VariableManager.getVarType(abilName, false, ref);
        return getEffectsFromAbilityType(type);
    }

    public static List<Effect> getEffectsFromAbilityType(AbilityType type) {
        if (type.getAbilities() == null) {
            type.construct();
        }
        Abilities abils = type.getAbilities();

        List<Effect> list = new ArrayList<>();
        for (Effect e : abils.getEffects()) {
            list.add(e);
        }
        return list;

    }

    public static List<Effect> getEffectsOfClass(Abilities actives, Class<?> CLASS) {
        List<Effect> list = new ArrayList<>();
        if (actives == null) {
            return list;
        }
        for (Effect e : actives.getEffects()) {
            list.addAll(getEffectsOfClass(e, CLASS));
        }
        return list;
    }

    public static List<Effect> getEffectsOfClass(ActiveObj active, Class<?> CLASS) {
        return getEffectsOfClass(getEffectsFromSpell(active), CLASS);
    }

    public static Effect getFirstEffectOfClass(ActiveObj active, Class<?> CLASS) {
        List<Effect> list = getEffectsOfClass(getEffectsFromSpell(active), CLASS);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static List<Effect> getEffectsOfClass(Effect effect, Class<?> CLASS) {
        if (ClassMaster.isInstanceOf(effect, CLASS)) {
            return new ListMaster<Effect>().getList(effect);
        }

        List<Effect> list = new ArrayList<>();

        if (effect instanceof ContainerEffect) {
            ContainerEffect containerEffect = (ContainerEffect) effect;
            return getEffectsOfClass(containerEffect.getEffect(), CLASS);
        }
        if (effect instanceof Effects) {
            Effects effects = (Effects) effect;
            for (Effect e : effects.getEffects()) {

                list.addAll(getEffectsOfClass(e, CLASS));
            }
        }

        return list;
    }

    public static boolean check(Effect e, Class<?> CLASS) {
        if (ClassMaster.isInstanceOf(e, CLASS)) {
            return true;
        }
        if (e instanceof ContainerEffect) {
            ContainerEffect containerEffect = (ContainerEffect) e;
            return check(containerEffect.getEffect(), CLASS);
        }
        if (e instanceof Effects) {
            Effects effects = (Effects) e;
            for (Effect ef : effects) {
                if (check(ef, CLASS)) {
                    return true;
                }
            }
        }
        return (ClassMaster.isInstanceOf(e, CLASS));
    }

    public static boolean check(ActiveObj active, Class<?> CLASS) {
        if (active == null) {
            return false;
        }
        if (active.getAbilities() == null) {
            return false;
        }
        return check(active.getAbilities(), CLASS);
    }

    public static boolean check(Abilities actives, Class<?> CLASS) {
        if (actives == null) {
            return false;
        }
        if (actives.getEffects() == null) {
            return false;
        }
        return check(actives.getEffects(), CLASS);
    }

    public static List<RollEffect> getRollEffects(ActiveObj active) {
        List<RollEffect> list = new ArrayList<>();
        for (Effect e : getEffectsOfClass(active, RollEffect.class)) {
            ((RollEffect) e).getEffect(); // construct!
            list.add(((RollEffect) e));
        }
        return list;
    }

    public static List<Effect> getBuffEffects(Effect e, Class<?> CLASS) {
        List<Effect> list = new ArrayList<>();
        if (e instanceof AddBuffEffect) {
            AddBuffEffect addBuffEffect = (AddBuffEffect) e;
            Effect effect = addBuffEffect.getEffect();
            if (effect == null) {
                return list;
            }
            if (effect instanceof Effects) {
                Effects effects = (Effects) effect;
                for (Effect eff : effects) {
                    if (ClassMaster.isInstanceOf(eff, CLASS)) {
                        list.add(eff);
                    }
                }
            } else if (ClassMaster.isInstanceOf(effect, CLASS)) {
                return new ListMaster<Effect>().getList(effect);
            }
        }
        return list;
    }

    public static void applyAttachmentEffects(Obj obj, Integer layer) {
        if (obj == null) {
            return;
        }
        List<Attachment> attachments = obj.getAttachments();
        if (attachments != null) {
            for (Attachment a : attachments) {
                try {
                    for (Effect e : a.getEffects()) {
                        if (layer != null) {
                            if (e.getLayer() != layer) {
                                continue;
                            }
                        }
                        e.apply(Ref.getSelfTargetingRefCopy(obj));
                    }
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

    public static Attack getAttackFromAction(ActiveObj t) {
        List<Effect> eff = getEffectsOfClass(t, AttackEffect.class);
        AttackEffect e = (AttackEffect) eff.get(0);
        return e.getAttack();
    }

    public static Effects initParamModEffects(String modString, Ref ref) {
        Effects modEffects = new Effects();
        Map<PARAMETER, String> map = new RandomWizard<PARAMETER>().constructStringWeightMap(
         modString, PARAMETER.class);
        initParamModEffects(modEffects, map, ref);
        return modEffects;
    }

    public static Effects initPropModEffects(String modString, Ref ref) {
        Effects modEffects = new Effects();
        Map<PROPERTY, String> map = new RandomWizard<PROPERTY>().constructStringWeightMap(
         modString, PROPERTY.class);
        initPropModEffects(modEffects, map, ref);
        return modEffects;
    }

    public static void initParamModEffects(Effects modEffects, Map<PARAMETER, String> map, Ref ref) {
        for (PARAMETER param : map.keySet()) {
            String amount = map.get(param);
            Effect.MOD code = Effect.MOD.MODIFY_BY_CONST;
            if (!amount.contains(Strings.BASE_CHAR)) {
                if (amount.contains(Strings.MOD)) {
                    code = Effect.MOD.MODIFY_BY_PERCENT;
                    amount = amount.replace(Strings.MOD, "");
                }
                if (amount.contains(Strings.SET)) {
                    code = Effect.MOD.SET;
                    amount = amount.replace(Strings.SET, "");
                }
            }
            if (ref.getId(Ref.KEYS.INFO) == null) {
                ref.setID(Ref.KEYS.INFO, ref.getId(Ref.KEYS.SKILL));
            }
            amount = TextParser.parse(amount, ref, TextParser.INFO_PARSING_CODE);
            modEffects.add(new ModifyValueEffect(param, code, amount));
        }
    }

    public static void initPropModEffects(Effects modEffects, Map<PROPERTY, String> propMap, Ref ref) {
        for (PROPERTY prop : propMap.keySet()) {
            String amount = propMap.get(prop);
            Effect.MOD_PROP_TYPE code1 = Effect.MOD_PROP_TYPE.ADD;
            if (amount.contains(Strings.REMOVE)) {
                code1 = Effect.MOD_PROP_TYPE.REMOVE;
                amount.replace(Strings.REMOVE, "");
            }
            if (amount.contains(Strings.SET)) {
                code1 = Effect.MOD_PROP_TYPE.SET;
                amount.replace(Strings.SET, "");
            }

            ModifyPropertyEffect e = new ModifyPropertyEffect(prop, code1, amount);
            modEffects.add(e);

        }
    }

    public static Effects getEffectsFromAbilityString(String abilName, Ref ref) {
        Effects effects = (new Effects());
        String separator = Strings.AND_PROPERTY_SEPARATOR;
        if (!abilName.contains(separator)) {
            separator = Strings.VERTICAL_BAR;
        }
        for (String s : ContainerUtils.open(abilName, separator)) {
            effects.addAll(getEffectsFromAbilityType(s, ref));
        }
        return effects;

    }
}
