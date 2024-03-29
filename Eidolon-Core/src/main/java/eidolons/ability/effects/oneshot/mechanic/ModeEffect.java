package eidolons.ability.effects.oneshot.mechanic;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.ability.effects.common.ModifyPropertyEffect;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.ability.effects.continuous.SetCustomModeEffect;
import eidolons.ability.effects.oneshot.buff.RemoveBuffEffect;
import eidolons.ability.effects.oneshot.status.ImmobilizeEffect;
import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.rules.magic.ChannelingRule;
import eidolons.game.battlecraft.rules.mechanics.InterruptRule;
import eidolons.game.battlecraft.rules.perk.AlertRule;
import eidolons.game.exploration.handlers.ExplorationMaster;
import main.ability.effects.*;
import main.ability.effects.periodic.PeriodicEffect;
import main.content.ContentValsManager;
import main.content.mode.MODE;
import main.content.mode.ModeImpl;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.ability.AE_ConstrArgs;
import main.data.ability.construct.VariableManager;
import main.elements.conditions.Condition;
import main.elements.conditions.RefCondition;
import main.elements.conditions.StringComparison;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.math.Formula;

import java.util.Map;

/**
 * wait mode - set target initiative position?
 * <portrait>
 * how to modify Modes? I.e., a perk enables acting while Alert... ?
 *
 * @author JustMe
 */
public class ModeEffect extends MicroEffect implements OneshotEffect {

    private static final String PARAM_MOD = "param mod";
    private static final String PARAM_BONUS = "param bonus";
    private static final String TIME_FACTOR = "{TIME_FACTOR}";
    protected boolean onActivateEffect;
    protected boolean onDeactivateEffect;

    private MODE mode;
    private final String prop = "{SOURCE_MODE}";
    private AddBuffEffect addBuffEffect;
    private STANDARD_EVENT_TYPE REMOVE_EVENT = STANDARD_EVENT_TYPE.ROUND_ENDS;
    private final ModifyPropertyEffect modPropEffect;
    private boolean reinit = true;

    @AE_ConstrArgs(argNames = {"template", "name", "defenseMod", "disableCounter", "dispelOnHit",})
    public ModeEffect(STD_MODES template, String name, Integer defenseMod, Boolean disableCounter,
                      Boolean dispelOnHit) {
        mode = new ModeImpl(template);
        mode.setDefenseMod(defenseMod);
        mode.setDisableCounter(disableCounter);
        mode.setDispelOnHit(dispelOnHit);
        if (mode.getBuffName() == null) {
            mode.setBuffName(name);
        }
        modPropEffect = new ModifyPropertyEffect(G_PROPS.MODE, MOD_PROP_TYPE.SET, template
                .toString());

        mapThisToConstrParams(template, name, defenseMod, disableCounter,
                dispelOnHit);
    }

    public ModeEffect(STD_MODES template) {
        modPropEffect = new ModifyPropertyEffect(G_PROPS.MODE, MOD_PROP_TYPE.SET, template
                .toString());

        this.mode = template;
    }

    private boolean applyExplorationVersion() {
        /*
        start restoring <?> over time
set mode all the same, just allow breaking it?
while in Mode, restore...

isModeDisablesActions

alert - ?
divination?
         */
        Unit unit = (Unit) getRef().getSourceObj();
        unit.getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().
                unitActivatesMode(unit);
        unit.getGame().getStateManager().reset(unit);
        return false;
    }

    @Override
    public boolean applyThis() {
        if (ExplorationMaster.isExplorationOn()) {
            applyExplorationVersion();
        }
        if (reinit) {
            initBuffEffect();
        }
        if (mode.isDispelOnHit()) {
            addDispelOnHitTrigger();
        }
        if (ExplorationMaster.isExplorationOn()) {
            //dispel on action? in

        } else {
            if (DC_Engine.isAtbMode()) {
                addPeriodicEffect();
                addInitiativeEffect();
            } else if (mode.isEndTurnEffect()) {
                addEndTurnEffect();
            }

        }
        if (mode.getDefenseMod() != 0) {
            addDefModEffect();
        }
        addParamMods();
        addParamBonuses();
        addPropMods();

        if (mode.equals(STD_MODES.ALERT))
            addBuffEffect.addEffect(AlertRule.getWakeUpTriggerEffect());
        if (addBuffEffect.getDuration() == null) {
            if (mode.isContinuous()) {
                addBuffEffect.setDuration(ContentValsManager.INFINITE_VALUE);
            } else {
                addBuffEffect.setDuration(
                        (!ExplorationMaster.isExplorationOn() && DC_Engine.isAtbMode()) ? mode.getDuration()
                                : 1);
            }

        }
        addBuffEffect.setIrresistible(true);
        if (!mode.isContinuous()) {
            addRemoveTrigger();
        }

        Effect e = createOneShotEffect(mode, ref.getTargetObj());
        if (e != null) {
            e.apply(ref);
        }
        return addBuffEffect.apply(ref);
    }

    //TODO NF Rules revamp - add counters?
    private Effect createOneShotEffect(MODE mode, Obj obj) {
        return null;
    }

    private void addInitiativeEffect() {
        addBuffEffect.addEffect(
                new ImmobilizeEffect());
    }

    private void addPeriodicEffect() {
        String periodicValues = mode.getPeriodicValues();
        if (periodicValues == null)
            if (mode != STD_MODES.DIVINATION)
                return;
        Effects effects = new Effects();
        String period = mode.getPeriod();
        for (String substring : ContainerUtils.openContainer(periodicValues)) {
            String amount = VariableManager.getVar(substring, 0);
            String maxAmount = VariableManager.getVar(substring, 1);
            String periodicValue = VariableManager.removeVarPart(substring);

            Formula max = new Formula(maxAmount);
            Formula formula = new Formula(amount);
            effects.add(new ModifyValueEffect(periodicValue, MOD.MODIFY_BY_CONST,
                    formula, max));
        }
        Effect fx = new PeriodicEffect(period, effects);
        fx.setRef(Ref.getSelfTargetingRefCopy(ref.getSourceObj()));
        addBuffEffect.addEffect(fx);

    }

    public synchronized AddBuffEffect getAddBuffEffect() {
        if (addBuffEffect == null) {
            initBuffEffect();
        }
        return addBuffEffect;
    }

    public synchronized void setAddBuffEffect(AddBuffEffect addBuffEffect) {
        this.addBuffEffect = addBuffEffect;
    }

    private void initBuffEffect() {
        this.addBuffEffect = new AddBuffEffect(mode.getBuffName(), modPropEffect);
        if (mode instanceof ModeImpl) {
            addBuffEffect.addEffect(new SetCustomModeEffect(mode));
        }
    }

    private void addPropMods() {
        Map<PROPERTY, String> map = new RandomWizard<PROPERTY>().constructStringWeightMap(mode
                .getPropsAdded(), PROPERTY.class);
        for (PROPERTY property : map.keySet()) {
            if (property != null) {
                addBuffEffect.addEffect(new ModifyPropertyEffect(property,
                        MOD_PROP_TYPE.ADD, map.get(property)));
            }
        }
    }


    private void addParamBonuses() {
        add(false, mode.getParameterBoni());
    }

    private void addParamMods() {
        add(true, mode.getParameterMods());
    }

    private void add(boolean mod, String string) {
        if (!StringMaster.isEmpty(string)) {
            string += ";";
        } else {
            string = "";
        }
        // "Custom Parameters" of old...
        StringBuilder stringBuilder = new StringBuilder(string);
        for (String s : ContainerUtils.open(ref.getSourceObj().getProperty(
                G_PROPS.CUSTOM_PROPS))) {
            if (StringMaster.contains(s, mode.getBuffName(), true, false)) {
                if (StringMaster.contains(s, mod ? PARAM_MOD : PARAM_BONUS, true, false)) {
                    stringBuilder.append(VariableManager.removeVarPart(s)).append(";");
                }
            }
        }
        string = stringBuilder.toString();
        Map<PARAMETER, Integer> map = new RandomWizard<PARAMETER>().constructWeightMap(string,
                PARAMETER.class);
        for (PARAMETER param : map.keySet()) {
            if (param != null) {
                addBuffEffect.addEffect(new ModifyValueEffect(param, mod ? MOD.MODIFY_BY_PERCENT
                        : MOD.MODIFY_BY_CONST, "" + map.get(param)));
            }
        }
    }

    private void addRemoveTrigger() {
        REMOVE_EVENT = mode.getRemoveEvent();
        if (REMOVE_EVENT == null) {
            return;
        }
        Condition c = null;
        if (REMOVE_EVENT == STANDARD_EVENT_TYPE.UNIT_TURN_READY) {
            c = new RefCondition(KEYS.EVENT_SOURCE, KEYS.MATCH);
            // ++ remove disable actions?!
        }
        if (mode.isRemoveEndRound())
            addBuffEffect.addEffect(new DelayedEffect(REMOVE_EVENT, new RemoveBuffEffect(addBuffEffect
                    .getBuffTypeName()), c));
        // .apply(ref);
    }

    private void addDefModEffect() {
        addBuffEffect.addEffect(new ModifyValueEffect(PARAMS.DEFENSE, MOD.MODIFY_BY_PERCENT, mode
                .getDefenseMod()
                + ""));
    }

    private void addDispelOnHitTrigger() {
        Effects effects = new Effects(new RemoveBuffEffect(addBuffEffect.getBuffTypeName()));
        effects.add(InterruptRule.getEffect());
        if (mode.equals(STD_MODES.CHANNELING)) {
            effects.add(new EffectImpl() {
                @Override
                public boolean applyThis() {
                    ChannelingRule.channelingInterrupted((Unit) getRef().getSourceObj());
                    return true;
                }
            });
        }

        STANDARD_EVENT_TYPE event_type = STANDARD_EVENT_TYPE.UNIT_IS_DEALT_TOUGHNESS_DAMAGE; // TODO
        Condition conditions = (mode.equals(STD_MODES.ALERT)) ? InterruptRule.getConditionsAlert()
                : InterruptRule.getConditions();

        addBuffEffect.addEffect(new DelayedEffect(event_type, effects, conditions));
    }

    private void addEndTurnEffect() {
        Condition condition = new StringComparison(prop, mode.toString(), true);
        String formula = mode.getFormula();
        if (ref.getActive() instanceof ActiveObj) {
            ActiveObj activeObj = (ActiveObj) ref.getActive();
            if (activeObj.getParam(PARAMS.FORMULA).contains(Strings.MOD)) {
                formula = StringMaster.wrapInParenthesis(formula) + "*"
                        + activeObj.getParam(PARAMS.FORMULA) + "/100";
            } else if (activeObj.getIntParam(PARAMS.FORMULA) != 0) {
                formula += "+" + activeObj.getIntParam(PARAMS.FORMULA);
            }
        }
        ModifyValueEffect effect = new ModifyValueEffect(mode.getParameter(), MOD.MODIFY_BY_CONST,
                new Formula("min(0, " + formula + ")"));
        PARAMETER param = ContentValsManager.getPARAM(mode.getParameter());
        effect.setParam(param);
        effect.setMaxParam(ContentValsManager.getBaseParameterFromCurrent(param));
        // Formula appendedByModifier = new Formula(formula).
        //         getAppendedByModifier(timeModifier);
        effect.setFormula(new Formula(formula));
        addBuffEffect.addEffect(new DelayedEffect(effect, condition));
        // new DelayedEffect(effect, condition).apply(ref);
    }

    public synchronized MODE getMode() {
        return mode;
    }

    public synchronized void setMode(STD_MODES mode) {
        this.mode = mode;
    }

    public boolean isOnActivateEffect() {
        return onActivateEffect;
    }

    public boolean isOnDeactivateEffect() {
        return onDeactivateEffect;
    }

    public String getProp() {
        return prop;
    }

    public STANDARD_EVENT_TYPE getREMOVE_EVENT() {
        return REMOVE_EVENT;
    }

    public ModifyPropertyEffect getModPropEffect() {
        return modPropEffect;
    }

    public void setReinit(boolean b) {
        reinit = b;
    }

}
