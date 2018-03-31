package eidolons.game.battlecraft.logic.meta.party;

import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.HeroEnums.PERSONALITY;

import java.util.List;

/**
 * Created by JustMe on 5/30/2017.
 * Maybe use Trust/Esteem/Affection/…?
 * How is status determined then?
 * How will it change during campaign?
 * <p>
 * fighting together
 * speeches
 * scripted events
 * temporal ?
 */
public class RelationsMaster {

    public void impact(RELATIONS_IMPACT_TYPE type, Unit source, Unit target) {
//        effect = new RelationsEffect(type);

        List<PERSONALITY> personalityList = getPersonalityTraits(target);
        PERSONALITY personality = null;
//random for dominant personality
        SITUATION situation = null;
        boolean favorable = isFavorable(situation, type, personality);

    }

    private List<PERSONALITY> getPersonalityTraits(Unit target) {
        return null;
    }

    boolean isFavorable(SITUATION situation, RELATIONS_IMPACT_TYPE type, PERSONALITY personality) {
//       reverse = situation.isReverseEffect();
//       increase
        switch (type) {
            case SLIGHT:

        }
        return false;
    }

    //    public enum PERSONALITY_PERKS{
//HAUGHTY,
//        ARROGANT,
//        JOVIAL,
//    }
    public enum RELATIONS_IMPACT_TYPE {
        SLIGHT,
        INSULT,
        ANNOYANCE,


        FAVOR,
        GIFT,
        GREED,
        WEAKNESS,
        HEROIC_DEED,
        CONFLICT,
        COMBAT_HELP
    }

    public enum RELATIONS_STATUS {
        BONDED, //refuse to separate
        FRIENDLY,
        NORMAL,
        STRAINED,
        IRRECONCILABLE, //refuse to fight together
    }

    public enum SITUATION {

    }
}
