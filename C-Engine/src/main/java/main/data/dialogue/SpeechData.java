package main.data.dialogue;

import main.data.dialogue.DataString.SPEECH_VALUE;
import main.system.data.DataUnit;

/**
 * Created by JustMe on 5/17/2017.
 */
public class SpeechData extends DataUnit<SPEECH_VALUE> {

    private DataString[] strings;

    public SpeechData(DataString... strings) {
        this.strings = strings;
        initValues();
    }

    public SpeechData(DataString string, DataString string2) {
        this(new DataString[]{
         string, string2
        });
        initValues();
    }

    private void initValues() {

    }


}
