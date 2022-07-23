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
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.actions.AfterActionSmart;
import libgdx.anims.fullscreen.Screenshake;
import libgdx.bf.generic.FadeImageContainer;
import logic.entity.Entity;
import logic.entity.Unit;
import logic.functions.combat.CombatLogic.ATK_OUTCOME;
import logic.lane.HeroPos;
import main.system.GuiEventManager;
import content.AphosEvent;
import main.system.threading.WaitMaster;

import java.util.List;

import static logic.functions.combat.HeroMoveLogic.*;

public class ActionAnims {
    static {
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_ATK, p -> atk((List) p.get()));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_HIT, p -> hit((List) p.get()));
        GuiEventManager.bind(AphosEvent.DUMMY_ANIM_DEATH, p -> death((List) p.get()));
    }

    public enum DUMMY_ANIM_TYPE {
        hit, atk, death
    }

    private static AnimDrawer animDrawer;

    public static void atk(List params) {
        animOn((Entity) params.get(0), DUMMY_ANIM_TYPE.atk, (ATK_OUTCOME) params.get(1), params.get(2));
    }

    public static void death(List params) {
        animOn((Entity) params.get(0), DUMMY_ANIM_TYPE.death, (ATK_OUTCOME) params.get(1));
    }

    public static void hit(List params) {
        animOn((Entity) params.get(0), DUMMY_ANIM_TYPE.hit, (ATK_OUTCOME) params.get(1));
    }

    public static void animOn(Entity entity, DUMMY_ANIM_TYPE type, ATK_OUTCOME outcome, Object... args) {
        //int intensity - for shake, ..
        //use hit anims to 'spray'


        FieldView view = ViewManager.getView(entity);
        Vector2 pos = ViewManager.getAbsPos(view);
//        viewMotion; shake, flash, scale, x to y,
//        screenShake;

        if (type == DUMMY_ANIM_TYPE.hit) {
//            Vector2 start=pos;
            FadeImageContainer portrait = view.getPortrait();
            Vector2 dest = new Vector2();
            dest = entity.isLeftSide() ? dest.add(-50, 30) : dest.add(50, 30);
            MoveToAction moveToAction = ActionMasterGdx.getMoveToAction(dest.x, dest.y, 0.2f);
            ActionMasterGdx.addAction(portrait, moveToAction);

            MoveToAction moveBack = ActionMasterGdx.getCopy(moveToAction, MoveToAction.class);
            moveBack.setPosition(0, 0);
            AfterActionSmart after = new AfterActionSmart();
            after.setAction(moveBack);
            ActionMasterGdx.addAction(portrait, after);
            after.setAction(moveBack);


        }
        String spritePath = SpriteFinder.getSpritePath(type, entity, outcome, args);
        SpriteAnim animation = animDrawer.add(pos, spritePath, type);
        if (type == DUMMY_ANIM_TYPE.hit) {
            //add 'spray' copies
            for (int i = 0; i < 5; i++) {
                spritePath = SpriteFinder.getSpritePath(type, entity, outcome, args); //randomize
                animDrawer.add(GdxMaster.offset(pos, RNG.integer(-10, 10), RNG.integer(-10, 10)), spritePath, type);
            }
        }
        //proper remove - ?  THIS IS UPSIDE DOWN - we don't depend on DeathAnim to do kill()!
        if (type == DUMMY_ANIM_TYPE.death) {
            animation.addOnFinish(() -> screenAnim(view));
            animation.addOnFinish(() -> view.kill());
        }

        if (type == DUMMY_ANIM_TYPE.hit)
            animation.addOnFinish(() ->
                    GuiEventManager.trigger(AphosEvent.CAMERA_SHAKE, new Screenshake(0.5f, true, VisualEnums.ScreenShakeTemplate.MEDIUM)));

        if (type == DUMMY_ANIM_TYPE.atk)
            animation.addOnFinish(() ->
                    WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ATK_ANIMATION_FINISHED, type));
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
