package main.content.enums.meta;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestEnums {
    public enum QUEST_GROUP {
        TUTORIAL,
        SCENARIO,
        DEMO,
        RNG,
        ADVENTURE,
    }

    public enum QUEST_LEVEL {
        EASY(1),
        AVERAGE(1.5f),
        HARD(2),
        ;
        public float factor ;

        QUEST_LEVEL(float factor) {
            this.factor = factor;
        }
    }

    public enum QUEST_REWARD_TYPE {
        ITEM,
        RANK,
        NONE,
        SOULS,
        XP,
        GOLD,
        MIXED,
        RANDOM,
        GLORY, ANTI_GLORY,

    }

    public enum QUEST_TIME_LIMIT {
        WALK,
        JOG,
        RUN,
        DASH,
    }

    public enum QUEST_TYPE {
        CUSTOM,
        BOSS,
        HUNT,
        OBJECTS,
        SECRETS,
        COMMON_ITEMS,
        SPECIAL_ITEM,
        ESCAPE,
        TUTORIAL,
    }
}
