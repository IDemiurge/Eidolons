package main.game.battlecraft.ai.advanced.machine;

import main.game.battlecraft.ai.UnitAI;
import main.system.auxiliary.data.MapMaster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 8/1/2017.
 */
public class ProfileMutator {

    private void mutate(PriorityProfile profile, PROFILE_MUTATION mutation) {

    }

    public void mutate(PriorityProfile profile, UnitAI ai) {
        for (PROFILE_MUTATION mutation : getMutations(ai)) {
            mutate(profile, mutation);
        }
    }

    private List<PROFILE_MUTATION> getMutations(UnitAI ai) {

        return null;
    }

    public PriorityProfile getMutated(PriorityProfile originalProfile) {
        Map<AiConst, Float> map = originalProfile.getMap();
        LinkedHashMap<AiConst, Float> clone = new MapMaster<AiConst, Float>().cloneLinkedHashMap(map);
//   TODO      mutateMap(clone);
        PriorityProfile newProfile = new PriorityProfile(clone);
        return newProfile;
    }

    public enum PROFILE_MUTATION {

    }
}
