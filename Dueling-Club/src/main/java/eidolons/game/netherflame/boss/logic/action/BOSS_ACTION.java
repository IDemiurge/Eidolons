package eidolons.game.netherflame.boss.logic.action;

import main.system.auxiliary.StringMaster;

public interface BOSS_ACTION {

    default String getName() {
        return StringMaster.getWellFormattedString(toString());
    }
}
