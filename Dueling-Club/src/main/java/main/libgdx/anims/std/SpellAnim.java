package main.libgdx.anims.std;

import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.active.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.libgdx.anims.AnimData;
import main.libgdx.bf.GridConst;
import main.system.Producer;

import java.util.List;

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

    public SPELL_ANIMS getTemplate() {
        return template;
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
        SPRAY(300, 0, active -> {
            List<Coordinates> list = active.getOwnerObj().getCoordinates().
                    getAdjacentCoordinates();
            list.removeIf(coordinates ->
                    FacingMaster.getSingleFacing(active.getOwnerObj().getFacing(),
                            active.getOwnerObj().getCoordinates(), coordinates) != UnitEnums.FACING_SINGLE.IN_FRONT);
            return list.size();
        }
        ) {
            @Override
            public int getAdditionalDistance(DC_ActiveObj active) {
                if (active.getOwnerObj().getFacing().isVertical()) {
                    return GridConst.CELL_H;
                }
                return GridConst.CELL_W;
            }
        },
        WAVE(active -> active.getIntParam(G_PARAMS.RADIUS)),
        RING(activeObj -> 8),
        NOVA(activeObj -> activeObj.getOwnerObj().getCoordinates().getAdjacentCoordinates().size()),;

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
}
