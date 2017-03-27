package main.libgdx.anims.text;

import com.badlogic.gdx.graphics.Color;
import main.ability.effects.common.ModifyStatusEffect;
import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.elements.costs.Cost;
import main.entity.Entity;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.game.ai.tools.target.EffectFinder;
import main.game.logic.combat.damage.Damage;
import main.game.logic.combat.damage.MultiDamage;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.GdxColorMaster;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.system.Producer;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.config.ConfigKeys;
import main.system.config.ConfigMaster;
import main.system.images.ImageManager;

import java.util.List;

/**
 * Created by JustMe on 2/7/2017.
 */
public class FloatingTextMaster {
    private static final float DEFAULT_DISPLACEMENT_Y = 140;
    private static final float DEFAULT_DISPLACEMENT_X =  0;
    private static final float DEFAULT_DURATION = 3;
    private static FloatingTextMaster instance;

    public FloatingTextMaster() {
        instance = this;
    }

    public static FloatingTextMaster getInstance() {
        if (instance == null) {
            instance = new FloatingTextMaster();
        }
        return instance;
    }

    public static void setInstance(FloatingTextMaster instance) {
        FloatingTextMaster.instance = instance;
    }

    private Color getColor(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case COSTS:
                Cost cost = (Cost) arg;
                return GdxColorMaster.getParamColor(cost.getPayment().getParamToPay());

        }
        return Color.RED;
    }

    private String getImage(Entity active, TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BONUS_DAMAGE:
                return ImageManager.getDamageTypeImagePath(
                 String.valueOf(((Damage) arg).getDmgType().getName()));
            case ATTACK_CRITICAL:
                break;
            case ATTACK_SNEAK:
                break;
            case ATTACK_DODGED:
                break;
            case COSTS:
                Cost cost = (Cost) arg;
                return ImageManager.getValueIconPath(cost.getPayment().getParamToPay());
            case STATUS:
                break;
            case MODE:
                break;
        }
        return null;
    }

    private String getText(Entity active, TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BONUS_DAMAGE:
                return String.valueOf(((Damage) arg).getAmount());
            case ATTACK_CRITICAL:
                return "Critical Attack!";
            case ATTACK_SNEAK:
                return "Sneak Attack!";
            case COSTS:
                Cost cost = (Cost) arg;
                return String.valueOf(-cost.getPayment().getLastPaid());
        }
        if (arg != null)
            if (!StringMaster.isEmpty(arg.toString()))
                return arg.toString();

        return aCase.getText();
    }

    public boolean isEventDisplayable(Event e) {
        return getCase(e) != null;
    }

    private TEXT_CASES getCase(Event e) {
        TEXT_CASES CASE = new EnumMaster<TEXT_CASES>().retrieveEnumConst(TEXT_CASES.class, e.getType().toString());
        if (CASE != null) return CASE;
        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) e.getType()) {
                case COSTS_HAVE_BEEN_PAID:
                    return TEXT_CASES.COSTS;
                case ATTACK_CRITICAL:
                    return TEXT_CASES.ATTACK_CRITICAL;
                case ATTACK_DODGED:
                    return TEXT_CASES.ATTACK_DODGED;
            }
        }
        return null;
    }

    public void addFloatingTextForEventAnim(Event e, CompositeAnim compositeAnim) {
        TEXT_CASES CASE = getCase(e);
        if (CASE == null) {
            return;
        }
        ANIM_PART part = getPart(CASE);
        Anim anim = compositeAnim.getMap().get(part);
        Object[] args = CASE.getArgs(e);
        DC_ActiveObj active = (DC_ActiveObj) e.getRef().getActive();
        float delay = 0;
        for (Object arg : args) {
            FloatingText floatingText = addFloatingText(active, CASE, arg, anim, delay);

            delay += floatingText.getDuration() / 2;


            LogMaster.log(1, e + "***** adding floating text for " + anim + " : " + floatingText);

        }
    }

    public FloatingText getFloatingText(Entity active, TEXT_CASES CASE, Object arg) {

        FloatingText floatingText =
         new FloatingText(
          () -> getText(active, CASE, arg), () -> getImage(active, CASE, arg)
          , getColor(CASE, arg));


//        floatingText.setDisplacementX(x);
//        floatingText.setDisplacementY(y);
//        floatingText.setDuration(dur);
        return floatingText;
    }
    private FloatingText addFloatingText(DC_ActiveObj active,
                                         TEXT_CASES CASE, Object arg, Anim anim, float delay) {
        FloatingText floatingText = getFloatingText(active, CASE, arg);
        floatingText.setDelay(delay);
        floatingText.setDuration(getDefaultDuration(CASE));
        floatingText.setDisplacementX(getDisplacementX(CASE));
        floatingText.setDisplacementY(getDisplacementY(CASE));
        anim.initPosition(); // TODO rework this!
        floatingText.setPosition(CASE.atOrigin ? anim.getOrigin() : anim.getDestination());
        anim.addFloatingText(floatingText
        );

        return floatingText;
    }

    private float getDisplacementY(TEXT_CASES aCase) {
        return DEFAULT_DISPLACEMENT_Y;
    }

    private float getDisplacementX(TEXT_CASES aCase) {
        return DEFAULT_DISPLACEMENT_Y;
    }

    private float getDefaultDuration(TEXT_CASES aCase) {
        return DEFAULT_DURATION* ConfigMaster.getInstance().getInt(ConfigKeys.FLOATING_TEXT_DURATION);
    }

    public void initFloatTextForDamage(Damage damage, Anim anim) {
        if (damage instanceof MultiDamage) {
            float delay = 0;
            for (Damage bonus : ((MultiDamage) damage).getAdditionalDamage()) {
                FloatingText floatingText = addFloatingText(
                 (DC_ActiveObj) damage.getRef()
                  .getObj(KEYS.ACTIVE),
                 TEXT_CASES.BONUS_DAMAGE, bonus, anim, delay);
                delay += floatingText.getDuration() / 2;
            }
        }
    }

    private ANIM_PART getPart(TEXT_CASES aCase) {
        switch (aCase) {
            case COSTS:
                return ANIM_PART.MAIN;
        }
        return ANIM_PART.IMPACT;
    }



    public enum TEXT_CASES {
        DEFAULT,
        REQUIREMENT,

        BONUS_DAMAGE,
        ATTACK_CRITICAL,
        ATTACK_SNEAK,

        ATTACK_DODGED,
        ATTACK_BLOCKED,
        ATTACK_PARRIED,
        ATTACK_MISSED,

        ATTACK_OF_OPPORTUNITY,
        ATTACK_COUNTER,
        ATTACK_INSTANT,

        ROLL,

        COSTS(true, (e) -> {
            DC_ActiveObj a = (DC_ActiveObj) e.getRef().getActive();
            List<Cost> costs = a.getCosts().getCosts();
            costs.removeIf(c -> c.getPayment().getLastPaid() == 0
//            getAmountFormula().toString().isEmpty()
            );
            return costs.toArray();
        }),
        STATUS(
         false, (e) -> {
            ModifyStatusEffect ef = (ModifyStatusEffect)
             EffectFinder.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
//                if (ef==null )
            return ef.getValue().split(";");
        }),
        MODE(
         false, (e) -> {
            ModeEffect ef = (ModeEffect)
             EffectFinder.getFirstEffectOfClass((DC_ActiveObj) e.getRef().getActive(), ModifyStatusEffect.class);
            return new Object[]{
             ef.getMode()
            };
        });
        public boolean atOrigin;
        private Producer<Event, Object[]> argProducer;

        TEXT_CASES() {

        }

        TEXT_CASES(boolean atOrigin, Producer<Event, Object[]> producer) {
            this.atOrigin = atOrigin;
            this.argProducer = producer;
        }

        public String getText() {
            return StringMaster.getWellFormattedString(name());
        }

        public Object[] getArgs(Event e) {
            if (argProducer == null) {
                return new Object[]{
                 "arg!"
                };
            }
            return argProducer.produce(e);
        }
    }

}
