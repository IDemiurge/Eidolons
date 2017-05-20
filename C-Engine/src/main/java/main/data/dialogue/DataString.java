package main.data.dialogue;

import main.data.ability.OmittedConstructor;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DataString {

    public enum SPEECH_VALUE {
        SFX,
        SOUND,
        MUSIC,
        ACTOR,
        ACTORS,

    }
    SPEECH_VALUE type;
    //idea - use it in generic way, Object type and any n of constructors
    String data;
    String enumName;

    public DataString(String enumName, String data) {
        this.enumName = enumName;
        this.data = data;
    }
    public DataString(SPEECH_VALUE type, String data) {
        this.type = type;
        this.data = data;
    }
@OmittedConstructor
    public DataString() {
    }

    public SPEECH_VALUE getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getEnumName() {
        return enumName;
    }
}
