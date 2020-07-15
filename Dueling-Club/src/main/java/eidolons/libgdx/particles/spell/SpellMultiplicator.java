package eidolons.libgdx.particles.spell;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.AnimEnums;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.std.SpellAnim;
import eidolons.libgdx.anims.std.SpellAnim.SPELL_ANIMS;
import eidolons.libgdx.anims.std.SpellAnim.ZONE_ANIM_MODS;
import eidolons.libgdx.bf.GridMaster;
import main.content.values.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.GeometryMaster;

import java.util.*;

import static eidolons.libgdx.bf.GridMaster.getCenteredPos;

/**
 * Created by JustMe on 1/29/2017.
 */
public class SpellMultiplicator implements Runnable {
    static final MULTIPLICATION_METHOD defaultMultiplicationMethod = MULTIPLICATION_METHOD.COORDINATE;
    MULTIPLICATION_METHOD multiplicationMethod = defaultMultiplicationMethod;
    private final Anim anim;
    private SpellAnim spellAnim;
    //parallel, in different threads!
    private List<SpellVfx> emitterList;
    private float duration;
    private Entity active;
    private SPELL_ANIMS template;

    public SpellMultiplicator(Anim anim) {
        this.anim = anim;
    }

    public static SpellMultiplicator getInstance(Anim anim) {
        return new SpellMultiplicator(anim);
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
        //        if (anim.getPart() == AnimConstructor.ANIM_PART.IMPACT) {
        //            return true;
        //        }
        if (anim.getPart() != AnimEnums.ANIM_PART.MISSILE) {
            return false;
        }
        if (anim instanceof SpellAnim) {
            return ((SpellAnim) anim).getTemplate() != null;
        }
        return false;
    }

    public static int getPrePoolVfxCount(SpellAnim active) {
        if (active.getTemplate() == null) {
            return 1;
        }
        return active.getTemplate().getNumberOfEmitters(active.getActive());
    }

    @Override
    public void run() {
        main.system.auxiliary.log.LogMaster.log(1, getClass().getSimpleName() + " works on " + anim);
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ANIM_DEBUG, anim.getEmitterList().size() + " EmitterList= " + anim.getEmitterList());
        emitterList = anim.getEmitterList();
        duration = anim.getDuration();
        active = anim.getActive();
        if (anim instanceof SpellAnim) {
            spellAnim = (SpellAnim) anim;
            template = spellAnim.getTemplate();
        }
        multiply(anim);
        main.system.auxiliary.log.LogMaster.log(1, getClass().getSimpleName() + " finished with " + anim);
        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.ANIM_DEBUG, anim.getEmitterList().size() + " EmitterList= " + anim.getEmitterList());
    }

    private void multiply(Anim anim) {
        AnimConstructor.removeCache(anim);
        applyTemplate(getMethod(anim));
        try {
            adjustAngle();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public static boolean isMultiAnimRequired(DC_ActiveObj active_) {
        return AnimConstructor.getTemplateForSpell(active_) != null;
    }

    public static MULTIPLICATION_METHOD getMethod(Anim anim) {
        return getMethod((DC_ActiveObj) anim.getActive());
    }

    public static MULTIPLICATION_METHOD getMethod(DC_ActiveObj active) {
        if (active.getProperty(PROPS.ANIM_MODS_VFX).contains("angle;")) {
            return MULTIPLICATION_METHOD.ANGLE;
        }
        return MULTIPLICATION_METHOD.COORDINATE;
    }

    private void adjustAngle() {
        emitterList.forEach(e -> {
            if (e.getTarget() != null) {
                float offset = 1 * (float) GeometryMaster.getAngle(
                        getActive().getOwnerObj().getCoordinates(),
                        //             getRef().getTargetObj().getCoordinates()
                        e.getTarget());
                offset += 90;
                LogMaster.log(1, getActive() + " is offset by " + offset);
                e.getEffect().offsetAngle(offset);
            }
        });
    }

    public void applyTemplate() {
        applyTemplate(defaultMultiplicationMethod);
    }

    private void applyTemplate(MULTIPLICATION_METHOD multiplicationMethod) {
        Set<Coordinates> coordinates = getRef().getArea();
        //         getActive().getAnimator().getZoneAnimCoordinates();
        if (coordinates == null) {

            try {
                coordinates = CoordinatesMaster.getZoneCoordinates(getActive());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            getRef().setArea(coordinates);
        }
        if (coordinates == null) {
            //            TODO GROUP MUST NOT BE COPIED FROM OTHER SPELLS!
            if (getRef().getGroup() != null) {
                Set<Coordinates> set = new LinkedHashSet();
                getRef().getGroup().getObjects().forEach(o -> set.add(o.getCoordinates()));
                coordinates = set;
                getRef().setArea(set);
            }
        }
        if (multiplicationMethod == MULTIPLICATION_METHOD.ANGLE) {
            if (template != null) {
                applyTemplateAngles(coordinates);
                return;
            }
        }
        if (ListMaster.isNotEmpty(coordinates)) {
            applyMultiplicationForCoordinates(coordinates);
        }

        if (template != null) {
            if (template.isRemoveBaseEmitters()) {
                anim.getEmitterList().removeIf(e -> !e.isGenerated());
            }
        }
    }

    public void applyMultiplicationForCoordinates(Set<Coordinates> coordinates) {
        List<SpellVfx> list = new ArrayList<>();

        filterCoordinates(template, coordinates).forEach(c ->
                {
                    for (SpellVfx e : emitterList) {
                        if (e.isGenerated()) {
                            continue;
                        }
                        SpellVfx actor = multiplicateEmitter(null, c, e);
                        list.add(actor);
                    }
                }
        );

        emitterList.addAll(list);

    }

    private Collection<Coordinates> filterCoordinates(SPELL_ANIMS template, Set<Coordinates> coordinates) {

        if (template != null) {
            FACING_DIRECTION facing = getActive().getOwnerObj().getFacing();
            List<Coordinates> filtered = new ArrayList<>(coordinates);
            Coordinates farthest = CoordinatesMaster.getFarmostCoordinateInDirection
                    (facing.getDirection(),
                            new ArrayList<>(coordinates), null);
            switch (template) {
                //                template.getNumberOfEmitters(getActive())
                case RAY:
                case RAY_AUTO:
                    anim.setForcedDestinationForAll(farthest);

                    return Collections.singletonList(farthest);

                case BLAST:
                case NOVA:

                case RING:
                    break;
                case WAVE:
                case SPRAY: {
                    boolean xOrY = !facing.isVertical();
                    filtered.removeIf(c ->
                                    farthest.getXorY(xOrY) != c.getXorY(xOrY)
                            //                     PositionMaster.getDistance(farthest, anim.getOriginCoordinates())
                            //                         > PositionMaster.getDistance(c, anim.getOriginCoordinates())
                    );
                    while (filtered.size() <
                            template.getNumberOfEmitters(getActive())) {
                        List<Coordinates> list = new ArrayList<>(coordinates);
                        list.removeAll(filtered);
                        list.removeIf(c ->
                                farthest.getXorY(!xOrY) == c.getXorY(!xOrY)
                        );
                        Coordinates c = CoordinatesMaster.getFarmostCoordinateInDirection(
                                facing.getDirection(),
                                list, null);
                        if (c != null)
                            filtered.add(c);
                    }
                    if (ListMaster.isNotEmpty(filtered))
                        return filtered;
                    else
                        return coordinates;
                }
            }
        }
        return coordinates;
    }

    private SpellVfx multiplicateEmitter(Integer angle, Coordinates c, SpellVfx e) {
        SpellVfx actor = SpellVfxPool.getEmitterActor(e.path);
        actor.setSpeed(e.getSpeed());
        actor.setPosition(getX(), getY());
        actor.setAttached(false);
        actor.setGenerated(true);
        //        anim.getMaster().addActor(actor);

        if (angle != null) {
            createAndAddEmitterActions(actor, angle, template);
        } else {
            createAndAddEmitterActions(actor, c);
        }

        ActionMaster.addRemoveAfter(actor);
        return actor;
    }

    private void applyTemplateAngles(Set<Coordinates> coordinates) {
        int max = template.getNumberOfEmitters(getActive());
        // 1 for ray, but we know coordinates precisely
        List<SpellVfx> list = new ArrayList<>();

        int angle = 0;
        for (Coordinates coordinate : coordinates) {
            for (SpellVfx e : emitterList) {
                if (e.isGenerated()) {
                    continue;
                }
                SpellVfx actor = multiplicateEmitter(angle, coordinate, e);

                list.add(actor);
            }
            angle += 360 / max;
        }
        emitterList.addAll(list);
    }

    private void createAndAddEmitterActions(SpellVfx actor, ZONE_ANIM_MODS mod) {
        //if (mod.isParallel()){
        //    ParallelAction a = new ParallelAction();
        //    actor.getActions().forEach(action ->  a .addAction(action));
        //
        //        }
        //        switch (template) {
        //
        //        }
    }

    private void createAndAddEmitterActions(SpellVfx actor, Coordinates c) {
        //        MoveToAction action = ActorMaster.getMoveToAction(c, actor, pixelsPerSecond);
        Vector2 v = getCenteredPos(c);
        float speed = getPixelsPerSecond();
        if (template != null) {
            GridMaster.offset(getOrigin(),
                    v, template.getAdditionalDistance(getActive())
            );
            speed = template.speed;
        }
        MoveByAction action = ActionMaster.getMoveByAction(getOrigin(), v, actor, (int) speed);

        if (anim.getPart() == AnimEnums.ANIM_PART.IMPACT) {
            duration = 1f / 100;
            action.setDuration(duration);
        } else {
            if (action.getDuration() > this.duration)
                this.duration = action.getDuration();

            actor.getEffect().setDuration((int) action.getDuration());
            anim.setDuration(duration);
        }
        //        ActorMaster.addRemoveAfter(actor);

    }

    private Action createAndAddEmitterActions(SpellVfx actor, Integer angle, SPELL_ANIMS template) {
        //        Action action = new SequenceAction();
        //        actor.addAction(action);
        //        action.setTarget(actor);
        int range = getActive().getRange();
        //        if ()
        range = getActive().getIntParam(G_PARAMS.RADIUS);

        switch (template) {
            case RAY:
            case RAY_AUTO:
            case BLAST:
            case SPRAY:
            case WAVE:
            case RING:
            case NOVA:
                return addRangeAndAngle(angle, range, actor);

        }

        return null;
    }

    private Action addRangeAndAngle(int angle, int range, SpellVfx actor) {
        MoveByAction action = new MoveByAction();
        //        MoveToAction action = new MoveToAction();

        //        MoveByAction action = ActionMaster.getMoveByAction(getOrigin(), v, actor, (int) speed);
        float x = (float) (132 * Math.sin(angle) * range);
        float y = (float) (132 * Math.cos(angle) * range);
        action.setAmount(x, y);
        //        action.setPosition(actor.getX()+ x, actor.getY()+y);
        //        main.system.auxiliary.log.LogMaster.dev(angle+" angle gives destination: " +(int)action.getX() + "-"+(int)action.getY());
        actor.setRotation(-angle);
        actor.addAction(action);
        action.setTarget(actor);
        Float duration = (float) (Math.sqrt(x * x + y * y) / getPixelsPerSecond());
        action.setDuration(
                duration);
        if (duration > this.duration) {
            this.duration = duration;
        }
        action.setInterpolation(Interpolation.swing);

        //        ActionMaster.addRemoveAfter(actor);

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

    public float getPixelsPerSecond() {
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
