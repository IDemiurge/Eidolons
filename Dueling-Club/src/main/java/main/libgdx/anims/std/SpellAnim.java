package main.libgdx.anims.std;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.libgdx.GameScreen;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.particles.EmitterActor;
import main.system.Producer;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.secondary.GeometryMaster;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        adjustAngle();
    }

    private void adjustAngle() {
        emitterList.forEach(e->{
            if ( e.getTarget()!=null ){
            float offset =1* (float) GeometryMaster.getAngle(
             getActive().getOwnerObj().getCoordinates(),
//             getRef().getTargetObj().getCoordinates()
            e.getTarget());
           offset+=90;
            main.system.auxiliary.LogMaster.log(1,getActive()+" is offset by " +offset);
            e.getEffect().offsetAngle(offset);}
        });
    }


    public void applyTemplate() {
        if (template != null){         applyTemplateAngles();return;}
        Effect effect = EffectMaster.getFirstEffectOfClass(getActive(), SpecialTargetingEffect.class);
        if (effect != null) {
            SpecialTargetingEffect targetEffect = (SpecialTargetingEffect) effect;
            applyTemplateForCoordinates(targetEffect.getCoordinates());
            return;
        }
        if (getRef().getGroup() != null) {
            Set<Coordinates> set = new LinkedHashSet();
            getRef().getGroup().getObjects().forEach(o -> set.add(o.getCoordinates()));
            applyTemplateForCoordinates(set);
            return;
        }



       

    }



    public void applyTemplateForCoordinates(Set<Coordinates> coordinates) {
        List<EmitterActor> list = new LinkedList<>();
        coordinates.forEach(c ->
         {
             for (EmitterActor e : emitterList) {
                 if (e.isGenerated()) continue;
//                 double angle = GeometryMaster.getAngle(c,
//                  getActive().getOwnerObj().getCoordinates());
                 //relative vs absolute
                 EmitterActor actor  = new EmitterActor(e.path);
//                 actor.getEffect().offsetAngle((float) angle);

//                 try {
//                     actor = EmitterPresetMaster.getInstance().
//                      getModifiedEmitter(e.path, angle, EMITTER_VALUE_GROUP.Angle);
//                 } catch (Exception e1) {
//                     e1.printStackTrace();
//                     actor = new EmitterActor(e.path);
//                 }


                 createAndAddEmitterActions(actor, c);
actor.debug();
                 list.add(actor);
                 actor.setTarget(c);
                 actor.setPosition(getX(), getY());
                 actor.setAttached(false);
                 actor.setGenerated(true);
                 GameScreen.getInstance().getAnimsStage().addActor(actor);
             }
         }
        );

        list.forEach(a -> emitterList.add(a));

    }
    private void createAndAddEmitterActions(EmitterActor actor, Coordinates c) {
        MoveToAction action = ActorMaster.getMoveToAction(c, actor, pixelsPerSecond);
        if (action.getDuration() > this.duration) this.duration = action.getDuration();
//        ActorMaster.addRemoveAfter(actor);
        actor.getEffect().setDuration((int) action.getDuration());
    }


    private void applyTemplateAngles() {
        int max = template.getNumberOfEmitters(getActive());
        List<EmitterActor> list = new LinkedList<>();
        for (EmitterActor e : emitterList) {
            if (e.isGenerated()) continue;
            //check emitter multiplication on
            int angle = 0;
            for (int i = 1; i <= max; i++) {
                EmitterActor actor = new EmitterActor(e.path);
                createAndAddEmitterActions(actor, angle, template);
                angle += 360 / max;
                list.add(actor);
                actor.setPosition(getX(), getY());
                actor.setAttached(false);
                actor.setGenerated(true);
                ActorMaster.addRemoveAfter(actor);
                GameScreen.getInstance().getAnimsStage().addActor(actor);
            }
        }
        list.forEach(a -> emitterList.add(a));
    }
    private Action createAndAddEmitterActions(EmitterActor actor, int angle, SPELL_ANIMS template) {
        Action action = new SequenceAction();
        actor.addAction(action);
        action.setTarget(actor);
        int range = getActive().getRange();
//        if ()
        range = getActive().getIntParam(G_PARAMS.RADIUS);

        switch (template) {
            case NOVA:
                return addRangeAndAngle(angle, range, actor);

        }

        return action;
    }


    private Action addRangeAndAngle(int angle, int range, EmitterActor actor) {
        MoveByAction action = new MoveByAction();
        float x = (float) (132 * Math.sin(angle) * range);
        float y = (float) (132 * Math.cos(angle) * range);
        action.setAmount(x, y);
        actor.setRotation(-angle);
        actor.addAction(action);
        action.setTarget(actor);
        Float duration = (float) (Math.sqrt(x * x + y * y) / pixelsPerSecond);
        action.setDuration(
         duration);
        if (duration > this.duration) this.duration = duration;

        ActorMaster.addRemoveAfter(actor);

        //TRY ALPHA/ROTATE ACTION
        return action;
    }

    public enum ZONE_ANIM_MODS {
        SWIRL,
        RETRACT,

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
