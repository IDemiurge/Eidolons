package eidolons.libgdx.utils;

public class GdxTimeMaster {

    boolean[] isPeriodCache; //will it be faster really? :)
    //from 2 to 10 at least

    public static float uptime;
    public static float fps;
    public static float lowestFps = 0;
    public static long frame = 0;

    public static void init() {

    }

    public static boolean isPeriodNow(int period) {

        return frame % period == 0;
    }

    public static void act(float delta) {
        if (1f / delta < lowestFps) {
            lowestFps = 1f / delta;
        }
        frame++;
        uptime += delta;
//        isPeriodCache = new Boolean[10];
    }

    public static float getAvrgFps() {
        return uptime / frame;
    }
}
