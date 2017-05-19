package main.data.dialogue;

/**
 * Created by JustMe on 5/17/2017.
 */
public class SpeechData extends DataString{

    private DataString[] strings;

    public SpeechData(DATA_TYPE type, String data) {
        super(type, data);
    }

    public SpeechData(DataString... strings) {
        this.strings = strings;
    }
    public SpeechData(DataString  string , DataString  string2)
    {
        this.strings = new DataString[]{
         string, string2
        };

    }
}
