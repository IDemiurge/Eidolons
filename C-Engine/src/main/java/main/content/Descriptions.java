package main.content;

import main.system.auxiliary.StringMaster;

public class Descriptions {
    // Witchery spell school specializes in curses and debilitating
    // enchantments. Mastery score along with Knowledge will automatically add
    // spells to your Spellbook as Known, while also increasing their effect in
    // various ways, different for each particular spell.
    // Mobility Mastery will increase your Dexterity and Agility by 1% per
    // point. Its required for all Specialist and some Melee classes.
    // Mobility skills unlock special move actions that allow easier maneuvering
    // on the battlefield.
    // Athletics Mastery
    // Athletics skills can even more greatly enhance your Strength and Vitality
    // as well as provide special bonuses such as Regeneration or increased
    // Weight carrying capacity.

	/*
     * Rank - novice, apprentice, advanced, expert, master, grand
	 * 0, 10, 20, 30, 40, 50
	 * 
	 */

    public static final String MEDITATION_MASTERY = "Highly useful mastery for any spellcaster, priestly or secular, as it vastly increases the Essence "
            + "capacity and restoration. Directly boosts the amount of Essence restored when using Meditation Mode. "
            + "Additionally, it offers bonuses to Detection, Perception, Magic Resistances and Focus Retainment. "
            + "Certain skills also provide ways to boost Spellpower considerably, while others grant the master "
            + "ability to leave corporeal body or transform it.\n Has synergies with Divination, Unarmed and Discipline "
            + "masteries.\n Prerequisite for Godly classes, especially shamanic and druidic paths."
            + "\n Slightly increases your final Wisdom score (1% per point). ";

    public static final String DIVINATION_MASTERY = "Central to all Godly classes, Divination offers an alternative way of casting spells without learning or "
            + "memorizing, receiving them instead directly from the shared consciousness of whatever Divine entity the "
            + "unit is affiliated with. To receive spells, caster must use Divination mode (or any alternative version "
            + "such as Prayer or Dreamsight).\n The maximum Spell Difficulty is determined by Charisma and Divination Mastery"
            + " score, and its total is also increased by the number of Action Points the unit had left "
            + "(as for most other modes, the effect is resolved if unit starts next round without having been interrupted)."
            + "\n The Deitys favored spell schools will determine the pool from which spells (only Standard ones) "
            + "are then drawn semi-randomly (each schools has a priority). If a spell is Divined that is already in the "
            + "active spellbook, either as Verbatim or Divined, it will instead receive Favored buff (stacks) that "
            + "reduces its costs and increases its effective spellpower by 25% each. ";

    // PRINCIPLES
    public static final String HONOR = "There is no higher joy in life than the sense of righteous power, personal achievement and selfless service that is contained in the Path of Honor - the Old Code"
            + ""
            + "Let the weaklings scheme and conspire. All together, theyll never be stronger "
            + "than one man of Honor! - Griff King Ergron, shortly before being shot in the back lethally by a poisoned bolt.";
    public static final String TREACHERY = "The ends always justify the means. "
            + "Honor is a fancy word for cowardice and stupidity. "
            + "Pragmatism is the only thing that works, dont let them tell you otherwise. "
            + "Devotion and chivalry always ends in disappointment, or worse, in zealotry!";

    public static final String AMBITION = "It is in the nature of all things to desire power, control and greatness. The struggle for it is the only meaning there is, everything else is useless philosophy. ";
    public static final String CHARITY = "All evil comes from selfishness. The poorest man is he who has no compassion The most dangerous man is he who is blinded by the lust for power. ";
    public static final String FREEDOM = "Let no one dictate what is allowed or not. Make your own decisions, be the master of your own fate!";
    public static final String LAW = "One must always do what is right, not easy or desirable. Freedom is an illusion, a false temptation, leading only to suffering, chaos and doubt. The wisest must decree, and the rest follow them. Obedience, order, discipline!";
    public static final String PEACE = "Why do you struggle? We will all die in the end. Look at the trees. They live while they live, and die when they must die. Why do you struggle?";
    public static final String TRADITION = "Wisdom of ages is the only reliable asset of civilization. Progress is naught but a futile rush towards self-destruction... "
            + StringMaster.NEW_LINE
            + "Empires rise and fall, and the higher they rise, the more painful their fall. Only those who learn to keep their traditions have a chance to pass the challenge of time. Elder races are better known to recognize the value Tradition, while the upstart humans have yet to experience the full scope of the devastation that recklessness brings. ";
    public static final String WAR = "What can compare to the sheer joy of the battle? Only the triumph of victory. War is the very lifeblood of creation. Let the dead enjoy their peace...";
    public static final String PROGRESS = "What use is tradition when things are known "
            + "only to deteriorate over time? What use if charity when what you are giving away becomes more sparse with each day? Who cares about power when it is possible for all to live in a world of freedom and enlightenment? And for proof of that, look to the Gods and ask yourself  where could such beings come from if not from a world that has been elevated above mortality itself by relentless efforts of its people?"
            + ""
            + " Stagnation is death and lifes only meaning is accomplishment! "
            + "If all were given the knowledge of Truth, there would be no enemy left but the death itself, "
            + "and even that can be overcome. "
            + "/nTrust me when I say, good colleagues, sky is not the limit. - Archmage Dorpheus ";

    public static final String Vitality = "Provides the character with Endurance necessary to withstand "
            + "continuous enbattlement and Stamina to perform actions. Also slightly increases Toughness"
            + "and gives minor Endurance regeneration";

    public static final String Strength = "	Increases the amount of weight hero can shoulder, essential for 	wearing of heavier armor and weapons. 	Strong characters are not as easily felled, although they can just as 	readily fall to the sheer exhaustion. 	Most heavy weapons have a significant Strength Damage Modifier as well, 	making this attribute into a considerable offensive factor for melee fighters. ";

    public static final String Spellpower = "Called by some the Soul Muscle, its the simplest of all attributes, yet it is "
            + "	also the most mysterious. It is proved not to be connected with either the "
            + "Wisdom "
            + "	or the Willpower or even Intelligence, by mere existence of powerful magicians"
            + " who	possess none of those attribute in any significant measure... "
            + "Such prodigies are often psychically unstable and prone to mood swings. "
            + "Whatever the true nature	of this endowment, it is measurable and can be "
            + "trained by simple practice of spellcasting.	"
            + "	Each spell has its own way of taking advantage of Spellpower,"
            + " some more than others,but generally more effective for simple damaging "
            + "spells, whereas more complex spells will make greater use of the Mastery score.";

    public static final String Intelligence = "Measures the quickness of wit and raw brainpower.	\n Hero with high Intelligence can learn complex spells "
            + "regardless of his level of Knowledge or corresponding Mastery. Moreover, the more Intelligence hero has, "
            + "the more spells he can memorize before combat, as an alternative to learning them En Verbatim. "
            + "	\nFinally, it also provides additional Mastery points per level. ";

    public static final String Knowledge = " Called by arcanists the Crystallized Intelligence, "
            + "its the most reliable way to accomodate spellcasting capacity. "
            + "	Having Knowledge and corresponding Mastery equal to the spells "
            + "Difficulty will automatically add it to heros spellbook (from which he can "
            + "either learn En Verbatim or Memorize to use the spell in combat) or auto-add it as Verbatim if the spell had been Self-Learned. "
            + "	Knowledge also provides additional experience with levels and decreases all experience costs,"
            + " since often hero would already know something before he starts learning, and has greater skill in studying as well. ";
	/*
	 * Even if you intend to increase your Knowledge and spell school Mastery, learning spell manually ahead of time might be a 
	 * good option, as it will be promoted to Verbatim once you meet the aforementioned requirements. 
	 * 
	 *  
	 * 
	 */

    public static final String Agility = "	Measures heros accuracy, precision and "
            + "speed of hand movements, increasing Initiative Modifier and Attack value. Provides additional "
            + "Counter Points for Counter Attacks, Attacks of Opportunity and special attacks. "
            + "Most small, light and exotic weapons also have an Agility Damage Modifier. " + "\n"
            + "An essential prerequisite for any Rogues, Scouts, Fighters and Tricksters "
            + "- all combatants who do not " + "brute strength and heavy weapons. " + "";

    public static final String Dexterity = "Also referred to as Fluidity in Motion by monks and tricksters, "
            + "			 it represents the ease with which hero performs actions, "
            + "providing additional Action Points. Dexterous characters are also difficult to hit properly,"
            + "			 due to their higher Defense rating.	\nIt is highly useful to any character,"
            + "	 but those who need to perform many kinds of actions - spellcasting, movement and attacking - above all.";

    public static final String Wisdom = "Known also as the Alignment with the Essence, Wisdom "
            + "affords a greater capacity for spellcasting by increasing Essence pool. "
            + "and boosts the character's detection score as well as their Astral Resistances."
            + " \nStill, many spellcasters resort to magical items and special training to augment their Essence "
            + "as they dont have time to meditate and reflect on the deep mysteries of existence.";
    public static final String Willpower = "Willpower	Represents the will to victory as well as stoicism against pain, therefore increasing heros "
            + "Spirit, which in turn increases heros Morale. "
            + "	Provides additional Magic and Elemental Resistance , "
            + "as the hero becomes generally more resilient to all that is supernatural. "
            + "	Those with stronger Willpower start combat with more Focus "
            + "and arent as likely to be dominated by Fear and other Mind-Affecting effects. "
            + "	Military academies strongly believe that it is Discipline that cultivates Willpower, advocating merciless drilling. "
            + "Solitary warriors and sorcerers believe that true Willpower can only come from Freedom. "
            + "Could the truth lie in between?";

    // TACTICS_MASTERY(null, Descriptions.TACTICS_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    // LEADERSHIP_MASTERY(null, Descriptions.LEADERSHIP_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    //
    // MARKSMANSHIP_MASTERY(null, Descriptions.MARKSMANSHIP_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    // ITEM_MASTERY(null, Descriptions.ITEM_MASTERY, false, 0, "units", "chars",
    // "classes", "skills"),
    //
    // ATHLETICS_MASTERY(null, Descriptions.ATHLETICS_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    // MOBILITY_MASTERY(null, Descriptions.MOBILITY_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    //
    // BLADE_MASTERY(null, Descriptions.BLADE_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    // BLUNT_MASTERY(null, Descriptions.BLUNT_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    // AXE_MASTERY(null, Descriptions.AXE_MASTERY, false, 0, "units", "chars",
    // "classes", "skills"),
    // POLEARM_MASTERY(null, Descriptions.POLEARM_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    // UNARMED_MASTERY(null, Descriptions.UNARMED_MASTERY, false, 0, "units",
    // "chars", "classes", "skills"),
    //
    // TWO_HANDED_MASTERY(null, Descriptions.TWO_HANDED_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    //
    // DUAL_WIELDING_MASTERY(null,
    // Descriptions.DUAL_WIELDING_MASTERY, false, 0, "units", "chars",
    // "classes", "skills"),
    //
    // ARMORER_MASTERY(null,
    // Descriptions.ARMORER_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // DEFENSE_MASTERY(null,
    // Descriptions.DEFENSE_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // SHIELD_MASTERY(null,
    // Descriptions.SHIELD_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // MEDITATION_MASTERY(null, Descriptions.MEDITATION_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    // DISCIPLINE_MASTERY(null,
    // Descriptions.DISCIPLINE_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // // MAGICAL_ITEM_MASTERY(null, Descriptions., false, 0, "units", "chars",
    // // "classes"),
    //
    // WIZARDRY_MASTERY(null,
    // Descriptions.WIZARDRY_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // SPELLCRAFT_MASTERY(null,
    // Descriptions.SPELLCRAFT_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // DIVINATION_MASTERY(null, Descriptions.DIVINATION_MASTERY, false, 0,
    // "units", "chars", "classes", "skills"),
    // WARCRY_MASTERY(null,
    // Descriptions.WARCRY_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // // spellgroups
    // PSYCHIC_MASTERY(null,
    // Descriptions.PSYCHIC_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // REDEMPTION_MASTERY(null,
    // Descriptions.REDEMPTION_MASTERY, false, 0, "units", "chars", "classes"),
    //
    // SORCERY_MASTERY(null,
    // Descriptions.SORCERY_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // // ANTI_MAGIC_MASTERY(null, Descriptions., false, 0, "units", "chars",
    // // "classes"),
    // ENCHANTMENT_MASTERY(null,
    // Descriptions.ENCHANTMENT_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // // TRANSMUTATION_MASTERY(null, Descriptions., false, 0, "units", "chars",
    // // "classes"),
    // CONJURATION_MASTERY(null,
    // Descriptions.CONJURATION_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // CELESTIAL_MASTERY(null,
    // Descriptions.CELESTIAL_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // BENEDICTION_MASTERY(null,
    // Descriptions.BENEDICTION_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // ELEMENTAL_MASTERY(null,
    // Descriptions.ELEMENTAL_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // SYLVAN_MASTERY(null,
    // Descriptions.SYLVAN_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // DEMONOLOGY_MASTERY(null,
    // Descriptions.DEMONOLOGY_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // WARP_MASTERY(null,
    // Descriptions.WARP_MASTERY, false, 0, "units", "chars", "classes"),
    // DESTRUCTION_MASTERY(null,
    // Descriptions.DESTRUCTION_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // AFFLICTION_MASTERY(null,
    // Descriptions.AFFLICTION_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // BLOOD_MAGIC_MASTERY(null,
    // Descriptions.BLOOD_MAGIC_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // WITCHERY_MASTERY(null,
    // Descriptions.WITCHERY_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // SHADOW_MASTERY(null,
    // Descriptions.SHADOW_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // NECROMANCY_MASTERY(null,
    // Descriptions.NECROMANCY_MASTERY, false, 0, "units", "chars", "classes"),
    //
    // VOID_MASTERY(null,
    // Descriptions.VOID_MASTERY, false, 0, "units", "chars", "classes"),
    // DETECTION_MASTERY(null,
    // Descriptions.DETECTION_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    // STEALTH_MASTERY(null,
    // Descriptions.STEALTH_MASTERY, false, 0, "units", "chars", "classes",
    // "skills"),
    //
    // SAVAGE_MASTERY(null,
    // Descriptions.SAVAGE_MASTERY, false, 0, "units", "chars", "classes"),

}
