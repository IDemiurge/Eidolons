package libgdx.anims.std;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.math.MathUtils;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.game.EidolonsGame;
import libgdx.anims.AnimData;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.bf.GridMaster;
import libgdx.particles.spell.SpellVfx;
import libgdx.particles.spell.SpellVfxPool;
import main.content.enums.entity.SpellEnums;
import main.content.values.parameters.G_PARAMS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.Producer;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
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

    public SpellAnim(Entity active, AnimData params, SPELL_ANIMS template, VisualEnums.ANIM_PART part) {
        super(active, params, part);
        this.template = template;
    }

    @Override
    protected void initEmitters() {
        //TODO use template!
        //        if (!ListMaster.isNotEmpty(emitterList) || CoreEngine.isActiveTestMode())
        {
            String vfx = data.getValue(AnimData.ANIM_VALUES.PARTICLE_EFFECTS);
            if (StringMaster.isEmpty(vfx) || isVfxOverridden(getActive(), getPart())) {
                vfx = //SpellAnimMaster.
                        getOverriddenVfx(getActive(), getPart());
                main.system.auxiliary.log.LogMaster.verbose(getActive() + " gets an OVERRIDE VFX: " + vfx);
            }
            setEmitterList(SpellVfxPool.getEmitters(vfx, 1));
        }
    }

    @Override
    protected void resetSprites() {
        super.resetSprites();
        if (!emitterList.isEmpty()) {
            sprites.removeIf(SpriteAnimationFactory::isDefault);
        }

    }

    public static String getOverriddenVfx(ActiveObj active, VisualEnums.ANIM_PART part) {
        //could be a bit randomized too!
        // enum for vfx after all?

        String data = getForName(part, active.getName());
        if (data != null) {
            return data;
        }
        SpellEnums.SPELL_GROUP group = null;
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
        return data;
    }

    private static String getForGroup(SpellEnums.SPELL_GROUP group, VisualEnums.ANIM_PART part) {
        String path = StrPathBuilder.build(part.getPartPath(), group, part.getPartPath());
        if (FileManager.isFile(PathFinder.getRootPath() + PathFinder.getSpellVfxPath() + path)) {
            return path;
        }
        if (EidolonsGame.FOOTAGE) {
            switch (group) {
                case FIRE:
                    switch (part) {
                        case MISSILE:
                            return "missile/chaos missile";
                    }
            }
        }
        switch (group) {
            case FIRE:
                if (part == VisualEnums.ANIM_PART.CAST) {
                    int i = RandomWizard.getRandomInt(4) + 1;
                    if (i == 1) {
                        return "cast/destruction center5";
                    }
                    return "cast/destruction center5";
                }

                if (part == VisualEnums.ANIM_PART.AFTEREFFECT) {
                    if (RandomWizard.chance(66))
                        return "flow/fire flow";
                    return "flow/fire flow3";
                }
                break;
            case SAVAGE:
                if (part == VisualEnums.ANIM_PART.CAST) {
                    int i = RandomWizard.getRandomInt(2) + 1;
                    if (i == 1) {
                        return "cast/savage center";
                    }
                    return "cast/savage center" + i;
                }
                break;
            case CELESTIAL:
                switch (part) {
                    case PRECAST:
                    case CAST:
                        if (RandomWizard.chance(66))
                            return "cast/celestial circle slow2";
                        return "cast/celestial circle slow";
                    case MISSILE:
                        if (RandomWizard.chance(66)) {
                            if (RandomWizard.chance(66))
                                return "new missile/celestial missile4";
                            return "new missile/celestial missile3";

                        }
                        if (RandomWizard.chance(66))
                            return "missile/celestial missile4";
                        return "missile/celestial missile3";
                    case IMPACT:
                    case AFTEREFFECT:
                        return "subtle/light";
                }
            case AIR:
                switch (part) {
                    case PRECAST:
                    case CAST:
                    case IMPACT:
                        if (RandomWizard.chance(66))
                            return "cast/new storm";
                        return "shape/blast electric";
                    case MISSILE:
                        if (RandomWizard.chance(66))
                            return "missile/electro missile";
                        return "shape/torrent";
                    case AFTEREFFECT:
                        if (RandomWizard.chance(66))
                            return "shape/electric teleport";
                        return "shape/wind fade swirl chaos 2";
                }
                break;
            case BENEDICTION:
                switch (part) {
                    case PRECAST:
                    case CAST:
                        int i = RandomWizard.getRandomInt(8) + 1;
                        if (i == 1) {
                            return "cast/benediction circle slow";
                        }
                        return "cast/benediction circle slow" + i;
                    case MISSILE:
                        if (RandomWizard.chance(66))
                            return "missile/benediction missile unattached";
                        return "missile/benediction missile2";
                    case IMPACT:
                    case AFTEREFFECT:
                        return "";
                }
                break;
            case REDEMPTION:
                switch (part) {
                    case PRECAST:
                    case CAST:
                        int i = RandomWizard.getRandomInt(8) + 1;
                        if (i == 1) {
                            return "cast/redemption circle slow";
                        }
                        return "cast/redemption circle slow" + i;
                    case MISSILE:
                    case IMPACT:
                    case AFTEREFFECT:
                        if (RandomWizard.chance(66))
                            return "cast/redemption circle static2";
                        return "cast/redemption circle static";
                }
                break;
            case WITCHERY:
                if (part == VisualEnums.ANIM_PART.CAST) {
                    int i = RandomWizard.getRandomInt(2) + 1;
                    if (i == 1) {
                        return "cast/witchery center";
                    }
                    return "cast/witchery center" + i;
                }
                break;
            case SHADOW:
                //                "breath"

                if (part == VisualEnums.ANIM_PART.MISSILE) {
                    //                    if (RandomWizard.chance(66))
                    //                        return "missile/shadow missile3";
                    //                    if (RandomWizard.chance(66))
                    //                        return "missile/dark writhe";
                    //                    return "missile/shadow missile2";
                }
                if (part == VisualEnums.ANIM_PART.CAST) {
                    int i = RandomWizard.getRandomInt(2) + 1;
                    if (i == 1) {
                        return "cast/shadow center";
                    }
                    return "cast/shadow center" + i;
                }
                break;
            case PSYCHIC:
                if (part == VisualEnums.ANIM_PART.CAST) {
                    int i = RandomWizard.getRandomInt(6) + 1;
                    if (i == 1) {
                        return "cast/witchery circle slow";
                    }
                    return "cast/witchery circle slow" + i;
                }
                return "cast/dark circle";
            case NECROMANCY:
                switch (part) {
                    case AFTEREFFECT:
                        if (RandomWizard.chance(66))
                            return "shape/soul flames 5";
                        if (RandomWizard.chance(66))
                            return "shape/soul flames 4";
                        return "shape/soul flames 2";
                    case PRECAST:
                        if (RandomWizard.chance(66))
                            return "shape/soul dissipation pale";
                        return "shape/soul dissipation short";

                    case IMPACT:
                        if (RandomWizard.chance(66))
                            return "shape/soul drain thick";
                        return "shape/ghostly teleport";
                    case CAST:
                        if (RandomWizard.chance(66))
                            return "shape/soul drain";
                        return "shape/soul drain thick";
                    case MISSILE:
                        if (RandomWizard.chance(66))
                            return "shape/ghostly teleport small short";
                        return "shape/ghostly teleport small wraith";

                }
                //shape/ghostly teleport
                break;
            case AFFLICTION:

                switch (part) {
                    case PRECAST:
                    case CAST:
                    case MISSILE:
                        if (RandomWizard.chance(66))
                            return "missile/new soul burn";
                        return "missile/new chaos bolt";
                    case IMPACT:
                        return "flow/afflict flow up";
                    case AFTEREFFECT:
                        /**
                         * witchery
                         * vampire
                         * blood
                         * psychic 3
                         */
                        if (RandomWizard.chance(66))
                            return "flow/afflict flow3";
                        if (RandomWizard.chance(66))
                            return "flow/afflict flow up";
                        return "flow/afflict flow";
                }
                break;
            case BLOOD_MAGIC:

                switch (part) {
                    case PRECAST:
                        if (RandomWizard.chance(66))
                            return "flow/blood flow2";
                        return "flow/blood flow";
                    case CAST:
                        if (RandomWizard.chance(66))
                            return "flow/vampire";
                        return "flow/blood flow pain";
                    case MISSILE:
                    case IMPACT:
                        if (RandomWizard.chance(66))
                            return "flow/blood flow up2";
                        return "flow/blood flow up";
                }
                break;
            case WARP:
                switch (part) {
                    case PRECAST:
                    case CAST:
                    case MISSILE:
                    case IMPACT:
                    case AFTEREFFECT:
                        return "";
                }
                break;
            case CONJURATION:
                if (RandomWizard.chance(66))
                    return "sand flow2";
                return "subtle light3";
            case DEMONOLOGY:
            case ELEMENTAL:
            case SYLVAN:
            case DESTRUCTION:
                break;
        }

        return null;
    }

    private static String getForName(VisualEnums.ANIM_PART part, String name) {
        switch (name) {
            case "Burst of Rage":
            case "Shadow Fury":
                switch (part) {
                    case CAST:
                        return "shape/nova waves";
                    case PRECAST:
                        //                        return getCircleVfx(group);
                    case MISSILE:
                        return "";
                }

        }
        if (name.contains("Shadow Fury")) {
            return "advanced/missile cone 3";
        }
        return null;
    }

    private boolean isVfxOverridden(ActiveObj active, VisualEnums.ANIM_PART part) {
        return getOverriddenVfx(active, part) != null;
    }


    @Override
    protected void initDuration() {
        super.initDuration();
        float max = DEFAULT_ANIM_DURATION;
        for (SpellVfx e : getEmitterList()) {
            for (ParticleEmitter emitter : e.getEffect().getEmitters()) {
                if (e.getSpeed() == 1f)
                    e.setSpeed(MathUtils.lerp(getDefaultVfxSpeed(part), AnimMaster.speedMod(),
                            0.3f));
                if (max < emitter.duration / e.getSpeed())
                    max = emitter.duration / e.getSpeed();

            }
        }
        //        for (SpellVfx e : getEmitterList()) {
        //            for (ParticleEmitter emitter : e.getEffect().getEmitters()) {
        //                if (max < emitter.duration)
        //                    max = emitter.duration;
        //                e.setSpeed(AnimMaster.getAnimationSpeedFactor());
        //            }
        //        }
        for (SpriteAnimation e : getSprites()) {
            if (max < e.getFrameDuration() * e.getFrameNumber())
                max = e.getFrameDuration() * e.getFrameNumber();
            //            e.setSpeed(AnimMaster.getAnimationSpeedFactor());
        }

        if (getActive() instanceof Spell) {
            if (((Spell) getActive()).isChannelingNow()) {
                main.system.auxiliary.log.LogMaster.devLog(" ANIM FOR ChannelingNow " + getActive());
                switch (getPart()) {
                    case CAST:
                        main.system.auxiliary.log.LogMaster.devLog(" ANIM FOR ChannelingNow duration =" + max);
                        if (getActive().getOwnerUnit().isPlayerCharacter()) {
                            getEmitterList().forEach(e -> e.getEffect().setAlpha(0.74f));
                        }
                        setDuration(max);
                        return;
                }
            }
            setDuration(0);
        }

        main.system.auxiliary.log.LogMaster.devLog("Spell anim duration set: " + max);
        setDuration(
                Math.min(DEFAULT_MAX_ANIM_DURATION, max));
    }

    @Override
    public boolean draw(Batch batch) {
        boolean reslt = super.draw(batch);
        if (getActive() instanceof Spell) {
            if (((Spell) getActive()).isChannelingNow()) {
                if (!getActive().getOwnerUnit().isDead()) {
                    switch (getPart()) {
                        case CAST:
                            return true;
                    }
                }
            }
        }
        return reslt;
    }

    private float getDefaultVfxSpeed(VisualEnums.ANIM_PART part) {
        switch (part) {
            //            case CAST:
            //                return 1.18f;
        }
        return 1f;
    }

    @Override
    public void setDuration(float duration) {

        super.setDuration(duration);
    }

    public SPELL_ANIMS getTemplate() {
        return template;
    }

    public enum SPELL_ANIMS {
        STAR(activeObj -> 4),
        CROSS(activeObj -> 4),
        RAY(activeObj -> 1),
        RAY_AUTO(activeObj -> 1),
        BLAST(active -> (active.getIntParam(G_PARAMS.RADIUS) == 0) ? 1 :
                active.getRef().getTargetObj().getCoordinates().
                        getAdjacentCoordinates().size()),
        RECTANGLE(active -> active.getIntParam(G_PARAMS.RADIUS)),
        WAVE(active -> active.getIntParam(G_PARAMS.RADIUS)),
        RING(activeObj -> 8),
        NOVA(activeObj -> activeObj.getOwnerUnit().getCoordinates().getAdjacentCoordinates().size()),
        SPRAY(300, 0, active -> {
            Set<Coordinates> set = active.getOwnerUnit().getCoordinates().
                    getAdjacentCoordinates();
            //TODO LC 2.0
            // set.removeIf(coordinates ->
            //         FacingMaster.getSingleFacing(active.getOwnerUnit().getFacing(),
            //                 active.getOwnerUnit().getCoordinates(), coordinates) != UnitEnums.FACING_SINGLE.IN_FRONT);
            return set.size();
        }) {
            public int getAdditionalDistance(ActiveObj active) {
                //TODO Grid 2.0
                return GridMaster.CELL_W;
            }
        },
        ;

        public int speed;
        private Producer<ActiveObj, Integer> numberOfEmitters;
        private boolean removeBaseEmitters = true;

        //emitter placement templates
        SPELL_ANIMS() {

        }

        SPELL_ANIMS(int speed, int distance, Producer<ActiveObj, Integer> numberOfEmittersSupplier) {
            this.speed = speed;
            numberOfEmitters = numberOfEmittersSupplier;
        }

        SPELL_ANIMS(Producer<ActiveObj, Integer> numberOfEmittersSupplier) {
            this(300, 0, numberOfEmittersSupplier);
        }

        public int getAdditionalDistance(ActiveObj active) {

            return 0;
        }

        public int getNumberOfEmitters(ActiveObj active) {
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
