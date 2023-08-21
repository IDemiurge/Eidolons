package apps;

import logic.calculation.AttackCalc;

import javax.swing.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static apps.JOption.*;

/**
 * Created by Alexander on 8/1/2023
 *
 * Keeping state
 * Info msgs
 * >> Maybe we can simulate player choices this way - so we will go thru it together w/ ilya
 *
 * damage of unit vs unit - with factors? Well yeah, better not keep any state - just raw unit stats
 * so this app should guide us thru the attack logic and get/assign all numbers?
 * Info about units?
 * What is separate random for? New turn
 *
 * How to align this with future logic code?
 * In a nutshell, we are just taking input from user... otherwise it's a valid atk calc!
 */
public class HelperApp {
    public static void main(String[] args) {
        Object[] options= {
                "Attack", //Then we must have that much coded - full unit and atk stats.
                //Q: Wards?
                "New Round", //flame => strategy phase <???> => initiative
                "Info", //units overview and in-depth examine
                "Exit"
        };
        Icon icon=null;
        loop: while (true){

        int option = pickIndex(options);
            switch (option) {
                case 0: attack(); break;
                case 1: newRound(); break;
                case 2: info(); break;
                case 3: break loop;
            }
        }
    }

    private static void attack() {
        Map<String, PickType> picks = new LinkedHashMap<>();
        picks.put("Attacker", PickType.UnitType);
        picks.put("Target", PickType.UnitType);
        picks.put("PWR or STD?", PickType.Bool);
        picks.put("Factors", PickType.Factors); // all in 1 line, so up to ... 10 or 12 max
        Map<String, Object> pickResults = multiPick(picks);
        String resultMessage= AttackCalc.calculate(pickResults);

        // JOptionPane.showMessageDialog(null, resultMessage);
       //as stringBuilder?
        // pick("Attacker", unitSet).pick

        //logic: atker, atked, std/pwr, factors
    }
    private static void newRound() {

    }
    private static void info() {

    }


}
