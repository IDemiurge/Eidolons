package eidolons.macro.global.time;

import eidolons.macro.MacroGame;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;

public class TimeMaster {

    private static final int DEFAULT_HOURS_PER_TURN = 12;
    private static GameDate date;

    public static int getHoursPerTurn() {
        return DEFAULT_HOURS_PER_TURN;
    }


    public static GameDate getDate() {
        if (date == null) {
            date = new GameDate();
            date.setHour(1);
            if (MacroGame.getGame() != null) {
//            date.setEra(MacroGame.getGame().getCampaign()
//             .getIntParam(MACRO_PARAMS.ERA));
//            date.setYear(MacroGame.getGame().getCampaign()
//             .getIntParam(MACRO_PARAMS.YEAR));
//            date.setMonthNumber(MacroGame.getGame().getCampaign()
//             .getIntParam(MACRO_PARAMS.MONTH));
//            date.setDay(MacroGame.getGame().getCampaign()
//             .getIntParam(MACRO_PARAMS.DAY));
            }
            date.setDayTime(DAY_TIME.values[0]);
        }
        return date;
    }

}
