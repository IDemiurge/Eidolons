package system.math;

import main.content.enums.GenericEnums;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Alexander on 10/24/2023
 */
public class Rolls {
    public static final  Map<GenericEnums.DieType, Integer> fixMap = new HashMap<>();
    public static int roll(GenericEnums.DieType die) {
        if (fixMap.containsKey(die)){
            Integer fixed = fixMap.remove(die);
            return fixed;
        }
        int i = new Random().nextInt(die.value);
        return i+1;
    }
        public static Boolean roll(int die, int failureOffset, int successOffset) {
        int i = new Random().nextInt(die);
        if (i >= die - successOffset - 1)
            return true;
        if (i <= failureOffset)
            return false;
        return null;
    }

    public static void setNext(GenericEnums.DieType die, int value) {
        fixMap.put(die, value);
    }
}
