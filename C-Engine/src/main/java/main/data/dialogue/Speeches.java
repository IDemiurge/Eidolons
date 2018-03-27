package main.data.dialogue;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Speeches implements SpeechInterface{
    List<SpeechInterface> list;

    public Speeches(SpeechInterface... array) {
        this.list = new ArrayList<>(Arrays.asList(array));
    }

    public List<SpeechInterface> getList() {
        return list;
    }
}
