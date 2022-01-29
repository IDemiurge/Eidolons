package main.system;

import java.util.Comparator;
import java.util.function.Function;

public class Sort<T> {
public Comparator<T> getIntSorter(Function<T, Integer> func){
    return new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            Integer res1 = func.apply(o1);
            Integer res2 = func.apply(o2);
            if (res1>res2)
                return 1;
            if (res1 < res2)
                return -1;
            return 0;
        }
    };
}
}
