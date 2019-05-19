package eidolons.libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.particles.spell.SpellVfx;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.G_PARAMS;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.Producer;

import java.util.Set;

/**
 * Created by JustMe on 1/24/2017.
 */
public class SpellAnim extends ActionAnim {

    static {
        SPELL_ANIMS.BLAST.setRemoveBaseEmitters(false);
    }

    SPELL_ANIMS template;

    public SpellAnim(Entity active, AnimData params, SPELL_ANIMS template) {
        super(active, params);
        this.template = template;
    }

    @Override
    protected void initDuration() {
        super.initDuration();
        float max=DEFAULT_ANIM_DURATION;
        for (SpellVfx e : getEmitterList()) {
        for (ParticleEmitter emitter : e.getEffect().getEmitters()) {
            if (max<emitter.duration)
                max = emitter.duration;

            e.setSpeed(AnimMaster.getAnimationSpeedFactor());
        }
        }
        setDuration(max);
    }

    @Override
    public void setDuration(float duration) {

        super.setDuration(duration);
    }

    public SPELL_ANIMS getTemplate() {
        return template;
    }

    public enum SPELL_ANIMS {
        RAY(activeObj -> 1),
        BLAST(active -> (active.getIntParam(G_PARAMS.RADIUS) == 0) ? 1 :
         active.getRef().getTargetObj().getCoordinates().
          getAdjacentCoordinates().size()),
        SPRAY(300, 0, active -> {
            Set<Coordinates> set = active.getOwnerUnit().getCoordinates().
             getAdjacentCoordinates();
            set.removeIf(coordinates ->
             FacingMaster.getSingleFacing(active.getOwnerUnit().getFacing(),
              active.getOwnerUnit().getCoordinates(), coordinates) != UnitEnums.FACING_SINGLE.IN_FRONT);
            return set.size();
        }
        ) {
            @Override
            public int getAdditionalDistance(DC_ActiveObj active) {
                if (active.getOwnerUnit().getFacing().isVertical()) {
                    return GridMaster.CELL_H;
                }
                return GridMaster.CELL_W;
            }
        },
        WAVE(active -> active.getIntParam(G_PARAMS.RADIUS)),
        RING(activeObj -> 8),
        NOVA(activeObj -> activeObj.getOwnerUnit().getCoordinates().getAdjacentCoordinates().size()),;

        public int speed;
        private Producer<DC_ActiveObj, Integer> numberOfEmitters;
        private boolean removeBaseEmitters = true;

        //emitter placement templates
        SPELL_ANIMS() {

        }

        SPELL_ANIMS(int speed, int distance, Producer<DC_ActiveObj, Integer> numberOfEmittersSupplier) {
            this.speed = speed;
            numberOfEmitters = numberOfEmittersSupplier;
        }

        SPELL_ANIMS(Producer<DC_ActiveObj, Integer> numberOfEmittersSupplier) {
            this(300, 0, numberOfEmittersSupplier);
        }

        public int getAdditionalDistance(DC_ActiveObj active) {

            return 0;
        }

        public int getNumberOfEmitters(DC_ActiveObj active) {
            return numberOfEmitters.produce(active);
        }


        public boolean isRemoveBaseEmitters() {
            return removeBaseEmitters;
        }

        public void setRemoveBaseEmitters(boolean removeBaseEmitters) {
            this.removeBaseEmitters = removeBaseEmitters;
        }
    }

    public enum ZONE_ANIM_MODS {
        SWIRL,
        RETRACT,

    }
}
