package main.libgdx.anims.std;

import main.content.CONTENT_CONSTS.FACING_SINGLE;
import main.content.parameters.G_PARAMS;
import main.entity.Entity;
import main.entity.obj.top.DC_ActiveObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.libgdx.anims.AnimData;
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
