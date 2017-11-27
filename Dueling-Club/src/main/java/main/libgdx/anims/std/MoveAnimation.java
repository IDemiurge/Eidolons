package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import main.ability.effects.oneshot.move.MoveEffect;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.bf.Coordinates;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.actions.MoveByActionLimited;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridMaster;
import main.libgdx.screens.DungeonScreen;
import main.system.EventCallbackParam;
import main.system.GuiEventType;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.data.ListMaster;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static main.system.GuiEventType.UNIT_MOVED;

/**
 * Created by JustMe on 1/14/2017.
 */
public class MoveAnimation extends ActionAnim {


    static boolean on = true;
    private MoveByActionLimited action;
    private Unit unit;

    public MoveAnimation(Entity active, AnimData params) {
        super(active, params);
        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
         MoveEffect.class))) // for teleports, telekinesis etc
        {
            unit = (Unit) getRef().getTargetObj();
        }
        unit = (Unit) getRef().getSourceObj();
    }

    public static boolean isOn() {
        return on;
    }


    protected MoveByActionLimited getAction() {
//        new Pool.Poolable()
        if (action == null) {
            action = new MoveByActionLimited();
        } else {
            action.reset();
        }
        setDuration(1);
        float x = getDestination().x-getOrigin().x;
        if (x>160){
            x= GridConst.CELL_W;
        }
        float y =getDestination().y-getOrigin().y;
        if (y>160){
            y= GridConst.CELL_H;
        }
        action.setAmount(x
         ,
         y);

        action.setDuration(getDuration());

        return action;
    }


    public void initPosition() {
        origin = GridMaster
         .getVectorForCoordinate(getOriginCoordinates(), false, true);
        main.system.auxiliary.log.LogMaster.log(1,
         this + " origin: " + origin);
        destination = GridMaster
         .getVectorForCoordinate(getDestinationCoordinates(), false, true);
        main.system.auxiliary.log.LogMaster.log(1,
         this + " destination: " + destination);

        defaultPosition = getDefaultPosition();
    }
    @Override
    public List<SpriteAnimation> getSprites() {
        return new ArrayList<>();
    }

    @Override
    public List<EmitterActor> getEmitterList() {
        return new ArrayList<>();
    }


    public void playSound() {
        DC_SoundMaster.playMoveSound(getActive().getOwnerObj());
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Unit unit = (Unit) getRef().getSourceObj();
        if (!ListMaster.isNotEmpty(EffectFinder.getEffectsOfClass(getActive(),
         MoveEffect.class)))
            unit = (Unit) getRef().getTargetObj();
        BaseView actor = DungeonScreen.getInstance().getGridPanel().getUnitMap()
         .get(unit);
        DungeonScreen.getInstance().getGridPanel().detachUnitView(unit);

//        DungeonScreen.getInstance().getGridStage().addActor(actor);
//        actor.setPosition(getX(), getY());
        getAction().setStartPointY(actor.getY());
        getAction().setStartPointX(actor.getX());
        actor.addAction(getAction());
        action.setTarget(actor);


    }


    @Override
    public boolean draw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    public Coordinates getOriginCoordinates() {
//        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
//         MoveEffect.class))) // for teleports, telekinesis etc
//            return getRef().getTargetObj().getCoordinates();
//        return super.getOriginCoordinates();
        MoveEffect e = (MoveEffect) EffectFinder.getFirstEffectOfClass(getActive(),
         MoveEffect.class);
        return e.getOrigin();

    }

    @Override
    public Coordinates getDestinationCoordinates() {


        MoveEffect e = (MoveEffect) EffectFinder.getFirstEffectOfClass(getActive(),
         MoveEffect.class); //TODO could be 2+?
        return e.getDestination();
//            if (e.getDirection() != null) {
//                return ref.getSourceObj().getCoordinates().getAdjacentCoordinate
//                 (DirectionMaster.getDirectionByFacing(FacingMaster.getFacing(ref.getSourceObj()),
//                  e.getDirection()));
//            }
//            if (e.getTemplate() != null)
//                return
//             DC_Game.game.getMovementManager().getTemplateMoveCoordinate(e.getTemplate(),
//              FacingMaster.getFacing(ref.getSourceObj()), ref.getSourceObj(), ref);
//            return super.getDestinationCoordinates();
    }
//getSpeed()
    //getTrajectory()

//    @Override
//    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnStart() {
//        return Arrays.asList(new ImmutablePair<>(UNIT_STARTS_MOVING, new EventCallbackParam(unit)));
//    }

    public static void setOn(boolean on) {
        MoveAnimation.on = on;
    }
    @Override
    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return Arrays.asList(new ImmutablePair<>(UNIT_MOVED, new EventCallbackParam(unit)));
    }

    @Override
    protected Texture getTexture() {
//        if (ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
//         MoveEffect.class))) {
//            return TextureCache.getOrCreate(getRef().getSourceObj().getImagePath());
//        }
//        return TextureCache.getOrCreate(getRef().getTargetObj().getImagePath());
        return null;
    }
}
