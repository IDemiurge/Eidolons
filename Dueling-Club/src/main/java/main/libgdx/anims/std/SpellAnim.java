package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.particles.ParticleEmitter;
import main.system.Producer;

import java.util.List;

/**
 * Created by JustMe on 1/24/2017.
 */
public class SpellAnim extends ActionAnim {

    SPELL_ANIMS template;

    public SpellAnim(Entity active, AnimData params, SPELL_ANIMS template) {
        super(active, params);
        this.template = template;
    }

    public void applyTemplate() {
        int max = template.getNumberOfEmitters(getActive());
        for (ParticleEmitter e : emitterList) {
            //check emitter multiplication on
            for (int i = 0; i < max; i++) {
                ParticleEmitter actor = new ParticleEmitter(e.getSfx());
                createAndAddEmitterActions(actor, i, template);
            }
        }

    }


    private Action createAndAddEmitterActions(ParticleEmitter actor, int i, SPELL_ANIMS template) {
        Action action = new SequenceAction();
        actor.addAction(action);
        action.setTarget(actor);
        int range = getActive().getRange();
//        if ()
        range = getActive().getIntParam(G_PARAMS.RADIUS);
        switch (template) {
            case NOVA:
                return addRangeAndAngle(360 / i, range, actor);

        }

        return action;
    }

    private Action addRangeAndAngle(int angle, int range, ParticleEmitter actor) {
        MoveByAction action = new MoveByAction();
        float x = (float) (Math.sin(angle) * range);
        float y = (float) (Math.cos(angle) * range);
        action.setAmount(x, y);
        actor.setRotation(-angle);
        actor.addAction(action);
        action.setTarget(actor);
        return action;
    }

    public enum SPELL_ANIMS {
        RAY(activeObj -> 1),
        BLAST(active ->(active.getIntParam(G_PARAMS.RADIUS)==0) ?1 :
         active.getRef().getTargetObj().getCoordinates().
         getAdjacentCoordinates().size()),
        SPRAY(active -> {
            List<Coordinates> list = active.getOwnerObj().getCoordinates().
             getAdjacentCoordinates();
            list.removeIf(coordinates ->
                  FacingMaster.getSingleFacing(active.getOwnerObj().getFacing(),
                 active.getOwnerObj().getCoordinates(), coordinates) != FACING_SINGLE.IN_FRONT );
            return list.size();
        }
        ),
        WAVE(active-> active.getIntParam(G_PARAMS.RADIUS)),
        RING(activeObj -> 8),
        NOVA(activeObj -> activeObj.getOwnerObj().getCoordinates().getAdjacentCoordinates().size()),;

        private Producer<DC_ActiveObj, Integer> numberOfEmitters;

        //emitter placement templates
        SPELL_ANIMS() {

        }

        SPELL_ANIMS(Producer<DC_ActiveObj, Integer> numberOfEmittersSupplier) {
            numberOfEmitters = numberOfEmittersSupplier;
        }

        public int getNumberOfEmitters(DC_ActiveObj active) {
            return numberOfEmitters.produce(active);
        }


    }

}
