package eidolons.libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.particles.spell.SpellVfx;
import eidolons.libgdx.particles.spell.SpellVfxPool;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.G_PARAMS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.Producer;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

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
    protected void initEmitters() {
        if (emitterList == null) {
            String vfx=data.getValue(AnimData.ANIM_VALUES.PARTICLE_EFFECTS);
            if (vfx.isEmpty() || isVfxOverridden(getActive(), getPart())) {
                vfx = //SpellAnimMaster.
                        getOverriddenVfx(getActive(), getPart());
            }
                setEmitterList(SpellVfxPool.getEmitters(vfx));
        }
    }

    public static final  String getOverriddenVfx(DC_ActiveObj active, AnimConstructor.ANIM_PART part) {
        //could be a bit randomized too!
        // enum for vfx after all?

       String data = getForName(part, active.getName());
        if (data != null) {
            return data;
        }
        SpellEnums.SPELL_GROUP group =null ;
        if (active instanceof Spell) {
            group = ((Spell) active).getSpellGroup();

        }
        if (group != null) {
            data = getForGroup(group, part);
        }
        /**
         * advanced/missile cone
         * advanced/missile cone 2
         * advanced/missile cone 3
         * unit/
         *
         createEmitter("unit/black soul bleed 3", 64, 64);
         createEmitter("unit/chaotic dark", 32, 32);
         createEmitter("unit/black soul bleed 3", -64, 64);
         createEmitter("unit/chaotic dark", -32, 32);
         */
        return data ;
    }

    private static String getForGroup(SpellEnums.SPELL_GROUP group, AnimConstructor.ANIM_PART part) {
        String path = StrPathBuilder.build(part.getPartPath() , group, part.getPartPath());
        if (FileManager.isFile(PathFinder.getEnginePath() + PathFinder.getSpellVfxPath() + path)) {
            return path;
        }

        switch (group) {
            case FIRE:
                break;
            case AIR:
                break;
            case WATER:
                break;
            case EARTH:
                break;
            case CONJURATION:
                break;
            case ENCHANTMENT:
                break;
            case SORCERY:
                break;
            case TRANSMUTATION:
                break;
            case VOID:
                break;
            case WITCHERY:
                break;
            case SHADOW:
                break;
            case PSYCHIC:
                break;
            case NECROMANCY:
                break;
            case AFFLICTION:
                break;
            case BLOOD_MAGIC:
                break;
            case WARP:
                break;
            case DEMONOLOGY:
                break;
            case DESTRUCTION:
                break;
            case CELESTIAL:
                break;
            case BENEDICTION:
                break;
            case REDEMPTION:
                break;
            case SYLVAN:
                break;
            case ELEMENTAL:
                break;
            case SAVAGE:
                break;
        }
        return null;
    }

    private static String getForName(AnimConstructor.ANIM_PART part, String name) {
        switch (name) {
            case "Burst of Rage":
            case "Shadow Fury":
                switch (part) {
                    case PRECAST:
//                        return getCircleVfx(group);
                    case MISSILE:
                        return "";
                }

        }
        if (name.contains("Shadow Fury")){
            return "advanced/missile cone 3";
        }
        return null;
    }

    private boolean isVfxOverridden(DC_ActiveObj active, AnimConstructor.ANIM_PART part) {
        return getOverriddenVfx(active,  part) != null;
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
        setDuration(
                Math.min(DEFAULT_MAX_ANIM_DURATION, max));
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
