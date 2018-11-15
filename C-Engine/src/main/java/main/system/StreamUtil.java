package main.system;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/20/2018.
 */
public class StreamUtil<T> {

    public List<T> filter(List<T> list, Predicate<T> predicate){
        return list.stream().filter(predicate).collect(Collectors.toList());
    }
}
