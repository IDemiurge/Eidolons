package elements.stats;

import elements.stats.generic.Stat;

/**
 * Created by Alexander on 8/2/2023
 */
public enum ActionProp implements Stat {
        Exec_data {
                @Override
                public String getName() {
                        return "exec data";
                }
        }
}
