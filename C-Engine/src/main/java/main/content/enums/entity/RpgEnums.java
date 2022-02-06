package main.content.enums.entity;

import main.content.text.Descriptions;
import main.system.auxiliary.StringMaster;

/**
 * Created by Alexander on 2/1/2022
 */
public class RpgEnums {
    /*
🕀 Prowess
Physical (Strike Force | Fortitude)
Martial (Aim | Coordination)
Mental (Hypnosis | Mind Resilience
Arcane Arts (Magic Arts | Secret Lore)
❖ Craft
Artstic (Performance | Culture
Mechanics (Tinker | Smith)
Science (logic | alchemy)
Nature (survival | animal training)
⛧ Influence
Diplomacy (seduction | bribe)
Intimidation (blackmail | threat)
Deception (impersonation | misdirection)
Etiquette (languages | manners)
     */
    public enum Talent {
        Physical(TalentGroup.Prowess, null),
        Martial(TalentGroup.Prowess, null),
        Mental(TalentGroup.Prowess, null),
        Arcane(TalentGroup.Prowess, null),

        Force(TalentGroup.Prowess, Physical),
        Fortitude(TalentGroup.Prowess, Physical),
        Hypnosis(TalentGroup.Prowess, Mental),
        Resilience(TalentGroup.Prowess, Mental),
        Aim(TalentGroup.Prowess, Martial),
        Coordination(TalentGroup.Prowess, Martial),
        Sorcery(TalentGroup.Prowess, Arcane),
        Lore(TalentGroup.Prowess, Arcane),

        Artistic(TalentGroup.Craft, null),
        Science(TalentGroup.Craft, null),
        Mechanics(TalentGroup.Craft, null),
        Nature(TalentGroup.Craft, null),

        Diplomacy(TalentGroup.Influence, null),
        Intimidation(TalentGroup.Influence, null),
        Deception(TalentGroup.Influence, null),
        Etiquette(TalentGroup.Influence, null),
        ;
        TalentGroup group;
        Talent parent;

        Talent(TalentGroup group, Talent parent) {
            this.group = group;
            this.parent = parent;
        }
    }

    public enum TalentGroup {
        Prowess, Craft, Influence
    }

    public enum WORLD_VIEW {
        faithless,
        blackheart,
        omnivalence,
        tradition,
        zeal;

    }

    public enum HERO_SOUNDSET {
        ANGEL("chars/male/angel/", false),
        FIGHTER("chars/male/quiet/", false),
        TOUGH("chars/male/dwarf/", false),
        JOVIAL("chars/male/trickster/", false),
        CAUTOUS("chars/male/rogue/", false),
        LEADER("chars/male/fighter/", false),
        LEARNED("chars/male/wizard/", false),
        VALIANT("chars/male/champion/", false),
        NOBLE("chars/male/noble/", false),
        MANIAC("chars/male/blackguard/", false),

        W_INNOCENT("chars/female/human/", true),
        W_PLAYFUL("chars/female/bad girl/", true),
        W_JOVIAL("chars/female/wood elf/", true),
        W_NOBLE("chars/female/good girl/", true),
        W_VALIANT("chars/female/fighter/", true),
        W_FEISTY("chars/female/feisty/", true),
        W_LEARNED("chars/female/noble/", true),
        W_TOUGH("chars/female/husky/", true),
        W_DEMENTED("chars/female/vampire female/", true),
        W_VILE("chars/female/succubus/", true),

        // ++ quiet =))
        ;
        private final Boolean female;
        private final String path;

        HERO_SOUNDSET(String path, Boolean female) {
            this.path = path;
            this.female = female;
        }

        public String getPropValue() {
            return path;
        }

        public String getName() {
            return StringMaster.format(name().replace("W_", ""));
        }

        @Override
        public String toString() {
            return StringMaster.format(name().replace("W_", "")).toUpperCase();
        }

        public boolean isFemale() {
            return female;
        }

    }

    public enum PERSONALITY {

        CONFIDENT(PRINCIPLES.LAW, PRINCIPLES.TRADITION),
        FANATICAL(PRINCIPLES.TRADITION, PRINCIPLES.HONOR),

        JOVIAL(PRINCIPLES.PEACE, PRINCIPLES.FREEDOM),
        EMPATHICAL(PRINCIPLES.PEACE, PRINCIPLES.CHARITY),

        FAITHFUL(PRINCIPLES.TRADITION, PRINCIPLES.CHARITY),
        SELFLESS(PRINCIPLES.CHARITY, PRINCIPLES.HONOR),

        ARROGANT(PRINCIPLES.FREEDOM, PRINCIPLES.PROGRESS),
        IMPULSIVE(PRINCIPLES.WAR, PRINCIPLES.PROGRESS),

        PARANOID(PRINCIPLES.TREACHERY, PRINCIPLES.AMBITION),
        GREEDY(PRINCIPLES.AMBITION, PRINCIPLES.TREACHERY),

        SADISTIC(PRINCIPLES.WAR, PRINCIPLES.LAW),
        SPITEFUL(PRINCIPLES.WAR, PRINCIPLES.FREEDOM),
        ;

        PERSONALITY(PRINCIPLES... p) {

        }
    }

    public enum PRINCIPLES {

        WAR, PEACE, HONOR, TREACHERY, LAW, FREEDOM, CHARITY, AMBITION, TRADITION, PROGRESS,
        ;

        static {
            HONOR.setOpposite(TREACHERY);
            TREACHERY.setOpposite(HONOR);

            AMBITION.setOpposite(CHARITY);
            CHARITY.setOpposite(AMBITION);

            FREEDOM.setOpposite(LAW);
            LAW.setOpposite(FREEDOM);

            PEACE.setOpposite(WAR);
            WAR.setOpposite(PEACE);

            TRADITION.setOpposite(PROGRESS);
            PROGRESS.setOpposite(TRADITION);

            HONOR.setDescription(Descriptions.HONOR);
            TREACHERY.setDescription(Descriptions.TREACHERY);
            AMBITION.setDescription(Descriptions.AMBITION);
            CHARITY.setDescription(Descriptions.CHARITY);
            FREEDOM.setDescription(Descriptions.FREEDOM);
            LAW.setDescription(Descriptions.LAW);
            PEACE.setDescription(Descriptions.PEACE);
            WAR.setDescription(Descriptions.WAR);
            TRADITION.setDescription(Descriptions.TRADITION);
            PROGRESS.setDescription(Descriptions.PROGRESS);
        }

        private PRINCIPLES opposite;
        private String description;

        PRINCIPLES() {

        }

        public PRINCIPLES getOpposite() {
            return opposite;
        }

        public void setOpposite(PRINCIPLES opposite) {
            this.opposite = opposite;
        }

        @Override
        public String toString() {
            return StringMaster.format(name());
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
