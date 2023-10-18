package system;

import main.system.datatypes.WeightMap;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.*;

/**
 * Created by Alexander on 9/2/2023
 */
public class CollectionsX {
    public static <T> T getRandomElement(List<T> array, double firstToLastRatio) {
        Random rand = new Random();
        int n = array.size();

        // Calculate the total weight using the arithmetic sequence formula: n/2 * (first + last)
        double totalWeight = (double) n / 2 * (1 + firstToLastRatio);

        // Generate a random number between 0 and totalWeight
        double randomWeight = rand.nextDouble() * totalWeight;

        // Determine which element to return
        double cumulativeWeight = 0.0;
        for (int i = 0; i < n; i++) {
            double currentWeight = 1 + i * (firstToLastRatio - 1) / (n - 1);
            cumulativeWeight += currentWeight;
            if (randomWeight <= cumulativeWeight) {
                return array.get(i);
            }
        }
        return null ;  // This should not happen if the algorithm is correct
    }
    @Test
    public void test(){
        Map<String, Integer> map = new LinkedHashMap<>();
        Integer[] array = {1, 2, 3, 4, 5,6, 7, 8, 9, 10};
        for (int s : array) {
            map.put(s+"", 0);
        }
        for (int i = 0; i < 10000; i++) {
            String random = String.valueOf(getRandomElement(Arrays.asList(array), .3));
            map.put(random, map.get(random)+1);
        }
        System.lineSeparator();
    }

    @Deprecated
    //does a pretty weird
    public static String getRandomWithOrderPriority(Collection<String> strings) {
       // if (asc)
        List<String> list = new ArrayList<>(strings);
        Collections.reverse(list);
        strings = list;
        //create a weight map?

        //at what percentile is this element?
        //how exactly does percentage grow?
        float average_chance = new Float(100/(strings.size())); // e.g. 3.33f for 30 elements

        float chance = average_chance/2; //min chance
        //
        WeightMap<String> weightMap = new WeightMap<>();

        float increment = new Float((average_chance   - chance)) / (strings.size());
        //how to ensure that SOME element is returned? Then min/max map must cover 100 !
        for (String string : strings) {
            // if (percentile > last && percentile <= chance)
            //     return string;
            weightMap.put(string, Math.round(chance));
            chance += average_chance/4;
            chance += increment;

            // min = max;
            // max += increment;
        }
        // return RandomWizard.getRandomFromPercentMap(map);
        return weightMap.getRandomByWeight();
    }
}
