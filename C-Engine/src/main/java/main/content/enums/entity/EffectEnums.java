package main.content.enums.entity;

import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.images.ImageManager;

import java.util.Map;

/**
 * Created by Alexander on 2/2/2022
 */
public class EffectEnums {
    public enum COUNTER {
        Conflagration,
        Blaze(Conflagration),
        Freeze,
        Chill(Freeze),
        Venom,
        Poison(Venom),
        Contamination,
        Disease(Contamination),


        Polarization,
        Magnetized(Polarization),
        Potency,
        Charge(Potency),//TODO  Positive_ ??
        Magma,
        Lava(Magma),
        Encase(),
        Clay(Encase),
        Pollution,
        Grease(Pollution),
        Soak,
        Moist(Soak),


        Terror,
        Fear(Terror),
        Madness(),
        Haunted(Madness),
        Despair(),
        Vertigo(),
        Lust(Vertigo),
        Oblivion(),
        Haze(Oblivion),

        Hemorrhage,
        Bleeding(Hemorrhage),
        Stun,
        Concussion(Stun),
        Entanglement,
        Ensnared(Entanglement),
        Asphyxiation,
        Suffocation(Asphyxiation),

        Disloyalty,
        Dismay(Disloyalty),
        Agony,
        Pain(Agony),
        Injury,
        Wounds(Injury),
        Exhaustion,
        Fatigue(Exhaustion),

        Ritual,
        Channeling(Ritual),
        Fury,
        Rage(Fury),
        Killer,
        Adrenaline(Killer),
        Ecstasy(),
        Energy(Ecstasy),
        Inspiration,
        Morale(Inspiration),
        Devotion,
        Loyalty(Devotion),

        Holiness,
        Virtue(Holiness),
        Immortality,
        Undying(Immortality),
        Zen(),
        Stillness(Zen),


        Mutation(),
        Mutagen(Mutation),
        Taint(),
        Blight(Taint),
        Corrosion(),
        Acid(Corrosion),

        ;

        private Map<COUNTER, COUNTER_INTERACTION> interactionMap;
        private COUNTER down;
        private COUNTER up;
        private final String imagePath;
        private final String name =
                StringMaster.format(name()) + Strings.COUNTER;
        private COUNTER upgraded;

        COUNTER(COUNTER upgraded) {
            this();
            this.upgraded = upgraded;
        }

        COUNTER() {
            imagePath = ImageManager.getValueIconsPath() + "counters/" + toString() + ".png";
        }

        public boolean isNegativeAllowed() {
            return false;
        }

        public String getName() {
            return name;
        }

        public boolean isUpgraded() {
            return upgraded == null;
        }

        public Map<COUNTER, COUNTER_INTERACTION> getInteractionMap() {
            return interactionMap;
        }

        public void setInteractionMap(Map<COUNTER, COUNTER_INTERACTION> interactionMap) {
            this.interactionMap = interactionMap;
        }

        public COUNTER getDown() {
            return down;
        }

        public void setDown(COUNTER down) {
            this.down = down;
        }

        public COUNTER getUp() {
            return up;
        }

        public void setUp(COUNTER up) {
            this.up = up;
        }

        public String getImagePath() {
            return imagePath;
        }

        public COUNTER getUpgraded() {
            return upgraded;
        }
    }

    public enum COUNTER_INTERACTION {
        CONVERT_TO, CONVERT_FROM, MUTUAL_DELETION, DELETE_OTHER, DELETE_SELF,
        TRANSFORM_UP, TRANSFORM_DOWN,
        GROW_SELF, GROW_OTHER, GROW_BOTH,
    }

    public enum COUNTER_OPERATION {
        TRANSFER_TO,
        TRANSFER_FROM,
        ;

    }
}
