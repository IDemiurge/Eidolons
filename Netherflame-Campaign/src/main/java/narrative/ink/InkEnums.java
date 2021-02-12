package narrative.ink;

/**
 * Created by JustMe on 11/20/2018.
 * <p>
 * Emotional patterns
 * flavor
 * <p>
 * chatting
 * haggling
 * <p>
 */
public class InkEnums {
    public enum INK_ACTOR_VAR {
        NPC_NAME,

    }

    public enum INK_CONTEXT_VAL {
        REGION,
        //?
    }

    public enum INK_DIALOGUE_TEMPLATE {
        quest, //is there any work? Anything you need?
        shop, //standard ins and outs... show me your wares and the like
        chat, //any news?

    }


    public enum INK_GLOBAL_VAR {
        TIME_OF_DAY,
        TOWN_NAME,
        REGION_NAME,

        PC_,
    }

    public enum INK_STD_CONTEXT {
        STOKEPORT,

        INNS,

        BASTION,

    }

    public enum INK_STD_STORY_VARS {
        has_warden_pin,

    }

    public enum INK_VARS {
        INFORMAL_MALE,
        EXPLETIVE,

        FORMAL,
        BACKGROUND_REFERENCE,


    }



    public enum STD_INFLUENCE_ACTION {
        intimidate,
    }

    public enum STORY_STATE {

    }
}
