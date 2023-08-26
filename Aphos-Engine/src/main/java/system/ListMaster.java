package system;

import framework.data.statistics.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 8/25/2023
 */
public class ListMaster {

    public static <T> T getLast(List<T> results) {
        if (results.isEmpty())
            return null;
        return results.get(results.size()-1);
    }
}
