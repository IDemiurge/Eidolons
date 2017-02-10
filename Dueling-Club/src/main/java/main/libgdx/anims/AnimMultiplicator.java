package main.libgdx.anims;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import main.ability.effects.Effect;
import main.ability.effects.oneshot.common.SpecialTargetingEffect;
import main.content.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.libgdx.GameScreen;
import main.libgdx.anims.particles.EmitterActor;
import main.libgdx.anims.particles.EmitterPools;
import main.libgdx.anims.std.SpellAnim;
import main.libgdx.anims.std.SpellAnim.SPELL_ANIMS;
import main.libgdx.anims.std.SpellAnim.ZONE_ANIM_MODS;
import main.libgdx.bf.GridMaster;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.secondary.GeometryMaster;

import java.util.*;

/**
 * Created by JustMe on 1/29/2017.
 */
public class AnimMultiplicator implements Runnable {
    static final MULTIPLICATION_METHOD defaultMultiplicationMethod = MULTIPLICATION_METHOD.COORDINATE;
    MULTIPLICATION_METHOD multiplicationMethod = defaultMultiplicationMethod;
    private Anim anim;
    private SpellAnim spellAnim;
    //parallel, in different threads!
    private List<EmitterActor> emitterList;
    private float duration;
    private Entity active;
    private SPELL_ANIMS template;

    public AnimMultiplicator(Anim anim) {
        this.anim = anim;
    }

    public static AnimMultiplicator getInstance(Anim anim) {
        return new AnimMultiplicator(anim);
    }

    public static void checkMultiplication(Anim anim) {
        if (!isMultiplied(anim)) {
            return;
        }
        getInstance(anim).run();
//        new Thread(getInstance(anim)).start();
// concurrent modif to emitter list??
        // just didn't want to run this on gdx thread...
    }

    private static boolean isMultiplied(Anim anim) {
        if (anim instanceof SpellAnim) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        emitterList = anim.getEmitterList();
        duration = anim.getDuration();
        active = anim.getActive();
        if (anim instanceof SpellAnim) {
            spellAnim = (SpellAnim) anim;
            template = spellAnim.getTemplate();
        }
        multiply(anim);
    }

    private void multiply(Anim anim) {
        applyTemplate();
        adjustAngle();
    }

    private void adjustAngle() {
        emitterList.forEach(e -> {
            if (e.getTarget() != null) {
                float offset = 1 * (float) GeometryMaster.getAngle(
                 getActive().getOwnerObj().getCoordinates(),
//             getRef().getTargetObj().getCoordinates()
                 e.getTarget());
                offset += 90;
                main.system.auxiliary.LogMaster.log(1, getActive() + " is offset by " + offset);
                e.getEffect().offsetAngle(offset);
            }
        });
    }

    public void applyTemplate() {

        Effect effect = EffectMaster.getFirstEffectOfClass(getActive(), SpecialTargetingEffect.class);
        Set<Coordinates> coordinates = null;
        if (effect != null) {
            SpecialTargetingEffect targetEffect = (SpecialTargetingEffect) effect;
            coordinates = targetEffect.getCoordinates();
        }
        if (coordinates == null) {
            if (getRef().getGroup() != null) {
                Set<Coordinates> set = new LinkedHashSet();
                getRef().getGroup().getObjects().forEach(o -> set.add(o.getCoordinates()));
                coordinates = set;
            }
        }
        if (multiplicationMethod == MULTIPLICATION_METHOD.ANGLE) {
            if (template != null) {
                applyTemplateAngles(coordinates);
                return;
            }
        }
        if (coordinates != null) {
            applyMultiplicationForCoordinates(coordinates);
        }

        if (template != null) {
            if (template.isRemoveBaseEmitters()) {
                anim.getEmitterList().removeIf(e -> !e.isGenerated());
            }
        }
    }

    public void applyMultiplicationForCoordinates(Set<Coordinates> coordinates) {
        List<EmitterActor> list = new LinkedList<>();

        filterCoordinates(template, coordinates).forEach(c ->
         {
             for (EmitterActor e : emitterList) {
                 if (e.isGenerated()) {
                     continue;
                 }
                 EmitterActor actor = multiplicateEmitter(null, c, e);
                 list.add(actor);
             }
         }
        );

        list.forEach(a -> emitterList.add(a));

    }

    private Collection<Coordinates> filterCoordinates(SPELL_ANIMS template, Set<Coordinates> coordinates) {

        if (template != null) {
            FACING_DIRECTION facing = getActive().getOwnerObj().getFacing();
            List<Coordinates> filtered = new LinkedList<>(coordinates);
            Coordinates farthest = CoordinatesMaster.getFarmostCoordinateInDirection
             (facing.getDirection(),
              new LinkedList<>(coordinates), null);
            switch (template) {
//                template.getNumberOfEmitters(getActive())
                case RAY:
                    anim.setForcedDestinationForAll(farthest);

                    return Arrays.asList(new Coordinates[]{
                     farthest
                    });

                case BLAST:
                    break;
                case SPRAY: {
                    boolean xOrY = !facing.isVertical();
                    filtered.removeIf(c ->
                      farthest.getXorY(xOrY) != c.getXorY(xOrY)
//                     PositionMaster.getDistance(farthest, anim.getOriginCoordinates()) 
//                         > PositionMaster.getDistance(c, anim.getOriginCoordinates())
                    );
                    while (filtered.size() <
                     template.getNumberOfEmitters(getActive())) {
                        List<Coordinates> list = new LinkedList<>(coordinates);
                        list.removeAll(filtered);
                        list.removeIf(c ->
                         farthest.getXorY(!xOrY) == c.getXorY(!xOrY)
                        );
                        Coordinates c = CoordinatesMaster.getFarmostCoordinateInDirection(
                         facing.getDirection(),
                         list, null);
                        filtered.add(c);
                    }

                    return filtered;
                }
                case WAVE:
                    break;
                case RING:
                    break;
                case NOVA:
                    break;
            }
        }
        return coordinates;
    }

    private EmitterActor multiplicateEmitter(Integer angle, Coordinates c, EmitterActor e) {
        EmitterActor actor = EmitterPools.getEmitterActor(e.path);
        actor.setPosition(getX(), getY());
        actor.setAttached(false);
        actor.setGenerated(true);
        ActorMaster.addRemoveAfter(actor);
        GameScreen.getInstance().getAnimsStage().addActor(actor);

        if (angle != null) {
            createAndAddEmitterActions(actor, angle, template);
        } else {
            createAndAddEmitterActions(actor, c);
        }

        return actor;
    }

    private void applyTemplateAngles(Set<Coordinates> coordinates) {
        int max = template.getNumberOfEmitters(getActive());
        // 1 for ray, but we know coordinates precisely
        List<EmitterActor> list = new LinkedList<>();

        for (EmitterActor e : emitterList) {
            int angle = 0;
            for (Coordinates coordinate : coordinates) {

                if (e.isGenerated()) {
                    continue;
                }
                EmitterActor actor = multiplicateEmitter(angle, coordinate, e);
                angle += 360 / max;
                list.add(actor);
            }
        }
        list.forEach(a -> emitterList.add(a));
    }

    private void createAndAddEmitterActions(EmitterActor actor,ZONE_ANIM_MODS mod) {
//if (mod.isParallel()){
//    ParallelAction a = new ParallelAction();
//    actor.getActions().forEach(action ->  a .addAction(action));
//
//        }
//        switch (template) {
//
//        }
    }
        private void createAndAddEmitterActions(EmitterActor actor, Coordinates c) {
//        MoveToAction action = ActorMaster.getMoveToAction(c, actor, pixelsPerSecond);
        Vector2 v = GridMaster.
         getVectorForCoordinateWithOffset(c);
        int speed = getPixelsPerSecond();
        if (template!=null )
        {
            GridMaster.offset(getOrigin(), v, template.getAdditionalDistance(getActive()),getOrigin().x>c.x, getOrigin().y>c.y);
            speed =  template.speed;
        }
        MoveByAction action = ActorMaster.getMoveByAction(getOrigin(),
         v, actor,speed);


            if (action.getDuration() > this.duration) {
                this.duration = action.getDuration();
            }
//        ActorMaster.addRemoveAfter(actor);
        actor.getEffect().setDuration((int) action.getDuration());
        anim.setDuration(duration);
    }

    private Action createAndAddEmitterActions(EmitterActor actor, Integer angle, SPELL_ANIMS template) {
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
        Float duration = (float) (Math.sqrt(x * x + y * y) / getPixelsPerSecond());
        action.setDuration(
         duration);
        if (duration > this.duration) {
            this.duration = duration;
        }

        ActorMaster.addRemoveAfter(actor);

        //TRY ALPHA/ROTATE ACTION
        return action;
    }

    public DC_ActiveObj getActive() {
        return (DC_ActiveObj) anim.getActive();
    }

    public float getDuration() {
        return anim.getDuration();
    }

    public Ref getRef() {
        return anim.getRef();
    }

    public int getPixelsPerSecond() {
        return
         anim.getPixelsPerSecond();
    }

    public Vector2 getOrigin() {
        return anim.getOrigin();
    }

    public float getX() {
        return anim.getX();
    }

    public float getY() {
        return anim.getY();
    }

    public enum MULTIPLICATION_METHOD {
        ANGLE,
        COORDINATE,
    }
}
