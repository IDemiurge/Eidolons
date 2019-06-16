package main.content.mode;

import main.content.enums.system.AiEnums;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public interface MODE {
    String getBuffName();

    void setBuffName(String buffName);

    String getParameter();

    void setParameter(String parameter);

    String getFormula();

    void setFormula(String formula);

    boolean isDispelOnHit();

    void setDispelOnHit(boolean dispelOnHit);

    boolean isEndTurnEffect();

    void setEndTurnEffect(boolean endTurnEffect);

    boolean isDisableCounter();

    void setDisableCounter(boolean disableCounter);

    boolean isDisableActions();

    void setDisableActions(boolean disableActions);

    boolean isOnActivateEffect();

    void setOnActivateEffect(boolean onActivateEffect);

    boolean isOnDeactivateEffect();

    void setOnDeactivateEffect(boolean onDeactivateEffect);

    boolean isContinuous();

    void setContinuous(boolean continuous);

    int getDefenseMod();

    void setDefenseMod(int defenseMod);

    STANDARD_EVENT_TYPE getRemoveEvent();

    void setRemoveEvent(STANDARD_EVENT_TYPE removeEvent);

    boolean isBehavior();

    String getParameterMods();

    String getParameterBoni();

    String getPropsAdded();

    boolean isWatchSupported();

    String getPeriod();

    String getPeriodicValues();

    Integer getDuration();

    String getImagePath();

    AiEnums.BEHAVIOR_MODE getBehaviorMode();
}
