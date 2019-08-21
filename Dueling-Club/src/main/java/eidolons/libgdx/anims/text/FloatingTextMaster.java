package eidolons.libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.ability.effects.common.ModifyStatusEffect;
import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.MultiDamage;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.Animation;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.system.config.ConfigKeys;
import eidolons.system.config.ConfigMaster;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.Producer;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 2/7/2017.
 */
public class FloatingTextMaster {
    public static final float DEFAULT_DISPLACEMENT_Y = 160;
    public static final float DEFAULT_DISPLACEMENT_X = 40;
    public static final float DEFAULT_DURATION = 8.5f;
    private static FloatingTextMaster instance;
    public static boolean displacementInvertFlag;

    private FloatingTextMaster() {

    }

    public static FloatingTextMaster getInstance() {
        if (instance == null) {
            instance = new FloatingTextMaster();
        }
        return instance;
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
                return GdxColorMaster.PALE_GOLD;

            case ATTACK_CRITICAL:
                return GdxColorMaster.ORANGE;
            case ATTACK_SNEAK:
                return GdxColorMaster.PURPLE;
            case ATTACK_DODGED:
            case ATTACK_BLOCKED:
            case ATTACK_PARRIED:
                return GdxColorMaster.YELLOW;
            case ATTACK_MISSED:
                return GdxColorMaster.PALE_GREEN;
            case ATTACK_OF_OPPORTUNITY:
                break;
            case ATTACK_COUNTER:
                break;
            case ATTACK_INSTANT:
                break;


            case PARAM_MOD:
                Pair<PARAMETER, Integer> pair = (Pair<PARAMETER, Integer>) arg;
                return (pair.getValue() > 0)
                        ? GdxColorMaster.PALE_GOLD
                        : GdxColorMaster.RED;

            case GOLD:
                return GdxColorMaster.YELLOW;
            case XP:
                return GdxColorMaster.LILAC;
            case LEVEL_UP:
                return GdxColorMaster.YELLOW;

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
            case ANNIHILATED:
                return "Annihilated!";
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

    public static boolean isEventDisplayable(Event e) {
        return getCase(e) != null;
    }

    private static TEXT_CASES getCase(Event e) {
//        TEXT_CASES CASE =null ;
//        new EnumMaster<TEXT_CASES>().retrieveEnumConst(TEXT_CASES.class, e.getType().toString());
//        if (CASE != null) {
//            return CASE;
//        }
        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) e.getType()) {
                case UNIT_HAS_BEEN_ANNIHILATED:
                    return TEXT_CASES.ANNIHILATED;
                case DURABILITY_LOST:
                    return TEXT_CASES.DURABILITY_LOSS;
                case UNIT_ACQUIRES_STATUS:
                    return TEXT_CASES.STATUS;
                case UNIT_CHANGES_MODE:
                    return TEXT_CASES.MODE;


                case COSTS_HAVE_BEEN_PAID:
                    if (ExplorationMaster.isExplorationOn())
                        if (e.getRef().getActive() != null) {
                            if (e.getRef().getActive().isMove())
                                return null;
                            if (e.getRef().getActive().isTurn())
                                return null;
                        }

                    if (!e.getRef().getSourceObj().isMine()) {
                        if (((DC_Obj) e.getRef().getSourceObj()).getPlayerVisionStatus() != PLAYER_VISION.DETECTED)
                            return null;
                    }

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
        Animation anim = compositeAnim.getMap().get(part);
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


            LogMaster.verbose(e + "***** adding floating text for " + anim + " : " + floatingText);

        }
    }

    public FloatingText getFloatingText(Entity active, TEXT_CASES CASE, Object arg) {

        FloatingText floatingText =
                new FloatingText(
                        () -> getText(active, CASE, arg), () -> getImage(active, CASE, arg)
                        , getColor(CASE, arg));

        floatingText.setStayFullDuration(getStayFull(CASE, arg));
        floatingText.setFadeInDuration(getFadeIn(CASE, arg));

        Eidolons.onThisOrGdxThread(() -> floatingText.setFontStyle(getFontStyle(CASE, arg)));

        floatingText.setDisplacementX(getDisplacementX(CASE));
        floatingText.setDisplacementY(getDisplacementY(CASE));

        floatingText.setOffsetX(getOffsetX(CASE, arg));
        floatingText.setOffsetY(getOffsetY(CASE, arg));
        floatingText.setDuration(getDefaultDuration(CASE, arg));
        return floatingText;
    }

    public static float getOffsetY(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return -228;
        }
        return 0;
    }

    public static float getOffsetX(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return -228;
        }
        return 0;
    }

    public static float getFadeIn(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return 3;
        }
        return 0;
    }

    public static float getStayFull(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return 6 + new Float(arg.toString().length()) / 40;
        }
        return 0;
    }


    private LabelStyle getFontStyle(TEXT_CASES aCase, Object arg) {
        int size = 21;
        switch (aCase) {
            case BATTLE_COMMENT:
                size -= Math.min(4, arg.toString().length() / 100);
                return
                        StyleHolder.getSizedLabelStyle(FONT.CHANCERY, size);
            case GOLD:
            case XP:
                StyleHolder.getSizedLabelStyle(FONT.MAIN, 20);
            case LEVEL_UP:
                return
                        StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 26);
            case REQUIREMENT:
                break;
            case CANNOT_ACTIVATE:
                break;
            case HIT:
                return StyleHolder.getSizedLabelStyle(StyleHolder.DEFAULT_FONT, 23);
//                DamageFactory.getDamageFromAttack(
//                 DC_AttackMaster.getAttackFromAction(
//                  (DC_ActiveObj) arg))
            case BONUS_DAMAGE:
                size = 18;
                size = Math.max(14, Math.min(23, size + ((Damage) arg).getAmount() / 21));
                return StyleHolder.getSizedLabelStyle(StyleHolder.DEFAULT_FONT, size);
            case ATTACK_CRITICAL:
                break;
            case ATTACK_SNEAK:
                break;
        }
        return StyleHolder.getSizedLabelStyle(
                StyleHolder.DEFAULT_FONT_FLOAT_TEXT, StyleHolder.DEFAULT_FONT_SIZE_FLOAT_TEXT);

    }

    private FloatingText addFloatingText(DC_ActiveObj active,
                                         TEXT_CASES CASE, Object arg, Animation animation, float delay) {
        Anim anim = null;
        if (animation instanceof Anim) {
            anim = ((Anim) animation);
        } else
            return null;


        FloatingText floatingText = getFloatingText(active, CASE, arg);
        floatingText.setDelay(delay);
        floatingText.setDuration(getDefaultDuration(CASE, arg));
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
        displacementInvertFlag = !displacementInvertFlag;
        return
                displacementInvertFlag
                        ? -DEFAULT_DISPLACEMENT_X
                        : DEFAULT_DISPLACEMENT_X;
    }

    public static float getDefaultDuration(TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case REQUIREMENT:
                return 14;
            case BONUS_DAMAGE:
                return 10;
            case BATTLE_COMMENT:
                return 8;
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
                return ANIM_PART.MISSILE;
        }
        return ANIM_PART.IMPACT;
    }

    public void createFloatingText(TEXT_CASES CASE, String arg, Entity entity) {
        if (GdxMaster.isLwjglThread()) {
            createFloatingText(CASE, arg, entity, null);
        } else
            Gdx.app.postRunnable(() -> {
                try {
                    createFloatingText(CASE, arg, entity, null);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            });

    }

    private void createFloatingText(TEXT_CASES CASE, String arg, Entity entity,
                                    Stage atCursorStage) {
        createFloatingText(CASE, arg, entity, atCursorStage, null);
    }

    public void createFloatingText(TEXT_CASES CASE, String arg, Entity entity,
                                   Stage atCursorStage, Vector2 at) {
        FloatingText text;
        try {
            text = getFloatingText(entity, CASE, arg);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return;
        }
        Vector2 v = at;
        if (v == null) {
            if (atCursorStage != null)
                v = GdxMaster.getCursorPosition(atCursorStage);
            else if (entity instanceof BattleFieldObject) {
                v = GridMaster.getCenteredPos(((BattleFieldObject) entity).getCoordinates());
                text.setPosition(v);
            } else {
                if (entity instanceof DC_ActiveObj) {
                    v = GridMaster.getCenteredPos(
                            ((DC_ActiveObj) entity).getOwnerUnit().getCoordinates());

                }
            }
        }
//        else
//        {
//            main.system.auxiliary.log.LogMaster.dev("Text at " +
//                    v + " " + text.getText());
//        }
        text.setPosition(v);
        if (at == null)
            if (entity instanceof Unit) {
                text.debugAll();
                float height = 629;// text.getHeight();
                float width = 537;// text.getWidth();
                FACING_DIRECTION f = ((Unit) entity).getFacing().flip();
                switch (f.rotate(f.isCloserToZero())) {
                    case NORTH:
                        text.setY(text.getY() + height / 2);
                        break;
                    case WEST:
                        text.setX(text.getX() - width / 2);
                        break;
                    case EAST:
                        text.setX(text.getX() + width / 2);
                        break;
                    case SOUTH:
                        text.setY(text.getY() - height / 2);
                        break;
                }
            }


        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, text);
    }

    public void createAndShowParamModText(Object o) {
        Pair<PARAMETER, Ref> pair = (Pair<PARAMETER, Ref>) o;
        Entity active = pair.getValue().getObj(KEYS.ACTIVE);
        int amount = 0;
        if (pair.getValue().getAmount() != null)
            amount = pair.getValue().getAmount();
        else {
            return;
        }
        FloatingText text = getFloatingText(active,
                TEXT_CASES.PARAM_MOD,
                new ImmutablePair<>(pair.getKey(), amount));
        Obj obj = active.getRef().getTargetObj();
        if (obj == null)
            obj = active.getRef().getSourceObj();
        if (obj != null) {
            BaseView view = DungeonScreen.getInstance().getGridPanel().getViewMap().get(obj);
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
        }, DURABILITY_LOSS, ANNIHILATED,
        XP,
        GOLD,
        LEVEL_UP;
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
