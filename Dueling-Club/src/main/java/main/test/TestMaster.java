package main.test;

public class TestMaster {

    private static DebugProfile profile;

    // public enum BOOL_TEST_PARAMS {
    // SUBLEVEL_FREEZE_ON,
    // }
    // public enum INT_TEST_PARAMS {
    // }
    // public enum STRING_TEST_PARAMS {
    // }

    public static void initProfile() {
        profile = new DebugProfile();
        profile.setValue(TEST_PARAMS.SUBLEVEL_FREEZE_ON, "TRUE");
    }

    public static boolean isSublevelFreezeOn() {
        return getProfile().getBooleanValue(TEST_PARAMS.SUBLEVEL_FREEZE_ON);
    }

    public static DebugProfile getProfile() {
        if (profile == null)
            initProfile();
        return profile;
    }

    public static void setProfile(DebugProfile profile) {
        TestMaster.profile = profile;
    }

    public enum TEST_PARAMS {
        SUBLEVEL_FREEZE_ON
    }

}
