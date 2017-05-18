package main.data.dialogue;

/**
 * Created by JustMe on 5/17/2017.
 * DO NOT RENAME!
 */
public class Dialogue_Data {
    SpeechInterface root;
    SpeechData data;

    public Dialogue_Data(SpeechInterface root) {
        this.root = root;
    }

    public Dialogue_Data(SpeechInterface root, SpeechData data) {
        this.root = root;
        this.data = data;
    }
}
