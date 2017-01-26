package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.gwt.user.client.ui.Grid;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.libgdx.GameScreen;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.particles.ParticleEmitter;
import main.libgdx.bf.GridPanel;
import main.system.Producer;

import java.util.LinkedList;
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

    @Override
    public void start() {
        super.start();
        applyTemplate();
    }

    public void applyTemplate() {
        if (template==null )return ;
        int max = template.getNumberOfEmitters(getActive());
        List<ParticleEmitter> list = new LinkedList<>();
        for (ParticleEmitter e : emitterList) {
            //check emitter multiplication on
            for (int i = 1; i <= max; i++) {
                ParticleEmitter actor = new ParticleEmitter(e.path);
                createAndAddEmitterActions(actor, i, template);
                list.add(actor);
                actor.setPosition(getX(), getY());
//                actor.setGenerated(true);
                GameScreen.getInstance().getAnimsStage().addActor(actor);
            }
        }
//        list.forEach(a-> emitterList.add(a)); they have their own movements!

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
        float x = (float)( 132*Math.sin(angle) * range);
        float y = (float) ( 132*Math.cos(angle) * range);
        action.setAmount(x, y);
        actor.setRotation(-angle);
        actor.addAction(action);
        action.setTarget(actor);
        Float duration = (float) (Math.sqrt(x * x + y * y) / pixelsPerSecond);
        action.setDuration(
         duration);
        if (duration > this.duration) this.duration = duration;
        return action;
    }

    public enum SPELL_ANIMS {
        RAY(activeObj -> 1),
        BLAST(active -> (active.getIntParam(G_PARAMS.RADIUS) == 0) ? 1 :
         active.getRef().getTargetObj().getCoordinates().
          getAdjacentCoordinates().size()),
        SPRAY(active -> {
            List<Coordinates> list = active.getOwnerObj().getCoordinates().
             getAdjacentCoordinates();
            list.removeIf(coordinates ->
             FacingMaster.getSingleFacing(active.getOwnerObj().getFacing(),
              active.getOwnerObj().getCoordinates(), coordinates) != FACING_SINGLE.IN_FRONT);
            return list.size();
        }
        ),
        WAVE(active -> active.getIntParam(G_PARAMS.RADIUS)),
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
