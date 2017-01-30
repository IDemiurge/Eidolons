package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import main.ability.effects.MoveEffect;
import main.entity.Entity;
import main.game.battlefield.Coordinates;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.system.GuiEventType;
import main.system.ai.logic.target.EffectMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static main.system.GuiEventType.DESTROY_UNIT_MODEL;
import static main.system.GuiEventType.UNIT_MOVED;

/**
 * Created by JustMe on 1/14/2017.
 */
public class MoveAnimation extends ActionAnim {


    private MoveToAction action;

    public MoveAnimation(Entity active, AnimData params) {
        super(active, params);
    }

    @Override
    protected void initDuration() {
        super.initDuration();
        duration = 0.1f;
    }

    protected Action getAction() {
        if (action == null)
            action = new MoveToAction() {
                @Override
                protected void begin() {
                    super.begin();
                    main.system.auxiliary.LogMaster.log(1, this + " begins! "
                            + this.getX() + " " + this.getY());
                }

                @Override
                protected void update(float percent) {
                    super.update(percent);
                    main.system.auxiliary.LogMaster.log(1, this + ": " + getActor().getX() + " " + getActor().getY());
                }

                @Override
                public String toString() {
                    return super.toString() + " on " + getActor() + " to: " + this.getX() + " " + this.getY();
                }
            };
        action.setPosition(getDestination().x, getDestination().y);
        action.setDuration(1);
        return action;
    }

    @Override
    public List<SpriteAnimation> getSprites() {
        return new LinkedList<>();
    }

    @Override
    public List<ParticleEmitter> getEmitterList() {
        return new LinkedList<>();
    }


    @Override
    public void start() {
        super.start();
//        DC_HeroObj unit = (DC_HeroObj) getRef().getSourceObj();
//        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
//         MoveEffect.class)))
//            unit = (DC_HeroObj) getRef().getTargetObj();
//        BaseView actor = GameScreen.getInstance().getGridPanel().getUnitMap()
//         .get(unit);
//        GameScreen.getInstance().getGridStage().addActor(actor);
//        actor.setPosition(getX(), getY());
//        main.system.auxiliary.LogMaster.log(1, " ");
//        actor.addAction(getAction());
//        action.setTarget(actor);


    }


    @Override
    public boolean draw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    protected Coordinates getOriginCoordinates() {
//        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
//         MoveEffect.class))) // for teleports, telekinesis etc
//            return getRef().getTargetObj().getCoordinates();
//        return super.getOriginCoordinates();
        MoveEffect e = (MoveEffect) EffectMaster.getFirstEffectOfClass(getActive(),
                MoveEffect.class);
        return e.getOrigin();

    }

    @Override
    protected Coordinates getDestinationCoordinates() {


        MoveEffect e = (MoveEffect) EffectMaster.getFirstEffectOfClass(getActive(),
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

    @Override
    public List<GuiEventType> getEventsOnStart() {
        return Arrays.asList(DESTROY_UNIT_MODEL);
    }

    @Override
    public List<GuiEventType> getEventsOnFinish() {
        return Arrays.asList(UNIT_MOVED);
    }

    @Override
    protected Texture getTexture() {
//        if (ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
//         MoveEffect.class))) {
//            return TextureManager.getOrCreate(getRef().getSourceObj().getImagePath());
//        }
//        return TextureManager.getOrCreate(getRef().getTargetObj().getImagePath());
        return null;
    }
}
