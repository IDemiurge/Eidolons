package narrative.ink.gen;

import narrative.ink.InkEnums.INK_STD_CONTEXT;
import narrative.ink.logic.NpcProfile;
import narrative.ink.logic.TeaMaster.NPC_PROFESSION;
import narrative.ink.logic.TeaMaster.PERSONALITY_TAG;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 11/24/2018.
 *
 * use weightMaps
 *
 * how to achieve 80% realistic +20% fun results?
 *
 *
 *
 */
public class NpcGenerator {

    public NpcProfile generateProfile(NPC_PROFESSION profession, INK_STD_CONTEXT context  ){
//        Set<PERSONALITY_TAG> tags= new LinkedHashSet<>();
//                while (tags.size()<=maxTags){
//                    PERSONALITY_TAG tag = getTag(profession, context);
//                    tags.add(tag);
//                }
//        return new NpcProfile(profession, mood, t, e, a, tags);
        return null;
    }

    private PERSONALITY_TAG getTag(NPC_PROFESSION profession, INK_STD_CONTEXT context) {
        // merge maps
        WeightMap<PERSONALITY_TAG> map1 = getProfessionTagMap(profession);
        WeightMap<PERSONALITY_TAG> map2 = getContextTagMap(context);
        return map1.merge(map2).getRandomByWeight();
    }

    private WeightMap<PERSONALITY_TAG> getContextTagMap(INK_STD_CONTEXT context) {
        return new WeightMap<>();
    }

    private WeightMap<PERSONALITY_TAG> getProfessionTagMap(NPC_PROFESSION profession) {
        WeightMap<PERSONALITY_TAG> map = new WeightMap<>();
        switch (profession) {
            case Bard:
                map.chain(PERSONALITY_TAG.ARROGANT, 10);
        }
        return map;
    }


}
