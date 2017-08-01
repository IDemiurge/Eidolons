package main.game.battlecraft.ai.advanced.machine;

import main.data.XLinkedMap;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.system.auxiliary.data.MapMaster;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/1/2017.
 */
public class PriorityProfileManager extends AiHandler {
    PriorityProfile priorityProfile;

    public PriorityProfileManager(AiMaster master) {
        super(master);
        priorityProfile = getPriorityProfile(null);

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


    public PriorityProfile mutate(PriorityProfile originalProfile) {
        Map<AiConst, Float> map = originalProfile.getMap();
        LinkedHashMap<AiConst, Float> clone = new MapMaster<AiConst, Float>().cloneLinkedHashMap(map);
//   TODO      mutateMap(clone);
        PriorityProfile newProfile = new PriorityProfile(clone);
        return newProfile;
    }

    public PriorityProfile createProfile(Map<AiConst, Float> map) {

        return new PriorityProfile(map);
    }
}
