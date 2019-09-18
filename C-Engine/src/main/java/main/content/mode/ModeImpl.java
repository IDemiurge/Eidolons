package main.content.mode;

import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

public class ModeImpl implements MODE {
    private STD_MODES template;
    private String buffName;
    private STANDARD_EVENT_TYPE removeEvent;
    private int defenseMod;
    private boolean onDeactivateEffect;
    private boolean onActivateEffect;
    private boolean disableActions;
    private boolean disableCounter;
    private boolean endTurnEffect;
    private boolean dispelOnHit;
    private String formula;
    private String parameter;
    private BEHAVIOR_MODE behaviorMode;
    private String parameterMods;
    private String parameterBoni;
    private String propsAdded;
    private boolean continuous;
    private Boolean watchSupport;

    public ModeImpl(STD_MODES template) {
        this.template = template;
        setRemoveEvent(template.getRemoveEvent());
        this.buffName = (template.getBuffName());
        setDefenseMod(template.getDefenseMod());
        setFormula(template.getFormula());
        setParameter(template.getParameter());
        setDisableActions(template.isDisableActions());
        setDisableCounter(template.isDisableCounter());
        setDispelOnHit(template.isDispelOnHit());
        setOnActivateEffect(template.isOnActivateEffect());
        setEndTurnEffect(template.isEndTurnEffect());
        setOnDeactivateEffect(template.isOnDeactivateEffect());
        setContinuous(template.isContinuous());
        this.parameterMods = (template.getParameterMods());
        this.parameterBoni = (template.getParameterBoni());
        this.propsAdded = (template.getPropsAdded());
    }

    public ModeImpl(BEHAVIOR_MODE behaviorMode) {
        this.behaviorMode = behaviorMode;
        template= behaviorMode.mode;
    }

    @Override
    public String toString() {
        if (behaviorMode != null) {
            return behaviorMode.getName();
        }
        if (buffName != null) {
            return buffName;
        }
        return template.getName();
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    @Override
    public boolean equals(Object mode) {
        if (mode instanceof STD_MODES) {
            return template == mode;
        }
        if (mode instanceof ModeImpl) {
            return template == ((ModeImpl) mode).getTemplate();
        }
        return false;

    }

    @Override
    public boolean isWatchSupported() {
        if (watchSupport == null) {
            return template.isWatchSupported();
        }
        return watchSupport;
    }

    @Override
    public Integer getDuration() {
        return template.getDuration();
    }

    @Override
    public String getImagePath() {
        return template.getImagePath();
    }

    @Override
    public String getPeriod() {
        return template.getPeriod();
    }

    @Override
    public String getPeriodicValues() {
        return template.getPeriodicValues();
    }

    @Override
    public String getParameterMods() {
        return parameterMods;
    }

    @Override
    public String getParameterBoni() {
        return parameterBoni;
    }

    @Override
    public String getPropsAdded() {
        return propsAdded;
    }

    public String getBuffName() {
        return buffName;
    }

    public void setBuffName(String buffName) {
        this.buffName = buffName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean isDispelOnHit() {
        return dispelOnHit;
    }

    public void setDispelOnHit(boolean dispelOnHit) {
        this.dispelOnHit = dispelOnHit;
    }

    public boolean isEndTurnEffect() {
        return endTurnEffect;
    }

    public void setEndTurnEffect(boolean endTurnEffect) {
        this.endTurnEffect = endTurnEffect;
    }

    public boolean isDisableCounter() {
        return disableCounter;
    }

    public void setDisableCounter(boolean disableCounter) {
        this.disableCounter = disableCounter;
    }

    public boolean isDisableActions() {
        return disableActions;
    }

    public void setDisableActions(boolean disableActions) {
        this.disableActions = disableActions;
    }

    public boolean isOnActivateEffect() {
        return onActivateEffect;
    }

    public void setOnActivateEffect(boolean onActivateEffect) {
        this.onActivateEffect = onActivateEffect;
    }

    public boolean isOnDeactivateEffect() {
        return onDeactivateEffect;
    }

    public void setOnDeactivateEffect(boolean onDeactivateEffect) {
        this.onDeactivateEffect = onDeactivateEffect;
    }

    public int getDefenseMod() {
        return defenseMod;
    }

    public void setDefenseMod(int defenseMod) {
        this.defenseMod = defenseMod;
    }

    public STANDARD_EVENT_TYPE getRemoveEvent() {
        return this.removeEvent;
    }

    public void setRemoveEvent(STANDARD_EVENT_TYPE removeEvent) {
        this.removeEvent = removeEvent;
    }

    public STD_MODES getTemplate() {
        return template;
    }

    @Override
    public boolean isBehavior() {
        return behaviorMode != null;
    }

    public BEHAVIOR_MODE getBehaviorMode() {
        return behaviorMode;
    }

    @Override
    public boolean isRemoveEndRound() {
        return template.isRemoveEndRound();
    }

    public void setBehaviorMode(BEHAVIOR_MODE behaviorMode) {
        this.behaviorMode = behaviorMode;
    }

}
