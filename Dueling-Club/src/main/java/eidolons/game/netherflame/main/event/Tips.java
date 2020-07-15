package eidolons.game.netherflame.main.event;

import eidolons.game.netherflame.main.event.text.PuzzleTip;
import eidolons.game.netherflame.main.event.text.TIP;
import eidolons.game.netherflame.main.event.text.TextEvent;
import eidolons.game.netherflame.main.event.text.TxtTip;
import main.system.auxiliary.EnumMaster;

public class Tips {
    public static TextEvent getTipConst(String substring) {
        TextEvent tip = new EnumMaster<TIP>().retrieveEnumConst(TIP.class, substring);
        if (tip != null) {
            return tip;
        }
        tip = new EnumMaster<TxtTip>().retrieveEnumConst(TxtTip.class, substring);
        if (tip != null) {
            return tip;
        }
        tip = new EnumMaster<PuzzleTip>().retrieveEnumConst(PuzzleTip.class, substring);
        return tip;
    }
}
