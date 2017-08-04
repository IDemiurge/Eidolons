package main.game.battlecraft.ai.advanced.machine;

import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 8/1/2017.
 */
public class PriorityProfileManager extends AiHandler {
    //    PriorityProfile priorityProfile;
    Map<ObjType, PriorityProfile> preferredProfilesMap = new HashMap<>();
    Map<UnitAI, PriorityProfile> profilesMap= new HashMap<>();
    ProfileMutator mutator;

    public PriorityProfileManager(AiMaster master) {
        super(master);

    }

    public PriorityProfile getPriorityProfile(UnitAI ai) {
        PriorityProfile profile = profilesMap.get(ai.getUnit().getType());
        if (profile == null) {
            profile =
             preferredProfilesMap.get(ai.getUnit().getType());
//             getPriorityProfile(null);
            mutator.mutate(profile, ai); // ???
            profilesMap.put(ai, profile);
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

    public PriorityProfile createProfile(Map<AiConst, Float> map) {
        return new PriorityProfile(map);
    }
    public void setPriorityProfile(ObjType trainee, PriorityProfile profile) {
        preferredProfilesMap.put(trainee, profile);
    }

    public PriorityProfile getProfile() {
        return getPriorityProfile();
    }

    public PriorityProfile getPriorityProfile() {
        return getPriorityProfile(getUnit().getAI());
    }



}
