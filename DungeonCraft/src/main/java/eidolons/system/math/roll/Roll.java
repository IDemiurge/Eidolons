package eidolons.system.math.roll;

import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.RollType;
import main.entity.Ref;

public class Roll {
    RollType type;
    GenericEnums.DieType die;

    private String sValue;
    private String tValue;

    private String sDice;
    private String tDice;

    private int rolledValue;
    private int rolledValue2;

    private String logAppendix;
    private String rollHint;
    private String rollHintTarget;
    private boolean result;

    public Roll(RollType type, GenericEnums.DieType die) {
        this.type = type;
        this.die = die;
    }

    public Roll(RollType type, GenericEnums.DieType die, String sValue, String tValue) {
        this.type = type;
        this.die = die;
        this.sValue = sValue;
        this.tValue = tValue;
    }

    public Roll(RollType type, GenericEnums.DieType die, String sValue, String tValue, String sDice, String tDice) {
        this.type = type;
        this.die = die;
        this.sValue = sValue;
        this.tValue = tValue;
        this.sDice = sDice;
        this.tDice = tDice;
    }

    public Boolean roll(Ref ref) {
            boolean result = RollMaster.roll(this, ref);
            return result;
    }

    public Roll setsValue(String sValue) {
        this.sValue = sValue;
        return this;
    }

    public Roll settValue(String tValue) {
        this.tValue = tValue;
        return this;
    }

    public Roll setsDice(String sDice) {
        this.sDice = sDice;
        return this;
    }

    public Roll settDice(String tDice) {
        this.tDice = tDice;
        return this;
    }

    public Roll setLogAppendix(String logAppendix) {
        this.logAppendix = logAppendix;
        return this;
    }

    public Roll setRollHint(String rollHintSource) {
        this.rollHint = rollHintSource;
        return this;
    }

    public Roll setRollHintTarget(String rollHintTarget) {
        this.rollHintTarget = rollHintTarget;
        return this;
    }

    public RollType getType() {
        return type;
    }

    public GenericEnums.DieType getDie() {
        return die;
    }

    public String getsValue() {
        return sValue;
    }

    public String gettValue() {
        return tValue;
    }

    public String getsDice() {
        return sDice;
    }

    public String gettDice() {
        return tDice;
    }

    public int getRolledValue() {
        return rolledValue;
    }

    public int getRolledValue2() {
        return rolledValue2;
    }

    public String getLogAppendix() {
        return logAppendix;
    }

    public String getRollHint() {
        return rollHint;
    }

    public String getRollHintTarget() {
        return rollHintTarget;
    }

    public boolean isResult() {
        return result;
    }

    public void setRolledValue(int rolledValue) {
        this.rolledValue = rolledValue;
    }

    public void setRolledValue2(int rolledValue2) {
        this.rolledValue2 = rolledValue2;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}