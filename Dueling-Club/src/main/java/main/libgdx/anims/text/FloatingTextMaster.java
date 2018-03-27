package main.libgdx.anims.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import main.ability.effects.common.ModifyStatusEffect;
import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.battlecraft.rules.combat.damage.Damage;
import main.game.battlecraft.rules.combat.damage.MultiDamage;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.GdxColorMaster;
import main.libgdx.StyleHolder;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;
import main.libgdx.anims.CompositeAnim;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridMaster;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.Producer;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.config.ConfigKeys;
import main.system.config.ConfigMaster;
import main.system.images.ImageManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 2/7/2017.
 */
public class FloatingTextMaster {
    private static final float DEFAULT_DISPLACEMENT_Y = 160;
    private static final float DEFAULT_DISPLACEMENT_X = 40;
    private static final float DEFAULT_DURATION = 8.25f;
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
            case BONUS_DAMAGE:
                Damage damage = (Damage) arg;
                return GdxColorMaster.getDamageTypeColor(damage.getDmgType());

            case COSTS:
                Cost cost = (Cost) arg;
                return GdxColorMaster.getParamColor(cost.getPayment().getParamToPay());
            case BATTLE_COMMENT:
                return GdxColorMaster.GOLDEN_WHITE;
            case PARAM_MOD:
                Pair<PARAMETER, Integer> pair = (Pair<PARAMETER, Integer>) arg;
                return (pair.getValue() > 0)
                 ? GdxColorMaster.GOLDEN_WHITE
                 : GdxColorMaster.RED;

        }
        return Color.RED;
    }

    private String getImage(Entity active, TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case PARAM_MOD: {
                Pair<PARAMETER, Integer> pair = (Pair<PARAMETER, Integer>) arg;
                String path = ImageManager.getValueIconPath(pair.getKey());
                if (ImageManager.isImage(path)) {
                    return path;
                }
                return null;
            }
            case BONUS_DAMAGE:
                return ImageManager.getDamageTypeImagePath(
                 String.valueOf(((Damage) arg).getDmgType().getName()), true);
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
            case PARAM_MOD: {
                Pair<PARAMETER, Integer> pair = (Pair<PARAMETER, Integer>) arg;
                if (pair.getValue() > 0)
                    return "+" + pair.getValue();
                return pair.getValue() + "";
            }
            case CANNOT_ACTIVATE:
                DC_ActiveObj activeObj = (DC_ActiveObj) active;
                return activeObj.getCosts().getReasonsString();
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
        if (arg != null) {
            if (!StringMaster.isEmpty(arg.toString())) {
                return arg.toString();
            }
        }

        return aCase.getText();
    }

    public boolean isEventDisplayable(Event e) {
        return getCase(e) != null;
    }

    private TEXT_CASES getCase(Event e) {
//        TEXT_CASES CASE =null ;
//        new EnumMaster<TEXT_CASES>().retrieveEnumConst(TEXT_CASES.class, e.getType().toString());
//        if (CASE != null) {
//            return CASE;
//        }
        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) e.getType()) {
                case DURABILITY_LOST:
                    return TEXT_CASES.DURABILITY_LOSS;
                case UNIT_ACQUIRES_STATUS:
                    return TEXT_CASES.STATUS;
                case UNIT_CHANGES_MODE:
                    return TEXT_CASES.MODE;


                case COSTS_HAVE_BEEN_PAID:
                    return TEXT_CASES.COSTS;
                case ATTACK_CRITICAL:
                    return TEXT_CASES.ATTACK_CRITICAL;
                case ATTACK_DODGED:
                    return TEXT_CASES.ATTACK_DODGED;
                case ATTACK_SNEAK:
                    return TEXT_CASES.ATTACK_SNEAK;
                case ATTACK_INSTANT:
                    return TEXT_CASES.ATTACK_INSTANT;
                case ATTACK_COUNTER:
                    return TEXT_CASES.ATTACK_COUNTER;
                case ATTACK_OF_OPPORTUNITY:
                    return TEXT_CASES.ATTACK_OF_OPPORTUNITY;
                case ATTACK_PARRIED:
                    return TEXT_CASES.ATTACK_PARRIED;
                case ACTION_MISSED:
                    return TEXT_CASES.ATTACK_MISSED;
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
        if (anim == null) {
            if (compositeAnim.getMap().values().iterator().hasNext())
                return;
            anim = compositeAnim.getMap().values().iterator().next();
        }
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


        floatingText.setFontStyle(getFontStyle(CASE, arg));
        floatingText.setDisplacementX(getDisplacementX(CASE));
        floatingText.setDisplacementY(getDisplacementY(CASE));
        floatingText.setDuration(getDefaultDuration(CASE));
        return floatingText;
    }

    private LabelStyle getFontStyle(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case REQUIREMENT:
                break;
            case CANNOT_ACTIVATE:
                break;
            case HIT:
                return StyleHolder.getSizedLabelStyle(StyleHolder.DEFAULT_FONT, 22);
//                DamageFactory.getDamageFromAttack(
//                 DC_AttackMaster.getAttackFromAction(
//                  (DC_ActiveObj) arg))
            case BONUS_DAMAGE:
                int size = 17;
                size = Math.min(22, size + ((Damage) arg).getAmount() / 20);
                return StyleHolder.getSizedLabelStyle(StyleHolder.DEFAULT_FONT, size);
            case ATTACK_CRITICAL:
                break;
            case ATTACK_SNEAK:
                break;
        }
        return null;
    }

    private FloatingText addFloatingText(DC_ActiveObj active,
                                         TEXT_CASES CASE, Object arg, Anim anim, float delay) {
        FloatingText floatingText = getFloatingText(active, CASE, arg);
        floatingText.setDelay(delay);
        floatingText.setDuration(getDefaultDuration(CASE));
        floatingText.setDisplacementX(getDisplacementX(CASE));
        floatingText.setDisplacementY(getDisplacementY(CASE));
//        anim.initPosition(); // TODO rework this!
        if (anim.getOrigin() == null) {
            anim.initPosition();
        }
        if (anim.getDestination() == null) {
            anim.initPosition();
        }
        floatingText.setPosition(CASE.atOrigin ? anim.getOrigin() : anim.getDestination());

        anim.addFloatingText(floatingText
        );

        return floatingText;
    }

    private float getDisplacementY(TEXT_CASES aCase) {

        switch (aCase) {
            case BATTLE_COMMENT:
                return 0;
        }
        return DEFAULT_DISPLACEMENT_Y;
    }

    private float getDisplacementX(TEXT_CASES aCase) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return 0;
        }
        return DEFAULT_DISPLACEMENT_X;
    }

    private float getDefaultDuration(TEXT_CASES aCase) {
        switch (aCase) {
            case REQUIREMENT:
                return 11;
            case BONUS_DAMAGE:
                return 10;
            case BATTLE_COMMENT:
                return 10;
        }
        return DEFAULT_DURATION * ConfigMaster.getInstance().getInt(ConfigKeys.FLOATING_TEXT_DURATION);
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

    public void createFloatingText(TEXT_CASES CASE, String arg, Entity entity) {
        FloatingText text;
        try {
            text = getFloatingText(entity, CASE, arg);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return;
        }
        if (entity instanceof BattleFieldObject) {
            Vector2 v = GridMaster.getCenteredPos(((BattleFieldObject) entity).getCoordinates());
            text.setPosition(v);
        } else {
            if (entity instanceof DC_ActiveObj) {
                Vector2 v = GridMaster.getCenteredPos(
                 ((DC_ActiveObj) entity).getOwnerObj().getCoordinates());
                text.setPosition(v);
            }
        }
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, text);
    }

    public void createAndShowParamModText(Object o) {
        Pair<PARAMETER, Ref> pair = (Pair<PARAMETER, Ref>) o;
        Entity active = pair.getValue().getObj(KEYS.ACTIVE);
        FloatingText text = getFloatingText(active,
         TEXT_CASES.PARAM_MOD,
         new ImmutablePair<>(pair.getKey(), pair.getValue().getAmount()));
        Obj obj = active.getRef().getTargetObj();
        if (obj == null)
            obj = active.getRef().getSourceObj();
        if (obj != null) {
            BaseView view = DungeonScreen.getInstance().getGridPanel().getUnitMap().get(obj);
            if (view != null) {
                Vector2 v = view.localToStageCoordinates(new Vector2(view.getX(), view.getY()));
                text.setPosition(v.x, v.y);
            }
        }
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, text);
    }


    public enum TEXT_CASES {
        DEFAULT,
        REQUIREMENT,
        CANNOT_ACTIVATE,

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
        PARAM_MOD,
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
        }),

        BATTLE_COMMENT,

        HIT {
            @Override
            public Object[] getArgs(Event e) {
                return new Object[]{
                 e.getRef().getAmount()
                };
            }
        }, DURABILITY_LOSS;
        public boolean atOrigin;
        String name = StringMaster.getWellFormattedString(name());
        private Producer<Event, Object[]> argProducer;

        TEXT_CASES() {

        }

        TEXT_CASES(boolean atOrigin, Producer<Event, Object[]> producer) {
            this.atOrigin = atOrigin;
            this.argProducer = producer;
        }

        public String getText() {
            return name;
        }

        public Object[] getArgs(Event e) {
            if (argProducer == null) {
                return new Object[]{
                 StringMaster.getWellFormattedString(e.getType().toString())
                };
            }
            return argProducer.produce(e);
        }
    }

}
