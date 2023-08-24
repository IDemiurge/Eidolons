package apps.math;

import javax.swing.*;
import java.util.Random;

/**
 * Created by Alexander on 6/6/2023
 */
public class RollUtil {
    public static void main(String[] args) {
        while (true) {
            try {
                int min = Integer.valueOf(JOptionPane.showInputDialog("Min?"));
                int max = Integer.valueOf(JOptionPane.showInputDialog("Max?"));
                int result = new Random().nextInt(max - min) + min;
                int input = JOptionPane.showConfirmDialog(null, "Rolled " + result);
                if (input == JOptionPane.CANCEL_OPTION)
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
