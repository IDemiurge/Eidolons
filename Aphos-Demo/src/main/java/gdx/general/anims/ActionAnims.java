package gdx.general.anims;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import content.RNG;
import eidolons.content.consts.VisualEnums;
import gdx.dto.UnitDto;
import gdx.views.FieldView;
import gdx.views.HeroView;
import gdx.views.UnitView;
import gdx.visuals.front.HeroZone;
import gdx.visuals.front.ViewManager;
import gdx.visuals.lanes.LanesField;
import libgdx.GdxMaster;
import libgdx.anims.fullscreen.Screenshake;
import logic.core.Aphos;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.combat.CombatLogic.ATK_OUTCOME;
import logic.lane.HeroPos;
import logic.lane.LanePos;
import main.game.bf.directions.DIRECTION;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.auxiliary.data.ArrayMaster;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static logic.functions.combat.HeroMoveLogic.*;

public class ActionAnims {
    private static AnimDrawer animDrawer;

    public enum DUMMY_ANIM_TYPE {
        lane_hit, lane_atk, hero_atk, lane_death, hero_death, hero_hit, explode,
    }
    static {
        GuiEventManager.bind(AphosEvent.UNIT_MOVE, p -> moveUnit(p.get("unit", Unit.class), p.get("pos", LanePos.class)));

        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_ATK, p -> animOn(p,  DUMMY_ANIM_TYPE.lane_atk));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_HIT, p -> animOn(p,  DUMMY_ANIM_TYPE.lane_hit));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_DEATH, p -> animOn(p,  DUMMY_ANIM_TYPE.lane_death));

        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_EXPLODE, p -> animOn(p,  DUMMY_ANIM_TYPE.explode));

        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_HIT_HERO, p -> animOn(p,  DUMMY_ANIM_TYPE.hero_hit));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_DEATH_HERO, p -> animOn(p,  DUMMY_ANIM_TYPE.hero_death));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_ATK_HERO, p -> animOn(p,  DUMMY_ANIM_TYPE.hero_atk));

    }

    private static void animOn(EventCallbackParam p, DUMMY_ANIM_TYPE type) {
        animOn(p.get("target", Entity.class), type, p.get("atk_outcome", ATK_OUTCOME.class), p.getNamedArgs());
    }

    public static void animOn(Entity entity, DUMMY_ANIM_TYPE type, ATK_OUTCOME outcome, Map<String, Object> namedArgs) {
        //int intensity - for shake, ..
        //use hit anims to 'spray'

        FieldView view = ViewManager.getView(entity);
        Vector2 pos = ViewManager.getAbsPos(view);
        String spritePath = SpriteFinder.getSpritePath(type, entity, outcome, namedArgs);
        SpriteAnim animation = animDrawer.add(pos, spritePath, type);

        switch (type) {
            case explode ->{
                animation.addOnFinish(() -> {
                    Map<Unit, ATK_OUTCOME> target_units = (Map<Unit, ATK_OUTCOME>) namedArgs.get("target_units");
                    for (Unit target_unit : target_units.keySet()) {
                        animOn(target_unit, DUMMY_ANIM_TYPE.lane_hit, target_units.get(target_unit), new HashMap<>());
                    }
                    Map<Hero, ATK_OUTCOME> target_heroes = (Map<Hero, ATK_OUTCOME>) namedArgs.get("target_heroes");
                    for (Hero target_hero : target_heroes.keySet()) {
                        animOn(target_hero, DUMMY_ANIM_TYPE.hero_hit, target_units.get(target_hero), new HashMap<>());
                    }
                });
                GuiEventManager.trigger(AphosEvent.CAMERA_SHAKE, new Screenshake(1.5f, false, VisualEnums.ScreenShakeTemplate.MEDIUM));
                /*

                 */
            }
            case lane_hit -> {
               GdxActions.addDisplace(view.getPortrait(), entity.isLeftSide() ? DIRECTION.UP_LEFT : DIRECTION.UP_RIGHT,
                        ArrayMaster.floatOrElse(namedArgs.get("intensity"), 50f));
                for (int i = 0; i < 5; i++) {
                    //add 'spray' copies
                    spritePath = SpriteFinder.getSpritePath(type, entity, outcome, namedArgs); //randomize
                    animDrawer.add(GdxMaster.offset(pos, RNG.integer(-10, 10), RNG.integer(-10, 10)), spritePath, type);
                }
                animation.addOnFinish(() ->
                        GuiEventManager.trigger(AphosEvent.CAMERA_SHAKE, new Screenshake(0.5f, true, VisualEnums.ScreenShakeTemplate.MEDIUM)));
            }
            case lane_atk -> {
                GdxActions.addDisplace(Aphos.view.getPortrait(), entity.isLeftSide() ? DIRECTION.UP_LEFT : DIRECTION.UP_RIGHT,
                        ArrayMaster.floatOrElse(namedArgs.get("intensity"), 50f));
                animation.addOnFinish(() ->
                        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ATK_ANIMATION_FINISHED, type));
            }
            case lane_death -> {
                animation.addOnFinish(() -> screenAnim(view));
                animation.addOnFinish(() -> view.kill());
            }


            case hero_atk -> {
            }
            case hero_hit -> {
                GdxActions.addDisplace(view.getPortrait(), entity.isLeftSide() ? DIRECTION.DOWN_RIGHT : DIRECTION.DOWN_LEFT,
                        ArrayMaster.floatOrElse(namedArgs.get("intensity"), 30f));
            }
            case hero_death -> {
            }
        }
        //proper remove - ?  THIS IS UPSIDE DOWN - we don't depend on DeathAnim to do kill()!


    }

    private static void moveUnit(Unit unit, LanePos pos) {
        UnitView view = LanesField.getView(unit.getPrevPos());
        UnitView newView = LanesField.getView(pos);
        moveUnit(view, newView, unit);
    }

    /*
    what happens is that we fade out on prev and fade in on cur!
    All we need here is some VFX
    Maybe later we can have some interesting shader-based effects like ... pull image out
     */
    public static void moveUnit(UnitView prev, UnitView cur, Unit unit) {
        prev.setDto(new UnitDto(null));
        cur.setDto(new UnitDto(unit));

    }

    public static void moveHero(HeroView view, HeroPos prevPos, HeroPos pos, int moveType) {
        MoveToAction moveToAction = new MoveToAction();

        switch (moveType) {
            case REVERSE_JUMP -> {
                moveToAction.setInterpolation(Interpolation.swingOut);
                moveToAction.setDuration(3);
            }
            case MID_JUMP -> {
                moveToAction.setInterpolation(Interpolation.circle);
                moveToAction.setDuration(2);
            }
            case NORMAL -> {
                boolean jump = Math.abs(pos.getCell() - prevPos.getCell()) >= 2;
                moveToAction.setInterpolation(jump ? Interpolation.sineOut : Interpolation.smooth);
                moveToAction.setDuration(1);
            }
        }

        //TODO for real jump we'll need separate actions for Y and X !
        float x = ViewManager.getHeroX(pos);
        float y = HeroZone.HEIGHT;
        y = y - ViewManager.getHeroYInverse(pos);
        if (pos.isLeftSide())
            System.out.printf("Moved to %2.0f on left side (%2.0f:%2.0f)\n", (float) pos.getCell(), x, y);
        else
            System.out.printf("Moved to %2.0f on right side (%2.0f:%2.0f)\n", (float) pos.getCell(), x, y);
        moveToAction.setPosition(x, y);
        moveToAction.setTarget(view);
        view.addAction(moveToAction);

    }

    public static void sideJump(boolean mid, HeroView view, HeroPos prev, HeroPos pos) {
        moveHero(view, prev, pos, mid ? MID_JUMP : REVERSE_JUMP);
    }

    public static void setAnimDrawer(AnimDrawer animDrawer) {
        ActionAnims.animDrawer = animDrawer;
    }

    //useless?
//    public static void moveUnit(UnitView view, LanePos prevPos, LanePos pos) {
//        if (prevPos.lane != pos.lane) {
////            jumpLane()
//        }
//        float scale = ViewManager.getScale(pos.cell);
//        float dur = 1;
//        ScaleToAction scaleAction = ActionMasterGdx.getScaleAction(scale, dur);
//        scaleAction.setInterpolation(Interpolation.smooth);
//        scaleAction.setTarget(view);
//        view.addAction(scaleAction);
//        MoveToAction moveToAction = ActionMasterGdx.getMoveToAction(ViewManager.getX(pos), ViewManager.getYInverse(pos), dur);
//        scaleAction.setInterpolation(Interpolation.smooth);
//        scaleAction.setTarget(view);
//        view.addAction(scaleAction);
//    }
    //TODO
    private static void screenAnim(FieldView view) {
        //            Action screenAction= new TemporalAction(0.5f, Interpolation.pow4Out) {
//                @Override
//                protected void update(float percent) {
//                    if (percent <0.5f)
//                        view.setScreenOverlay(percent*2);
//                    else
//                        view.setScreenOverlay(1-percent*2);
//                }
//            };
//            view.addAction(screenAction);
    }
}
