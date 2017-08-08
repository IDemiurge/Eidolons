package main.game.battlecraft.ai.advanced.machine;

import main.game.battlecraft.ai.UnitAI;
import main.system.auxiliary.RandomWizard;

import java.util.List;

/**
 * Created by JustMe on 8/1/2017.
 */
public class ProfileMutator {

    private static PROFILE_MUTATION mutationType=PROFILE_MUTATION.DEFAULT;

    private static void mutate(PriorityProfile profile, PROFILE_MUTATION mutation) {
        final float mutationModifierMax = 1.1f;
        final float mutationModifierMin = 0.9f;
        profile.getMap().keySet().forEach(key -> {
            float value = profile.getMap().get(key);
            float mutationModifier = new Float(RandomWizard.getRandomIntBetween(
             (int) mutationModifierMin * 100,
             (int) mutationModifierMax * 100)) / 100;
            value = value * mutationModifier;

            profile.getMap().put(key, value);
        });
    }

    public static void mutate(PriorityProfile profile, UnitAI ai) {
        for (PROFILE_MUTATION mutation : getMutations(ai)) {
            mutate(profile, mutation);
        }
    }

    private static List<PROFILE_MUTATION> getMutations(UnitAI ai) {

        return null;
    }

    public static PriorityProfile getMutated(PriorityProfile originalProfile) {
        PriorityProfile newProfile = new PriorityProfile(originalProfile);
        mutate(newProfile, mutationType);
        return newProfile;
    }

    public enum PROFILE_MUTATION {
        WEAK,
        DEFAULT,
        EXTREME,

    }
}
