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
        OBJECTS,
        SECRETS,
        COMMON_ITEMS,
        SPECIAL_ITEM,
        ESCAPE,
    }
}
