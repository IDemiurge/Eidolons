package main.data.dialogue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 5/17/2017.
 */
public class Speeches implements SpeechInterface{
    List<SpeechInterface> list;

    public Speeches(SpeechInterface... array) {
        this.list = new LinkedList<>(Arrays.asList(array));;
    }

    public List<SpeechInterface> getList() {
        return list;
    }
}
