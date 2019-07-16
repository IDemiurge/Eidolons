package eidolons.libgdx.particles.ambi;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.BaseView;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums.BF_OBJECT_GROUP;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;

import static eidolons.libgdx.particles.ambi.AttachEmitterManager.ATTACHED_EMITTER_TYPE.*;

/**
 * Created by JustMe on 11/16/2018.
 */
public class AttachEmitterManager {
    public static final boolean TEST_MODE = false;

    public AttachEmitterManager() {
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_MOVED, p -> updateAttachedEmitters(p.get()));
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_CREATED, p -> createAttachedEmitters(p.get()));

    }

    private void updateAttachedEmitters(Object o) {
    }

    private void createAttachedEmitters(Object o) {
        if (o instanceof BaseView) {
            BattleFieldObject obj = (BattleFieldObject) ((Actor) o).getUserObject();
            ATTACHED_EMITTER_TYPE[] types = getEmitterTypes(obj);
            for (ATTACHED_EMITTER_TYPE type : types) {
                if (RandomWizard.chance(50))
                    continue;
                Ambience emitter = createAttachedEmitter(type, obj);
                //            Vector2 v = GridMaster.getCenteredPos(obj.getCoordinates());
                ((BaseView) o).addActor(emitter);
                GdxMaster.center(emitter);
                //shader/blend
                main.system.auxiliary.log.LogMaster.log(1, emitter + " is attached to " + o);
                //            MapMaster.addToListMap(map, o, emitter);

                if (type.below) {
                    emitter.setZIndex(0);
                }
            }
        }
    }

    private Ambience createAttachedEmitter(ATTACHED_EMITTER_TYPE type, BattleFieldObject obj) {
        GenericEnums.VFX preset = getVfx(type);
        //chance to miss a cycle? or pause for X
        return new AttachedEmitter(preset);
    }

    private GenericEnums.VFX getVfx(ATTACHED_EMITTER_TYPE type) {
        WeightMap<GenericEnums.VFX> map = new WeightMap<>(GenericEnums.VFX.class);
        switch (type) {
            case FIRE:
                return map.
                 chain(GenericEnums.VFX.CINDERS, 10).
                 chain(GenericEnums.VFX.CINDERS2, 4).
                 chain(GenericEnums.VFX.CINDERS3, 4).
                 getRandomByWeight();
            case FIRE_MAGIC:
                break;
            case SMOKE:
                return GenericEnums.VFX.SMOKE;
            case CORPSE:
                return map.
                 chain(GenericEnums.VFX.FLIES, 10).
                 chain(GenericEnums.VFX.MOTHS, 4).
                 getRandomByWeight();
            case MIST:
                return map.
                 chain(GenericEnums.VFX.MIST_WHITE, 10).
                 chain(GenericEnums.VFX.MIST_WIND, 8).
                 chain(GenericEnums.VFX.MIST_WHITE2, 10).
                 chain(GenericEnums.VFX.MIST_WHITE3, 8).
                 chain(GenericEnums.VFX.MIST_ARCANE, 12).
                 getRandomByWeight();
            case BUFF:
                break;
        }
        return null;
    }

    private ATTACHED_EMITTER_TYPE[] getEmitterTypes(BattleFieldObject obj) {
        if (obj instanceof Unit) {
//List<ATTACHED_EMITTER_TYPE>
            return new ATTACHED_EMITTER_TYPE[]{
             MIST,
             FIRE,
             MIST,
             FIRE
            };
        }
        if (obj instanceof Structure) {
            BF_OBJECT_GROUP group =
             ((Structure) obj).getBfObjGroup();
            switch (group) {
                case ROCKS:
                    return new ATTACHED_EMITTER_TYPE[]{
                     MIST,
                     MIST,
                     MIST,
                    };
                case LIGHT_EMITTER:
                    return new ATTACHED_EMITTER_TYPE[]{
                     FIRE,
                    };
                case REMAINS:
                    return new ATTACHED_EMITTER_TYPE[]{
                     CORPSE,
                    };
            }
        } else {

        }

        return new ATTACHED_EMITTER_TYPE[0];
    }

    public enum ATTACHED_EMITTER_TYPE {
        FIRE,
        FIRE_MAGIC,
        SMOKE,
        CORPSE,
        MIST,
        MIST_BELOW(true),
        BUFF,;

        public boolean below;

        ATTACHED_EMITTER_TYPE() {
        }

        ATTACHED_EMITTER_TYPE(boolean below) {
            this.below = below;
        }
    }


}
