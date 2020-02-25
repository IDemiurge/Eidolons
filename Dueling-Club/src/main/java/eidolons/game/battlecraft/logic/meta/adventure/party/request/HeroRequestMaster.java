package eidolons.game.battlecraft.logic.meta.adventure.party.request;

/**
 * Created by JustMe on 7/23/2017.
 */
public class HeroRequestMaster {

    public void processRequest(HeroRequest request, Boolean response) {
        if (response == null) {
            return; //'later'?
        }
        if (response)
            switch (request.getType()) {
                case REMOVE_OTHER:
                case ITEM:
                case FORMATION:
                case INCREASE_SHARE:
                case ADD_OTHER:
                    break;
            }
    }

    public enum REQUEST_TYPE {
        REMOVE_OTHER,
        ADD_OTHER,
        INCREASE_SHARE,
        FORMATION,
        ITEM,
        PROGRESSION_ADVICE,
        PERSONAL_ADVICE,
        TACTICS_ADVICE,
        PRINCIPLE_ADVICE,
    }
}
