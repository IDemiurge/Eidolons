package main.system.auxiliary;

public class Loop {

    private static int counter;
    private int loopCounter;

    public Loop(int i) {
        loopCounter = i;
    }

    public static boolean loopContinues() {
        return !loopEnded();
    }

    public static void startLoop(int i) {
        counter = i;
    }

    public static boolean loopEnded() {
        counter -= 1;
        return counter <= 0;
    }

    public static int getCounter() {
        return counter;
    }

    public boolean continues() {
        return !ended();
    }

    public boolean ended() {
        loopCounter -= 1;
        return loopCounter < 0;
    }

    public void start(int i) {
        loopCounter = i;
    }

}
