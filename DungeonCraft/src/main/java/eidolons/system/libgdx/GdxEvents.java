package eidolons.system.libgdx;

public class GdxEvents {


    public static void tooltip(String description) {
        GdxAdapter.getInstance().getEventsAdapter().tooltip(description);
    }
}
