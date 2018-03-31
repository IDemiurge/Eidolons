package eidolons.game.battlecraft.ai.advanced.machine;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.machine.AiPriorityConstantMaster.AiConstant;
import eidolons.game.battlecraft.ai.advanced.machine.profile.ProfileChooser.AI_PROFILE_GROUP;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.data.XLinkedMap;
import main.entity.type.ObjType;

import java.util.*;

/**
 * Created by JustMe on 8/1/2017.
 */
public class PriorityProfileManager extends AiHandler {
    //    PriorityProfile priorityProfile;
    Map<String, PriorityProfile> preferredProfilesMap = new HashMap<>();
    Map<UnitAI, PriorityProfile> profilesMap = new HashMap<>();
    AI_PROFILE_GROUP group = AI_PROFILE_GROUP.UNIT_SPECIFIC;
    private PriorityProfile defaultProfile;

    public PriorityProfileManager(AiMaster master) {
        super(master);

    }

    public static List<PriorityProfile> initProfiles(int i) {
        PriorityProfileManager manager = new PriorityProfileManager(null);
        PriorityProfile original = manager.createNewProfile(null);
        List<PriorityProfile> list = new ArrayList<>();
        while (i > 0) {
            i--;
            list.add((PriorityProfile) original.mutate());
        }
        return list;
    }

    public PriorityProfile getPriorityProfile(UnitAI ai) {
        PriorityProfile profile = profilesMap.get(ai.getUnit().getType());
        if (profile == null) {
            profile =
             preferredProfilesMap.get(ai.getUnit().getType().getName());
//             getPriorityProfile(null);
            if (profile == null) {
                return getDefaultProfile();
            }
            profilesMap.put(ai, profile);
        }
        return profile;
    }

    private PriorityProfile getDefaultProfile() {
        if (defaultProfile == null) {
            defaultProfile = createNewProfile(null);
        }
        return defaultProfile;
    }

    public PriorityProfile createNewProfile(float[] array) {
        Map<AiConstant, Float> map = new XLinkedMap<>();
        if (array != null) {
            int i = 0;
            for (AiConst c : AiConst.values()) {
                float value = array[i];
                map.put(c, value);
                i++;
            }
        } else {
//            if (defaultArray==null ){
//            }
            Arrays.stream(AiConst.values()).forEach(c -> {
                float value = c.getDefValue();
                map.put(c, value);
            });
        }

        return createProfile(map);
    }

    public PriorityProfile createProfile(Map<AiConstant, Float> map) {
        PriorityProfile p = new PriorityProfile(map);
        p.setGroup(group);
        return p;
    }

    public void setPriorityProfile(ObjType trainee, PriorityProfile profile) {
        preferredProfilesMap.put(trainee.getName(), profile);
    }

    public PriorityProfile getProfile() {
        return getPriorityProfile();
    }

    public PriorityProfile getPriorityProfile() {
        return getPriorityProfile(getUnit().getAI());
    }

    public AI_PROFILE_GROUP getGroup() {
        return group;
    }

    public void setGroup(AI_PROFILE_GROUP group) {
        this.group = group;
    }

    public Map<UnitAI, PriorityProfile> getProfilesMap() {
        return profilesMap;
    }
}
