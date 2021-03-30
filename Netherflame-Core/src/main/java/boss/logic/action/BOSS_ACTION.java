package boss.logic.action;

import main.system.auxiliary.StringMaster;

public interface BOSS_ACTION {

    default String getName() {
        return StringMaster.format(toString());
    }
}
