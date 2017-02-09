package main.content.enums;

import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.math.Formulas;

public enum STD_MODES implements MODE {
    ALERT(true, false, true),
    CONCENTRATION(true, false, true, "C_FOCUS", "max(100, {SOURCE_CONCENTRATION_MOD})/100*("
            + Formulas.CONCENTRATION_BASE + "+{SOURCE_CONCENTRATION_BONUS})"),

    RESTING(false, true, true, "C_STAMINA", "max(100, {SOURCE_RESTING_MOD})/100*("
            + Formulas.REST_CONST + "+{SOURCE_REST_BONUS})"),
    MEDITATION(true, true, true, "C_ESSENCE", "max(100, {SOURCE_MEDITATION_MOD})/100*("
            + Formulas.MEDITATION_BASE + "+{SOURCE_MEDITATION_BONUS})"), // {ACTIVE_something}
    DEFENDING(false, false, true),
    CHANNELING(true, true, false),
    DIVINATION(true, true, true),
    HIDE(true, false, true),
    STEALTH(false, false, false, "Stealth(50);", "Ap penalty(50)", ""),
    SEARCH(false, false, false, "Detection(50);", "Ap penalty(50)", ""),

    PRAYER(true, true, true, "C_MORALE", "(max(100, {SOURCE_PRAYER_MOD})/100*("
            + Formulas.PRAYER_CONST + "+{SOURCE_PRAYER_BONUS})"), // {ACTIVE_something}

    NORMAL(false, false, false),
    COWER(true, true, true),;

    static {
        COWER.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD * 3 / 2);

        DEFENDING.setDefenseMod(Formulas.DEFENDING_MODE_DEF_MOD);

        String paramModString = StringMaster.getParamModString("PARRY_CHANCE",
                Formulas.DEFEND_PARRY_CHANCE_MOD)
                + StringMaster.SEPARATOR
                + StringMaster.getParamModString("BLOCK_CHANCE", Formulas.DEFEND_BLOCK_CHANCE_MOD);
        DEFENDING.setParameterMods(paramModString);

        paramModString = StringMaster.getParamModString("DETECTION", Formulas.ALERT_DETECTION_MOD);
        ALERT.setParameterMods(paramModString);

        // CONCENTRATION.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD);
        RESTING.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD);
        MEDITATION.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD);
        CHANNELING.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD);
        DIVINATION.setDefenseMod(Formulas.DEFAULT_MODE_DEF_MOD);

        CHANNELING.setRemoveEvent(null);
        DIVINATION.setEndTurnEffect(true);
        CHANNELING.setEndTurnEffect(false);
        HIDE.setContinuous(true);
        STEALTH.setContinuous(true);
        SEARCH.setContinuous(true);
        STEALTH.setBuffName("Stealth Mode");
        HIDE.setBuffName("Hide Mode");
        SEARCH.setBuffName("Search Mode");
        // DEFENDING.setRemoveEvent(STANDARD_EVENT_TYPE.UNIT_TURN_STARTED);
        // ALERT.setRemoveEvent(STANDARD_EVENT_TYPE.UNIT_TURN_STARTED);
    }

    String parameter;
    String formula;
    boolean continuous = false;
    boolean dispelOnHit = true;
    boolean endTurnEffect = true;
    boolean disableCounter = true;
    boolean disableActions = true;
    boolean onActivateEffect = false;
    boolean onDeactivateEffect = false;
    private int defenseMod = 0;
    private STANDARD_EVENT_TYPE removeEvent = STANDARD_EVENT_TYPE.ROUND_ENDS;
    private String parameterMods;
    private String parameterBoni;
    private String propsAdded;
    private String buffName;

    STD_MODES(boolean dispelOnHit, boolean disableCounter, boolean disableActions,
              boolean endTurnEffect) {
        this(dispelOnHit, disableCounter, disableActions);
        this.endTurnEffect = endTurnEffect;
    }

    STD_MODES(boolean dispelOnHit, boolean disableCounter, boolean disableActions) {
        this.dispelOnHit = dispelOnHit;
        this.disableCounter = disableCounter;
        this.disableActions = disableActions;

        endTurnEffect = false;
    }

    STD_MODES(boolean dispelOnHit, boolean disableCounter, boolean disableActions,
              String parameterMods, String parameterBoni, String propsAdded) {
        this(dispelOnHit, disableCounter, disableActions);
        this.parameterMods = parameterMods;
        this.parameterBoni = parameterBoni;
        this.propsAdded = propsAdded;

    }

    STD_MODES(boolean dispelOnHit, boolean disableCounter, boolean disableActions,
              String parameter, String formula) {
        this(dispelOnHit, disableCounter, disableActions);
        endTurnEffect = true;
        this.parameter = parameter;

        this.formula = formula;
        // "{SOURCE_" + formula_parameter + "}*" + formula_modifier
        // + "+" + formula_bonus;

    }

    public String getBuffName() {
        if (buffName != null) {
            return buffName;
        }
        return StringMaster.getWellFormattedString(name());
    }

    @Override
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

    @Override
    public boolean isContinuous() {
        return continuous;
    }

    @Override
    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
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

    @Override
    public boolean isWatchSupported() {
        switch (this) {
            case NORMAL:
            case ALERT:
                return true;

        }
        return false;
    }

    @Override
    public boolean isBehavior() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getName() {

        return StringMaster.getWellFormattedString(name());
    }

    @Override
    public String getParameterMods() {
        return parameterMods;
    }

    public void setParameterMods(String parameterMods) {
        this.parameterMods = parameterMods;
    }

    @Override
    public String getParameterBoni() {
        return parameterBoni;
    }

    public void setParameterBoni(String parameterBoni) {
        this.parameterBoni = parameterBoni;
    }

    @Override
    public String getPropsAdded() {
        return propsAdded;
    }

    public void setPropsAdded(String propsAdded) {
        this.propsAdded = propsAdded;
    }

}
