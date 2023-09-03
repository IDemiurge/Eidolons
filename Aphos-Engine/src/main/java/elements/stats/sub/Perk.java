package elements.stats.sub;

import elements.stats.UnitParam;

public enum Perk {
    Patience(UnitParam.Ap_retain),
    ;
    UnitParam param;

    Perk(UnitParam param) {
        this.param = param;
    }
}
