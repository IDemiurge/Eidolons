package apps.rules;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Alexander on 5/28/2023
 */
public class RandomPos {
    private static Map<Integer, String> map;
    static String[] cells = {
            "Front R",
            "Front C",
            "Front L",
            "Back R",
            "Back C",
            "Back L",
            "Flank R",
            "Rear",
            "Flank L",
    };
    static {
        map = new HashMap<>();
        int i = 0;
        for (String cell : cells) {
            map.put(i++, cell);
        }
    }
    public static void main(String[] args) {
        while (true) {
            // String input = JOptionPane.showInputDialog("input");
            // int max = Integer.parseInt(input);
            int max = 9 * 2 + 2;
            //are the chances all equal or weighed?
            int i = new Random().nextInt(max);
            String output = getOutput(i);
            int i1 = JOptionPane.showConfirmDialog(null, output);
            if (i1 == JOptionPane.CANCEL_OPTION)
                return;
            //can symbols drop on empty cells?!
        }
    }

    private static String getOutput(int i) {
        StringBuilder builder = new StringBuilder();
        String s = "";
        if (i >= 18) {
            return i == 18 ? "Vanguard LEFT" : "Vanguard RIGHT";
        } else
            s = (i >= 9) ? "Temple" : "Sylvan";
        builder.append(s + " > ");
        s = map.get(i % 9);
        builder.append(s);
        return builder.toString();
    }
}
