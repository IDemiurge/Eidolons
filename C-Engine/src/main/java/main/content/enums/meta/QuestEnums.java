package main.content.enums.meta;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestEnums {
    public enum QUEST_GROUP {
        TUTORIAL,
        SCENARIO,
        RNG,
        ADVENTURE,
    }

    public enum QUEST_LEVEL {
        EASY,
    }

    public enum QUEST_REWARD_TYPE {
        ITEM,
        RANK,
        XP,
        GOLD,
        MIXED,
        RANDOM,

    }

    public enum QUEST_TIME_LIMIT {
        WALK,
        JOG,
        RUN,
        DASH,
    }

    public enum QUEST_TYPE {
        BOSS,
        HUNT,
        FIND,
        ESCAPE,
    }
}
