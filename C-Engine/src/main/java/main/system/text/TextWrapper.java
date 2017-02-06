package main.system.text;

import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TextWrapper {

    public static String[] wrapIntoArray(String text, int wrapLength) {
        return WordUtils.wrap(text, wrapLength, StringMaster.NEW_LINE, true).split(StringMaster.NEW_LINE);
    }

    public static List<String> wrap(String text, int wrapLength) {
        return wrap(text, wrapLength, null);
    }

    public static List<String> wrap(String text, int wrapLength, Ref ref) {
        if (StringMaster.isEmpty(text)) {
            return new LinkedList<>();
        }
        text = TextParser.parse(text, ref);

        if (text.contains(StringMaster.NEW_LINE)) {
            LinkedList<String> list = new LinkedList<>();
            for (String subString : text.split(StringMaster.NEW_LINE)) {
                list.addAll(Arrays.asList(WordUtils.wrap(subString, wrapLength,
                        StringMaster.NEW_LINE, true).split(StringMaster.NEW_LINE)));
            }
            return list;
        }

        return new LinkedList<>(Arrays.asList(WordUtils.wrap(text, wrapLength,
                StringMaster.NEW_LINE, true).split(StringMaster.NEW_LINE)));
    }

}
