package main.system.math;

import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.system.SortMaster;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Created by JustMe on 4/16/2017.
 */
public class FuncMaster<T> {
    public  Entity getGreatestValue
     (List<? extends Entity> list, PARAMETER p) {
        return getGreatest(list, entity -> entity.getIntParam(p)); // TODO reverse w/o minus
    }

    public  Entity getGreatest
     (List<? extends Entity> list, Function<Entity, Integer> function) {
        list.sort(SortMaster.getSorterByExpression(function));
        return list.get(0);
    }

    public  Integer total
     (List<? extends T> list, ToIntFunction<T> function) {
        return list.stream().mapToInt(wrapFunction(function)).sum();
    }

    private  ToIntFunction<? super T> wrapFunction(ToIntFunction<T> function) {
        return t -> {
            try {
                return function.applyAsInt(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

    public  Integer getGreatestValue
     (List<? extends Entity> list, Function<Entity, Integer> function) {
        return function.apply(getGreatest(list, function));
    }
}
