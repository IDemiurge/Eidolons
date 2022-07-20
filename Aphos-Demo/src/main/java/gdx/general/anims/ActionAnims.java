package gdx.general.anims;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import gdx.views.FieldView;
import gdx.views.HeroView;
import gdx.views.UnitView;
import gdx.visuals.front.HeroZone;
import gdx.visuals.front.ViewManager;
import libgdx.anims.actions.ActionMasterGdx;
import logic.content.AUnitEnums;
import logic.entity.Entity;
import logic.lane.HeroPos;
import logic.lane.LanePos;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

import static logic.functions.MoveLogic.*;

public class ActionAnims {
    static {
        GuiEventManager.bind(GuiEventType.DUMMY_ANIM_ATK, p -> atk((Entity) p.get()));
        GuiEventManager.bind(GuiEventType.DUMMY_ANIM_HIT, p -> hit((Entity) p.get()));
        GuiEventManager.bind(GuiEventType.DUMMY_ANIM_DEATH, p -> death((Entity) p.get()));
    }

    public enum DUMMY_ANIM_TYPE {
        hit, atk, death
    }

    private static AnimDrawer animDrawer;

    public static void atk(Entity entity) {
        animOn(entity, DUMMY_ANIM_TYPE.atk);
    }

    public static void death(Entity entity) {
        animOn(entity, DUMMY_ANIM_TYPE.death);
    }

    public static void hit(Entity entity) {
        animOn(entity, DUMMY_ANIM_TYPE.hit);
    }

    public static void animOn(Entity entity, DUMMY_ANIM_TYPE type) {
        FieldView view = ViewManager.getView(entity);
        Vector2 pos = ViewManager.getAbsPos(view);
//        viewMotion; shake, flash, scale, x to y,
//        screenShake;
        String spritePath = getSpritePath(type, entity);
        SpriteAnim animation = animDrawer.add(pos, spritePath, type);
        //proper remove - ?
        animation.setOnFinish( ()-> view.fadeOut());
    }


    //TODO
    private static String getSpritePath(DUMMY_ANIM_TYPE anim, Entity entity) {
        switch (anim) {
            case hit -> {
//                Object type = entity.getValueMap().get(AUnitEnums.BODY);
                String base = "sprite/hit/";
//                return base+"blood/shower.txt";
                return "sprite\\hit\\blood\\shower.txt";
//                if (type==null)
//                switch (type.toString()) {
//
//                }
//                return RandomWizard.getRandomFrom(base + "", base + "");
                //blood / bone / ..
            }
            case atk -> {
                String base = "sprite/atk/";
                base += "blade\\scimitar\\slash.txt";
                return  "sprite\\atk\\blade\\scimitar\\slash\\slash.txt";
            }
            case death -> {
            }
        }
        return null;
    }

    public static void moveUnit(UnitView view, LanePos prevPos, LanePos pos) {
        if (prevPos.lane != pos.lane) {
//            jumpLane()
        }
        float scale = ViewManager.getScale(pos.cell);
        float dur = 1;
        ScaleToAction scaleAction = ActionMasterGdx.getScaleAction(scale, dur);
        scaleAction.setInterpolation(Interpolation.smooth);
        scaleAction.setTarget(view);
        view.addAction(scaleAction);

        MoveToAction moveToAction = ActionMasterGdx.getMoveToAction(ViewManager.getX(pos), ViewManager.getYInverse(pos), dur);
        scaleAction.setInterpolation(Interpolation.smooth);
        scaleAction.setTarget(view);
        view.addAction(scaleAction);

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
}
