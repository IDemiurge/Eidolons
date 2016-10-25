package main.content;

import main.content.CONTENT_CONSTS.DEITY;
import main.content.CONTENT_CONSTS.MASTERY_RANK;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.StringMaster;

public class DescriptionMaster {

    public static void initDescriptions() {
        for (PARAMS p : PARAMS.values()) {
            if (!p.isMastery())
                p.setDescr(getNonMasteryDescription(p));
        }
    }

    public static String getMasteryRankDescription(PARAMS param, MASTERY_RANK rank) {
        switch (param) {
            case MARKSMANSHIP_MASTERY:
                if (rank == MASTERY_RANK.NONE)
                    return "You could shoot an ogre if it still stood. In the back. Up close.";
                if (rank == MASTERY_RANK.NOVICE)
                    return "You could hit the mark on a tree if your hands didn't shake...";
                if (rank == MASTERY_RANK.APPRENTICE)
                    return "You could hit the mark on a tree if your mind didn't wander so much...";
                if (rank == MASTERY_RANK.ADEPT)
                    return "Mysteries of Zen are slowly revealing themselves... ";
                if (rank == MASTERY_RANK.ADVANCED)
                    return "Forget about aiming, it's all about the stance and breathing!";
                if (rank == MASTERY_RANK.EXPERT)
                    return "You can shoot a bird in the beak, with your eyes shut and hanging down from a tree. Shooting backwards. With hands tied.";
                /*
				 * DETECTION
				 * "Your eye is so sharp you have adopted a habit of keeping it close most of the time."
				 * "Nothing can take you unawares, if you don't forget to stay awake"
				 * "You start considering making a 
				 * "You are no longer a mere amateur in the field of detection." 
				 * 
				 * ATHLETICS
				 * Morning gymnastics seems too much to bother with 
				 * 
				 * MOBILITY
				 * Only weakling cowards need to learn dodging!
				 *  
				 * Stretching and jumping and running,  
				 * 
				 * DISCIPLINE
				 * 
				 * MEDITATION
				 * Inner oneness seems like a rather vague concept so far...
				 * "You know you can master your mind... You just need a bit of time alone... completely... full day preferably..."
				 * "It seems that harmony has been achieved... If only the pesky unenlightened ones would stop bothering you!.."
				 * 
				 * 
				 * 
				 */
            case PSYCHIC_MASTERY:
                if (rank == MASTERY_RANK.NONE)
                    return "You have difficulty reading the thoughts of your  drunkard friends shouting at you and have a presence so"
                            + " fearsome that people choose not to notice you";
                if (rank == MASTERY_RANK.GRAND_MASTER)
                    return "No mind is too strong for you to break, no spirit too fierce to subdue, no thought too quick to intercept. ";

        }// You are of Character.is
        return "You have a rank of " + StringMaster.getWellFormattedString(rank.name()) + " in "
                + param.getName();

    }

    public static String getMasteryDescription(PARAMS param) {
        switch (param) {
            case WARCRY_MASTERY:
                return "";
            case TACTICS_MASTERY:
                return "";
            case LEADERSHIP_MASTERY:
                return "";
            case MARKSMANSHIP_MASTERY:
                return "Increases accuracy when handling ranged weapons, such as bows and crossbows";
            case ITEM_MASTERY:
                return "Grants various bonuses for Usable items, such as potions, poisons. Also improves accuracy and damage when throwing weapons. Ideal for versatile trickster characters.";
            case SHADOW_MASTERY:
                return "Concealment and considerable damage output (also indirect as in Shadow Blade or Shield) coupled summon spells and a variety of versatile spells such as Leap into Darkness";
            case WITCHERY_MASTERY:
                return "";

            case BENEDICTION_MASTERY:
                return "The quiet grace and purity of the Light, a force most subtle, a love that is stronger than any hate. The Lies cannot ever hurt the Truth, Shadows may not slay the Sun, Darkness may cloud it, and still the sun is there. Likewise, there is courage and strength in every heart, one only needs to pierce the veil of doubt and fear. Faith often is the strongest shield and virtue the sharpest sword, and a restoration of body that comes from deep within...";
            case CELESTIAL_MASTERY:
                return "Long range damage spells and supreme ability to Illuminate the battlefield to the point of blinding your enemies";

            case SORCERY_MASTERY:
                return "Breaking the Laws of Aetherium, "
                        + "tapping into its Weave directly, traveling it corporeally, "
                        + "making light of distance, weight and time itself "
                        + "employing sonic, magnetic and electric forces ";
            case CONJURATION_MASTERY:
                return "Called by some academics the School of Creation, Conjuration is the magic that gives solid ground to any mage, allowing them to conjure objects and items out of thin air";

            case DEMONOLOGY_MASTERY:
                return "Dealing with demons " + ""
                        + "Chaos Cultists use children to communicate with their gods ";

            case REDEMPTION_MASTERY:
                return "All creatures of darkness fear this art, for it turns their own weapons against them, reveals the falseness and weakness of their hearts. Many warriors of the Light receive training in such magic, for while it is unceremonial and blunt, some might say, it is undoubtedly most effective. Its spells can deal heavy Holy damage, dispel negative effects, dismantle enchantments and are particularly effective against Evil - the undead, the demons and the creatures of the dark and all who bear Treachery or Ambition in their hearts.";

            case PSYCHIC_MASTERY:
                return "Despite being less potent than pure Warp Magic, the Art of Psionics "
                        + "is however more penetrating, subtle and precise, affording spells that rob foes of their senses and sanity, "
                        + "methodically breaking their mind and will to do with it as you please."
                        + "\n A psionicists repertoire includes, hallucinations that can mesmerize, haunt and unsettle, hypnosis that puts their foes into slumber or under their command,  phantasms that awaken the deepest fears and primal terror in their victims...  ";
            case ENCHANTMENT_MASTERY:
                return "Intricate and mysterious, Enchantment is considered the most intellectually challenging of all magical arts. "
                        + "The complexity allow to create powerful illusions as well, "
                        + "Some magi being able to put forth more than just a facade, but a veritable simulacrum of reality for their amusement or the ultimate bewilderment of their foes,"
                        + " as they start adding their own inventions to the simulation"
                        + "Telepathy and mind control "
                        + "Matrices that require acute understanding of Aetherium Laws and a fair bit of brainpower to process the ever-changing influx of information,";
            case WARP_MASTERY:
                return "Mercurial and devious ";
            case NECROMANCY_MASTERY:
                return "";
            case SAVAGE_MASTERY:
                return "";
            case DESTRUCTION_MASTERY:
                return "";

            case AFFLICTION_MASTERY:
                return "";
            case BLOOD_MAGIC_MASTERY:
                return "Undoubtedly the closest to what one would call an Evil art, the Blood Magic permits the user to take a very physical kind of control over life, siphoning it from their victims or bloating them until they burst in a gory explosion, making them crave for blood with a fiendish lust or inflicting excruciating pain to suffocate the living or bring violent frenzy into the dead";

            case ELEMENTAL_MASTERY:
                return "";
            case SYLVAN_MASTERY:
                return "";
            case VOID_MASTERY:
                return "";
            case BLADE_MASTERY:
                return "Blade Mastery increases your Attack when using swords and daggers, which are among the lightest and most versatile weapons, with best armor penetration and piercing/slashing damage output. Parry and Defense bonuses also come in handy. Synergy with Axes - bonus of 1 point for every 4 of Blade ";
            case BLUNT_MASTERY:
                return "Don't stop swinging, that's the key!";

            case SHIELD_MASTERY:
                return "";
            case MOBILITY_MASTERY:
                return "";
            case ATHLETICS_MASTERY:
                return "";
            case MEDITATION_MASTERY:
                return "Highly useful mastery for any spellcaster, priestly or secular, as it vastly increases the Essence capacity and restoration. Directly boosts the amount of Essence restored when using Meditation Mode. Additionally, it offers bonuses to Detection, Perception, Magic Resistances and Focus Retainment. Certain skills also provide ways to boost Spellpower considerably, while others grant the master ability to leave corporeal body or transform it. Has <a-combo> with Divination, Unarmed and Discipline masteries. Prerequisite for Godly classes, especially shamanic and druidic paths. Slightly increases your final Wisdom score (1% per point). ";
            case DISCIPLINE_MASTERY:
                return "";
            case WIZARDRY_MASTERY:
                return "";
            case SPELLCRAFT_MASTERY:
                return "";
            case DIVINATION_MASTERY:
                return "Central Mastery for all Godly classes, Divination offers an alternative way of casting spells without learning or memorizing, receiving them instead directly from the shared consciousness of whatever Divine entity the unit is affiliated with. To receive spells, caster must use Divination mode (or any alternative version such as Prayer or Dreamsight). The maximum Spell Difficulty is determined by Charisma and Divination Mastery score, and its total is also increased by the number of Action Points the unit had left (as for most other modes, the effect is resolved if unit starts next round without having been interrupted)The Deitys favored spell schools will determine the pool from which spells (only Standard ones) are then drawn semi-randomly (each schools has a priority). If a spell is Divined that is already in the active spellbook, either as Verbatim or Divined, it will instead receive Favored buff (stacks) that reduces its costs and increases its effective spellpower by 25% each. ";

            case DETECTION_MASTERY:
                return "";
            case ARMORER_MASTERY:
                return "";
            case DEFENSE_MASTERY:
                return "Offers a variety of defensive skills from parrying to dodging, as well as some useful counter-actions. Other skills include Vigilance, First Strike and Alert Mode upgrades. The Mastery score provides a moderate constant bonus to Defense score. As a Knightly Skill, it passes as a requirement for Squire classes, and as a Defensive one, can also be used to acquire certain Fighter classes. ";
            case STEALTH_MASTERY:
                return "";
            case DUAL_WIELDING_MASTERY:
                return "";
            case AXE_MASTERY:
                return "";
            case POLEARM_MASTERY:
                return "";
            case TWO_HANDED_MASTERY:
                return "";
            case UNARMED_MASTERY:
                return "";
        }
        return "";
    }

    public static String getNonMasteryDescription(PARAMS param) {

        switch (param) {
            case FORTITUDE:
                return "";
            // Used in rolls against effect such as Poison, Disease, Instant
            // Death and
            // physical Disintegration.
            // Increases the percentage of Toughness that has to be dealt to
            // the unit in
            // order for it to die, at the rate at which it is restored each
            // turn.
            // In addition, Bleeding counters are removed faster and Wounds
            // will have
            // less effect on the unit
        }
        return "";
    }

    public static String getDeityDescription(DEITY deity) {
        switch (deity) {
            case ALLFATHER: // bonus tooltip like for attributes would be
                // good...
                return "Provides: Unlocks: smithing skills, earth mastery  Prohibits: (integrity penalty)  Recommended for: Notable followers: Giants, Dwarves, Men of Ulduin";
            case ANNIHILATOR:
                return "";
            case ARACHNIA:
                return "";
            case CONSUMER_OF_WORLDS:
                return "";
            case CRIMSON_QUEEN:
                return "";
            case DARK_GODS:
                return "";
            case DEATH_GORGER:
                return "";
            case FAITHLESS:
                return "";
            case FEY_QUEEN:
                return "";
            case FIDE_ARCANUM:
                return "";
            case IRON_GOD:
                return "";
            case KEEPER_OF_TWILIGHT:
                return "";
            case LORD_OF_DECAY:
                return "";
            case LORD_OF_MAGIC:
                return "";
            case NIGHT_LORD:
                return "";
            case OLD_WAY:
                return "";
            case QUEEN_OF_LUST:
                return "";
            case REDEEMER:
                return "";
            case SKY_QUEEN:
                return "";
            case THREE_DIVINES:
                return "";
            case UNDERKING:
                return "";
            case VOID_LORD:
                return "";
            case WARMASTER:
                return "";
            case WILD_GOD:
                return "";
            case WITCH_KING:
                return "";
            case WORLD_SERPENT:
                return "";
            case WORLD_TREE:
                return "";
            case WRAITH_GOD:
                return "";
            default:
                break;

        }
        return "";

    }

    public static String getAttributeQualityDescription(DC_HeroObj hero, PARAMS attribute) {
        switch (attribute) {
            case AGILITY:
                // return "" +
                // ""+ agiFormula1.getInt(new Ref(hero));
                // you can strike a falling object at x speed
                // you can hit

            case CHARISMA:
                break; // you can get your way with up to x% of the people you
            // meet ; your prayers are answered in x% of cases
            case DEXTERITY:
                // you can jump feet high/length, run at velocity, dodge
                // projectile at y speed
                break;
            case INTELLIGENCE:
                // you can keep x variables in your mind
                // you can solve puzzles
                // you can multiple in your head
                break;
            case KNOWLEDGE:
                // you have tested x theories
                // you can recite x formulae and y quotes
                // you know x authors
                // you have read x books
                break;
            case SPELLPOWER:
                break;
            case STRENGTH:
                break;
            case VITALITY:
                // you can withstand of pressure ++ poison
                // you can hold your breath for seconds
                // you can run up to miles without stopping
                break;
            case WILLPOWER:
                // you can remain stoic in up to hard situations
                // you can resist mental suggestion
                break;
            case WISDOM:
                // you can remain still for up to second
                // you are mindful of your surroundings of the time
                // you are mindful of your inner world of the time
                break;
            default:
                break;

        }
        return null;
    }
}
