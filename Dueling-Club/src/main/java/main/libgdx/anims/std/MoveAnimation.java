package main.libgdx.anims.std;

import com.badlogic.gdx.graphics.Texture;
import main.ability.effects.SelfMoveEffect;
import main.entity.Entity;
import main.game.battlefield.Coordinates;
import main.libgdx.anims.AnimData;
import main.libgdx.texture.TextureManager;
import main.system.GraphicEvent;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.ListMaster;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/14/2017.
 */
public class MoveAnimation extends ActionAnim {

    public MoveAnimation(Entity active, AnimData params) {
        super(active, params);
    }

    @Override
    protected Coordinates getOriginCoordinates() {
        if (!ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
        SelfMoveEffect.class)))
           return ref.getTargetObj().getCoordinates();
        return super.getOriginCoordinates();
    }

    @Override
    protected Coordinates getDestinationCoordinates() {

        if (ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
         SelfMoveEffect.class))) {
            SelfMoveEffect e= (SelfMoveEffect) EffectMaster.getEffectsOfClass(getActive(),
             SelfMoveEffect.class).get(0); //TODO could be 2+?
//            if (e.getDirection() != null) {
//                return ref.getSourceObj().getCoordinates().getAdjacentCoordinate
//                 (DirectionMaster.getDirectionByFacing(FacingMaster.getFacing(ref.getSourceObj()),
//                  e.getDirection()));
//            }
//            if (e.getTemplate() != null)
//                return
//             DC_Game.game.getMovementManager().getTemplateMoveCoordinate(e.getTemplate(),
//              FacingMaster.getFacing(ref.getSourceObj()), ref.getSourceObj(), ref);

      return e.getDestination();


        }

            return super.getDestinationCoordinates();
    }
//getSpeed()
    //getTrajectory()

    @Override
    public List<GraphicEvent> getEventsOnStart() {
        return new LinkedList<>(Arrays.asList(new GraphicEvent[]{
         GraphicEvent.DESTROY_UNIT_MODEL
        }));
    }

    @Override
    public List<GraphicEvent> getEventsOnFinish() {
        return new LinkedList<>(Arrays.asList(new GraphicEvent[]{
         GraphicEvent.UNIT_MOVED
        }));
    }

    @Override
    protected Texture getTexture() {
//        if (getActive().getActionGroup()
//            if (getActive().getName().equals("Move")) {
        if (ListMaster.isNotEmpty(EffectMaster.getEffectsOfClass(getActive(),
         SelfMoveEffect.class))) {
            return TextureManager.getOrCreate(getRef().getSourceObj().getImagePath());
        }
        return TextureManager.getOrCreate(getRef().getTargetObj().getImagePath());
    }
}
