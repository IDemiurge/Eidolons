package main.system.launch;

public class LaunchModel {
    LAUNCH_PHASE phase;
    LAUNCH_STEP step;
    long timeElapsed;

    public enum LAUNCH_PHASE{
        SYSTEM,
        CONTENT,
        DATA,
        CLASSES,
        AFTER, ;
    }
    public enum LAUNCH_STEP{
XmlRead, ClassMapping,
        DungeonBuilding,
        GameInit,
        GameStart,

    }

    public void log(){

    }
}
