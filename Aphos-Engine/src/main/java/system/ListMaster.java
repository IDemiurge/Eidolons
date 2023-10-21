package system;

import framework.data.statistics.Result;
import framework.entity.field.FieldEntity;
import main.system.auxiliary.RandomWizard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexander on 8/25/2023
 */
public class ListMaster {

    public static <T> T getLast(List<T> results) {
        if (results.isEmpty())
            return null;
        return results.get(results.size()-1);
    }

    //recursive? No nested so far
    public static String represent(Collection o) {
         //TODO
        return o.toString();
    }

    public static <T> List<T> getRandomElements(List<T> list, Integer n) {
        if (n>= list.size())
            return list;
        List<T> cropped = new ArrayList<>(list);
        Collections.shuffle(cropped);
        return cropped.subList(0, n);
    }

    public static <T> T getRandom(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = RandomWizard.getRandomIndex(list);
        return list.get(randomIndex);
    }
    public static <T> List<T> mergeToList(Collection<T>... collections) {
        return Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    public static <T> Set<T> mergeToSet(Collection<T>... collections) {
        return Stream.of(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
