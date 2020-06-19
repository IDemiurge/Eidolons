package main.system.math;

import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.system.SortMaster;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Created by JustMe on 4/16/2017.
 */
public class FuncMaster<T> {
    public static Entity getGreatestValueEntity
     (List<? extends Entity> list, PARAMETER p) {
        return getGreatestEntity(list, entity -> entity.getIntParam(p)); // TODO reverse w/o minus
    }

    public static Entity getGreatestEntity
     (List<? extends Entity> list, Function<Entity, Integer> function) {
        list.sort(SortMaster.getEntitySorterByExpression(function));
        return list.get(0);
    }

    public static Object getGreatest
     (List<? extends Object> list, Function<Object, Integer> function) {
        list.sort(SortMaster.getSorterByExpression(function));
        return list.get(0);
    }

    public static Integer getGreatestValueEntity
     (List<? extends Entity> list, Function<Entity, Integer> function) {
        return function.apply(getGreatestEntity(list, function));
    }

    public T getGreatest_
     (List<? extends T> list, Function<T, Integer> function) {
        list.sort(new SortMaster<T>().getSorterByExpression_(function));
        return list.get(0);
    }

    public Integer total
     (Collection<? extends T> list, ToIntFunction<T> function) {
        return list.stream().mapToInt(wrapFunction(function)).sum();
    }

    private ToIntFunction<? super T> wrapFunction(ToIntFunction<T> function) {
        return t -> {
            try {
                return function.applyAsInt(t);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            return 0;
        };
    }
}
