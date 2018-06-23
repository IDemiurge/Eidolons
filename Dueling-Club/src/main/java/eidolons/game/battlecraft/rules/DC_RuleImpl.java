package eidolons.game.battlecraft.rules;

import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.game.core.game.GenericGame;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Rule;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.LOG_CHANNEL;

import java.util.Arrays;

public abstract class DC_RuleImpl implements Rule {
    protected EVENT_TYPE event_type;
    protected EVENT_TYPE[] event_types;
    protected Conditions conditions = new Conditions();
    protected GenericGame game;
    protected Effect effects;
    protected boolean on = true;
    private boolean initialized = false;


    public DC_RuleImpl(GenericGame game) {
        this.game = game;
        initEventType();
        initConditions();
        if (isFastFailConditions()) {
            conditions.setFastFailOnCheck(true);
        }
    }

    protected boolean isFastFailConditions() {
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void apply(Ref ref) {
        initEffects();
        LogMaster.log(LOG_CHANNEL.RULES_DEBUG, toString() + " applies! "
         + ref);
        effects.setReconstruct(true);
        // effects.setForcedLayer(Effect.BUFF_RULE);
        effects.apply(ref);

    }

    public abstract void initEffects();

    public abstract void initConditions();

    @Override
    public boolean check(Event event) {
        if (isOn())
            return false;
        if (event_types != null) {
            if (!Arrays.asList(event_types).contains(event.getType())) {
                return false;
            }

        } else {
            if (!getEventType().equals(event.getType())) {
                return false;
            }

        }
        if (conditions == null) {
            return true;
        }
        Ref ref = Ref.getCopy(event.getRef());
        ref.setEvent(event);
        LogMaster.log(LOG_CHANNEL.RULES_DEBUG, toString() + " checked on "
         + ref.getSourceObj());
        return conditions.preCheck(ref);
    }

    public EVENT_TYPE getEventType() {
        return event_type;
    }

    public abstract void initEventType();


    @Override
    public boolean isOn() {
        if (getRuleEnum() == null) {
            return on;
        }
        return RuleKeeper.isRuleOn(getRuleEnum());

    }

    public void setOn(boolean on) {
        this.on = on;
    }

    protected RULE getRuleEnum() {
        return null;
    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }
}
