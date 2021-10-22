package libgdx.anims.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.FacingEntity;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.MultiDamage;
import eidolons.game.core.Core;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.Anim;
import eidolons.content.consts.VisualEnums.ANIM_PART;
import libgdx.anims.Animation;
import libgdx.anims.CompositeAnim;
import libgdx.bf.GridMaster;
import libgdx.bf.grid.cell.BaseView;
import libgdx.screens.ScreenMaster;
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
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private Color getColor(VisualEnums.TEXT_CASES aCase, Object arg) {
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
            case LEVEL_UP:

            case GOLD:
                return GdxColorMaster.YELLOW;
            case ATTACK_MISSED:
                return GdxColorMaster.PALE_GREEN;
            case ATTACK_OF_OPPORTUNITY:
            case ATTACK_INSTANT:
            case COUNTER_ATTACK:
                break;


            case PARAM_MOD:
                Pair<PARAMETER, Integer> pair = (Pair<PARAMETER, Integer>) arg;
                return (pair.getValue() > 0)
                        ? GdxColorMaster.PALE_GOLD
                        : GdxColorMaster.RED;
            case XP:
                return GdxColorMaster.LILAC;

        }
        return Color.RED;
    }

    private String getImage(Entity active, VisualEnums.TEXT_CASES aCase, Object arg) {
        if (EidolonsGame.FOOTAGE) {

            switch (aCase) {
                //                case COSTS:
                case BONUS_DAMAGE:
                    break;
                default:
                    return "";
            }
        }

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
            case MODE:
            case STATUS:
            case ATTACK_DODGED:
            case ATTACK_SNEAK:
                break;
            case COSTS:
                Cost cost = (Cost) arg;
                if (cost.getPayment().getParamToPay() != null) {
                    return ImageManager.getValueIconPath(cost.getPayment().getParamToPay());
                }
                return ImageManager.getValueIconPath(cost.getCostParam());
        }
        return null;
    }

    private String getText(Entity active, VisualEnums.TEXT_CASES aCase, Object arg) {
        if (EidolonsGame.FOOTAGE) {
            switch (aCase) {
                //                case COSTS:
                case BONUS_DAMAGE:
                    break;
                default:
                    return "";
            }
        }

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

    private static VisualEnums.TEXT_CASES getCase(Event e) {
        //        TEXT_CASES CASE =null ;
        //        new EnumMaster<TEXT_CASES>().retrieveEnumConst(TEXT_CASES.class, e.getType().toString());
        //        if (CASE != null) {
        //            return CASE;
        //        }
        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) e.getType()) {
                case UNIT_HAS_BEEN_ANNIHILATED:
                    return VisualEnums.TEXT_CASES.ANNIHILATED;
                case DURABILITY_LOST:
                    return VisualEnums.TEXT_CASES.DURABILITY_LOSS;
                case UNIT_ACQUIRES_STATUS:
                    return VisualEnums.TEXT_CASES.STATUS;
                case UNIT_CHANGES_MODE:
                    return VisualEnums.TEXT_CASES.MODE;


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

                    return VisualEnums.TEXT_CASES.COSTS;
                case ATTACK_CRITICAL:
                    return VisualEnums.TEXT_CASES.ATTACK_CRITICAL;
                case ATTACK_DODGED:
                    return VisualEnums.TEXT_CASES.ATTACK_DODGED;
                case ATTACK_SNEAK:
                    return VisualEnums.TEXT_CASES.ATTACK_SNEAK;
                case ATTACK_INSTANT:
                    return VisualEnums.TEXT_CASES.ATTACK_INSTANT;
                case ATTACK_COUNTER:
                    return VisualEnums.TEXT_CASES.COUNTER_ATTACK;
                case ATTACK_OF_OPPORTUNITY:
                    return VisualEnums.TEXT_CASES.ATTACK_OF_OPPORTUNITY;
                case ATTACK_PARRIED:
                    return VisualEnums.TEXT_CASES.ATTACK_PARRIED;
                case ACTION_MISSED:
                    return VisualEnums.TEXT_CASES.ATTACK_MISSED;
            }
        }
        return null;
    }

    public void addFloatingTextForEventAnim(Event e, CompositeAnim compositeAnim) {
        VisualEnums.TEXT_CASES CASE = getCase(e);
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

    public FloatingText getFloatingText(Entity active, VisualEnums.TEXT_CASES CASE, Object arg) {
        String text = getText(active, CASE, arg);
        if (text == null) {
            text = "";
        }
        String finalText = text;
        FloatingText floatingText =
                new FloatingText(
                        finalText::trim, () -> getImage(active, CASE, arg)
                        , getColor(CASE, arg));

        floatingText.setStayFullDuration(getStayFull(CASE, arg));
        floatingText.setFadeInDuration(getFadeIn(CASE, arg));

        Core.onThisOrGdxThread(() -> floatingText.setFontStyle(getFontStyle(CASE, arg)));

        floatingText.setDisplacementX(getDisplacementX(CASE));
        floatingText.setDisplacementY(getDisplacementY(CASE));

        floatingText.setOffsetX(getOffsetX(CASE, arg));
        floatingText.setOffsetY(getOffsetY(CASE, arg));
        floatingText.setDuration(getDefaultDuration(CASE, arg));
        return floatingText;
    }

    public static float getOffsetY(VisualEnums.TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return -228;
        }
        return 0;
    }

    public static float getOffsetX(VisualEnums.TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return -228;
        }
        return 0;
    }

    public static float getFadeIn(VisualEnums.TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return 3.7f;
        }
        return 0;
    }

    public static float getStayFull(VisualEnums.TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case BATTLE_COMMENT:
                return 6 + (float) arg.toString().length() / 40;
        }
        return 0;
    }


    private LabelStyle getFontStyle(VisualEnums.TEXT_CASES aCase, Object arg) {
        int size = 21;
        switch (aCase) {
            case BATTLE_COMMENT:
                //                if (arg.toString().contains("(psychic)")) {
                //                    size=20;
                //                    return
                //                            StyleHolder.getSizedLabelStyle(FONT.SUPER_KNIGHT, size);
                //                }
                size++;
                size -= Math.min(2, arg.toString().length() / 200);
                return
                        StyleHolder.getSizedLabelStyle(FONT.CHANCERY, size);
            //            return
            //                    StyleHolder.getSizedLabelStyle(FONT.IMMORTAL, size);
            case GOLD:
            case XP:
                StyleHolder.getSizedLabelStyle(FONT.MAIN, 20);
            case LEVEL_UP:
                return
                        StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 26);
            case REQUIREMENT:
            case ATTACK_SNEAK:
            case ATTACK_CRITICAL:
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
        }
        return StyleHolder.getSizedLabelStyle(
                StyleHolder.DEFAULT_FONT_FLOAT_TEXT, StyleHolder.DEFAULT_FONT_SIZE_FLOAT_TEXT);

    }

    private FloatingText addFloatingText(DC_ActiveObj active,
                                         VisualEnums.TEXT_CASES CASE, Object arg, Animation animation, float delay) {
        Anim anim;
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
            try {
                anim.initPosition();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (anim.getDestination() == null) {
            try {
                anim.initPosition();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        floatingText.setPosition(CASE.atOrigin ? anim.getOrigin() : anim.getDestination());

        anim.addFloatingText(floatingText
        );

        return floatingText;
    }

    private float getDisplacementY(VisualEnums.TEXT_CASES aCase) {

        switch (aCase) {
            case BATTLE_COMMENT:
                return 0;
        }
        return DEFAULT_DISPLACEMENT_Y;
    }

    private float getDisplacementX(VisualEnums.TEXT_CASES aCase) {
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

    public static float getDefaultDuration(VisualEnums.TEXT_CASES aCase, Object arg) {
        switch (aCase) {
            case REQUIREMENT:
                return 14;
            case BONUS_DAMAGE:
                return 10;
            case BATTLE_COMMENT:
                return 8;
            //            case DURABILITY_LOSS:
        }
        return DEFAULT_DURATION  ;
    }

    public void initFloatTextForDamage(Damage damage, Anim anim) {
        if (damage instanceof MultiDamage) {
            float delay = 0;
            for (Damage bonus : ((MultiDamage) damage).getAdditionalDamage()) {
                FloatingText floatingText = addFloatingText(
                        (DC_ActiveObj) damage.getRef()
                                .getObj(KEYS.ACTIVE),
                        VisualEnums.TEXT_CASES.BONUS_DAMAGE, bonus, anim, delay);
                delay += floatingText.getDuration() / 2;
            }
        }
    }

    private ANIM_PART getPart(VisualEnums.TEXT_CASES aCase) {
        switch (aCase) {
            case COSTS:
                return VisualEnums.ANIM_PART.MISSILE;
        }
        return VisualEnums.ANIM_PART.IMPACT;
    }

    public void createFloatingText(VisualEnums.TEXT_CASES CASE, String arg, Entity entity) {
        if (ScreenMaster.getGrid() == null) {
            main.system.auxiliary.log.LogMaster.devLog("Cannot do float text w/o grid: " + arg);
            return;
        }
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

    private FloatingText createFloatingText(VisualEnums.TEXT_CASES CASE, String arg, Entity entity,
                                            Stage atCursorStage) {
        return createFloatingText(CASE, arg, entity, atCursorStage, null, null, null, true);
    }

    public FloatingText createFloatingText(VisualEnums.TEXT_CASES CASE, String arg, Entity entity,
                                           Stage atCursorStage, Vector2 at, LabelStyle overrideFont, AtomicBoolean waiterFlag, boolean autoShow) {
        FloatingText text = null;
        try {
            text = getFloatingText(entity, CASE, arg);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return text;
        }
        if (waiterFlag != null) {
            text.setStayFullCondition(time -> waiterFlag.get());
        }
        Vector2 v = at;
        if (v == null) {
            if (atCursorStage != null)
                v = GdxMaster.getCursorPosition(atCursorStage);
            else if (entity instanceof FacingEntity) {
                v = GridMaster.getCenteredPos(((FacingEntity) entity).getCoordinates());
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
            if (entity instanceof FacingEntity) {
                float height = 629;// text.getHeight();
                float width = 537;// text.getWidth();
                if (overrideFont != null) {
                    width = 620;
                }
                FACING_DIRECTION f = ((FacingEntity) entity).getFacing().flip();
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

        text.setCase(CASE);
        if (overrideFont != null) {
            text.setFontStyle(overrideFont);
        }
        if (autoShow) {
            GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, text);
        }
        return text;
    }

    public void createAndShowParamModText(Object o) {
        Pair<PARAMETER, Ref> pair = (Pair<PARAMETER, Ref>) o;
        Entity active = pair.getValue().getObj(KEYS.ACTIVE);
        int amount;
        if (pair.getValue().getAmount() != null)
            amount = pair.getValue().getAmount();
        else {
            return;
        }
        FloatingText text = getFloatingText(active,
                VisualEnums.TEXT_CASES.PARAM_MOD,
                new ImmutablePair<>(pair.getKey(), amount));
        Obj obj = active.getRef().getTargetObj();
        if (obj == null)
            obj = active.getRef().getSourceObj();
        if (obj != null) {
            BaseView view = ScreenMaster.getGrid().getViewMap().get(obj);
            if (view != null) {
                Vector2 v = view.localToStageCoordinates(new Vector2(view.getX(), view.getY()));
                text.setPosition(v.x, v.y);
            }
        }
        GuiEventManager.trigger(GuiEventType.ADD_FLOATING_TEXT, text);
    }


}
