package main.game.battlecraft.ai.advanced.machine;

import main.data.XLinkedMap;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by JustMe on 8/1/2017.
 */
public class PriorityProfileManager extends AiHandler {
    PriorityProfile priorityProfile;
    Map<UnitAI, PriorityProfile> preferredProfilesMap;
    ProfileMutator mutator;

    public PriorityProfileManager(AiMaster master) {
        super(master);
        priorityProfile = getPriorityProfile(null);

    }

    public PriorityProfile getPreferredPriorityProfile(UnitAI ai) {
        PriorityProfile profile = preferredProfilesMap.get(ai);
        if (profile == null) {
            profile = getPriorityProfile(null);
            mutator.mutate(profile, ai);

            preferredProfilesMap.put(ai, profile);
        }
        return profile;
    }


    public PriorityProfile getPriorityProfile(float[] array) {
        Map<AiConst, Float> map = new XLinkedMap<>();
        if (array != null) {
            int i = 0;
            for (AiConst c : AiConst.values()) {
                float value = array[i];
                map.put(c, value);
                i++;
            }
        } else {
            Arrays.stream(AiConst.values()).forEach(c -> {
                float value = c.getDefValue();
                map.put(c, value);
            });
        }

        return createProfile(map);
    }

    public void setPriorityProfile(float[] array) {
        setPriorityProfile(getPriorityProfile(array));
    }

    public PriorityProfile getProfile() {
        return getPriorityProfile();
    }

    public PriorityProfile getPriorityProfile() {
        return priorityProfile;
    }

    public void setPriorityProfile(PriorityProfile priorityProfile) {
        this.priorityProfile = priorityProfile;
    }


    public PriorityProfile createProfile(Map<AiConst, Float> map) {

        return new PriorityProfile(map);
    }
}
