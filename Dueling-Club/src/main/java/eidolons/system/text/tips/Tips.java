package eidolons.system.text.tips;

import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import main.system.auxiliary.EnumMaster;

public class Tips {
    public static TextEvent getTipConst(String substring) {
        TextEvent tip = new EnumMaster<TIP>().retrieveEnumConst(TIP.class, substring);
        if (tip != null) {
            return tip;
        }
        tip = new EnumMaster<TxtTip>().retrieveEnumConst(StdTips.class, substring);
        if (tip != null) {
            return tip;
        }
        tip = new EnumMaster<PuzzleTip>().retrieveEnumConst(PuzzleTip.class, substring);
        return tip;
    }

    public static TextEvent getPuzzleTipConst(Boolean win_fail_intro, PuzzleEnums.puzzle_type type, String suffix) {
        String arg = "intro";
        if (win_fail_intro != null) {
            arg = win_fail_intro ? "win" : "defeat";
        }
        String substring = type +"_puzzle_" + arg;
        if (suffix.isEmpty()) {
            substring+= "_" + suffix;
        }

        PuzzleTip tip = new EnumMaster<PuzzleTip>().retrieveEnumConst(PuzzleTip.class, substring);
        return tip;
    }
}
