package eidolons.game.module.herocreator.logic.generic;

import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by JustMe on 12/7/2018.
 *
 * examples of usage:
 * Library with [SCHOOLS] needs N spells that each have a [SCHOOL]
 *
 *
 * Eidolon item traits
 *
 */
public class RepertoireManager<T, E> {

    // should never fail - we'll rely on this...
    public List<T> select(int max, int min, int preferred,
                                 Set<T> pool, int maxValue, float buffer,
                                 Function<Pair<T, E>, Integer> mapFunction,
                                 Function<T, Integer> valueFunction,
                                 WeightMap<E> weightMap, float randomness, boolean neverRepeat
    ,boolean recursion){
        int value = 0;
        int n = 0;
        List<T> result=    new ArrayList<>() ;
        Set<T> originalPool = new LinkedHashSet<>(pool);
        while(value<=maxValue&& n<max){
            E e = weightMap.getRandomByWeight();
            float greater=0;
            T pick = null;
            for (T t :     new ArrayList<>(pool)) {
                if (!check(t, value, maxValue, valueFunction))
                {
                    pool.remove(t);
                    continue;
                }
                Integer val =
                 mapFunction.apply(new ImmutablePair<>(t, e));
                float compare = RandomWizard.getRandomInt(val) * randomness
                 + val * (1 - randomness);
                if (compare>greater)
                {
                    greater = compare;
                    pick = t;
                }
            }
            if (pick == null) {
                //what to do if pref not achieved?
                //retry, at first
                //then use some buffer size
                if (n>=preferred){
                    break;
                }
                if (recursion)
                    return null;
                Loop retryLoop= new Loop(getRetryAttempts(originalPool, preferred));
                while(retryLoop.continues()){
                    List<T> list = select(max, min, preferred, pool, maxValue, buffer,
                     mapFunction, valueFunction, weightMap, randomness, neverRepeat, true);
                    if (list != null)
                    if (list.size()>=preferred) {
                        return list;
                    }
                }
                retryLoop= new Loop(getRetryAttempts(originalPool, preferred));
                while(retryLoop.continues()){
                    List<T> list = select(max, min, preferred, pool, maxValue, buffer,
                     mapFunction, valueFunction, weightMap, randomness, neverRepeat, true);
                    if (list.size()>=min) {
                        return list;
                    }
                }
                //apply buffer to max value
            }
            result.add(pick);
            if (neverRepeat)
                pool.remove(pick);
            n++;
            value += valueFunction.apply(pick);
        }



        return result;
    }

    private int getRetryAttempts(Set<T> originalPool, int preferred) {
        return originalPool.size() / preferred;
    }

    private boolean check(T t, int value, int maxValue, Function<T, Integer> valueFunction) {
        return maxValue <= value + valueFunction.apply(t);
    }


}
