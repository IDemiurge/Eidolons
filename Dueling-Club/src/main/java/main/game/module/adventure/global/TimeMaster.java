package main.game.module.adventure.global;

import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.content.values.parameters.MACRO_PARAMS;
import main.game.module.adventure.MacroGame;

public class TimeMaster {

    private static final int DEFAULT_HOURS_PER_TURN = 12;
    private static GameDate date;
    private static Campaign campaign;

    public static int getHoursPerTurn() {
        return DEFAULT_HOURS_PER_TURN;
    }

	/*
     * day/night
	 * 
	 * make 'turns' with various parties, moving them around and triggering
	 * events
	 * 
	 * Time Factor -
	 * 
	 * Macro Turn 1) Provisions 2) Fatigue/wounds/...
	 */

    public static void setCampaign(Campaign campaign_) {
        campaign = campaign_;
    }

    public static void turnEnded() {
        campaign.setParameter(MACRO_PARAMS.HOURS_INTO_TURN, 0);
        date.nextTurn();
        // date.setHour(0); // offset = 6, right?
        // date.setDay_or_night(!date.isDay_or_night());
    }

    public static boolean hoursPassed(int hours) {
        campaign.modifyParameter(MACRO_PARAMS.HOURS_INTO_TURN, hours);

        if (campaign.getIntParam(MACRO_PARAMS.HOURS_INTO_TURN) > getHoursPerTurn()) {
            campaign.modifyParameter(MACRO_PARAMS.HOURS_INTO_TURN,
                    -getHoursPerTurn());
            return false;
        }
        Integer hour = campaign.getIntParam(MACRO_PARAMS.HOURS_INTO_TURN);
        date.setHour(hour);
        date.setDay_or_night(!date.isDay_or_night());
        return true;

    }

    public static GameDate getDate() {
        if (date == null) {
            date = new GameDate();
            date.setHour(1);
            date.setEra(MacroGame.getGame().getCampaign()
                    .getIntParam(MACRO_PARAMS.ERA));
            date.setYear(MacroGame.getGame().getCampaign()
                    .getIntParam(MACRO_PARAMS.YEAR));
            date.setMonthNumber(MacroGame.getGame().getCampaign()
                    .getIntParam(MACRO_PARAMS.MONTH));
            date.setDay(MacroGame.getGame().getCampaign()
                    .getIntParam(MACRO_PARAMS.DAY));
            date.setDayTime(DAY_TIME.values[0]);
        }
        return date;
    }

    public static int hoursLeft() {
        return getHoursPerTurn()
                - campaign.getIntParam(MACRO_PARAMS.HOURS_INTO_TURN);
    }
}
