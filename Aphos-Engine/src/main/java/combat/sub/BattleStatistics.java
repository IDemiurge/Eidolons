package combat.sub;

import framework.data.statistics.Result;
import logic.calculation.damage.DamageResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 *
 * Sometimes we're using those result objects to perform further actions/calc
 *
 */
public class BattleStatistics {
    Map<Class, List<Result>> map= new HashMap<>();

    public Result getLast(Class clazz){
        return system.ListMaster.getLast(map.get(clazz));
    }

    public void add(Result result) {
        List<Result> list = map.get(result.getClass());
        if (list == null) {
            map.put(result.getClass(), list = new ArrayList<>());
        }
        list.add(result);
    }

    public void report(){

    }
}
