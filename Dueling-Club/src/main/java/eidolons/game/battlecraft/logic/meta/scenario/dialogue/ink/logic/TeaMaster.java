package eidolons.game.battlecraft.logic.meta.scenario.dialogue.ink.logic;

import main.content.enums.entity.HeroEnums.PRINCIPLES;

/**
 * Created by JustMe on 2/14/2017.
 *
 * what are the main gears of this mechanic?
 *
 * 1) We enter into conversation and see some limited info about the NPC - perhaps its mood, profession,
 * approx. Influence levels...
 * 2) We can use the Gift to see more - their Traits (tags),
 * 3)
 *
 * conversation is build from a number of branches
 * for instance, an Innkeep will have
 * > rumors
 * > quests
 * > influence
 *
 * influence options will be broad enough:
 * perhaps they could work w/o words -
 * [boast]
 * [threaten]
 * [joke]
 * (some 5 more)
 *
 * the reaction will first be mathematical...
 *
 * Really? You handled all five of them, wow, my old man could do seven any day of the week!
 * Piss off...
 *
 *
 * most responses have a tag of 'type'
 *
 *
 */
public class TeaMaster {

    public enum INFLUENCE_ACTION {

        threaten, //trepidation
        boast, //esteem+trepidation

        seduce, //affection
        caress,//trepidation+affection

        connect, //esteem if has traveled enough in their home region
        JOKE , //affection+esteem completely on random + background bonus

    }
        public enum AFFECTION_LEVEL {
        AVERSE, INDIFFERENT, FOND, ATTRACTED, ENAMORED,
    }

    public enum ESTEEM_LEVEL {
        DISGUSTED, SCORNFUL, APPROVING, IMPRESSED, REVERENT
    }

    //can be assigned to a speech as a tag, will determine its Influence
    public enum FLAVOR {
        terrifying,
        seductive,
        boastful,
        jaded,
        sarcastic,
        funny,
        deadpan,


    }

    public enum INFLUENCE {
       TREPIDATION, ESTEEM, AFFECTION,

    }

//sets some coefs on influence perceptiveness
    public enum MOOD {
        ELATED,
        INSPIRED,
        OPTIMISTIC,

        TROUBLED,
        MOODY,
        WISTFUL,
        HEARTBROKEN,

    }

    // * Each profession should have a small custom chat line
    // * E.g. bard should be able to tell a story or sing a song…

    //basic version should have less?
    public enum NPC_PROFESSION {
        Scholar,
        Traveler,
        Bard,
        Gambler,
        Fraud,
        Merchant,
        Mercenary,
        Governor,
        Elder,

        Prostitute,
        Madam,

        Drug_dealer,
        Death_dealer,
        Sailor,
        Captain,

        Vagabond, outlaw, Heretic, Exile, Fugitive, Artist, minstrel,;
    }

    public enum NPC_CUSTOM_TAG {
        DRINKER,
        ADDICT,
        CRIPPLE,
        HOMOSEXUAL,
        ELDERLY,
        YOUNG,

    }
        public enum PERSONALITY_TAG {
        MISTRUSTING, TRUSTING,
        INDIFFERENT,

        idealist, friendly, hostile,
        pessimist, optimist,
        Sarcastic, Fatalist,
        COWARDLY, BRAVE,
        DECEITFUL, HONEST,

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
        SPITEFUL(PRINCIPLES.WAR, PRINCIPLES.FREEDOM),;

        PERSONALITY_TAG(PRINCIPLES... p) {

        }
    }

    public enum TEA_PROFILE {
        INSENSITIVE,
        SCEPTICAL,
        PRAGMATIC,
        NEUTRAL,
        IRRATIONAL,
        RECEPTIVE,
        SENSITIVE,
    }

    public enum TREPIDATION_LEVEL {
        AMUSED, UNIMPRESSED, WARY, SCARED, TERRIFIED,
    }

    public enum TRUST_LEVEL {
        MISTRUSTFUL, CAUTIOUS, RECEPTIVE, TRUSTING, DEIFYING,
    }
}
