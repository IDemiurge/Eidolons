package main.system.math.roll;

import main.content.enums.GenericEnums.ROLL_TYPES;
import main.entity.Ref;

public class Roll {
    ROLL_TYPES type;
    int drawRange;
    private String success;
    private String fail;
    private Boolean result;
    private int rolledValue;
    private int rolledValue2;
    private String logAppendix;
    private String rollSource;
    private String rollTarget;

    public Roll(ROLL_TYPES type, String success, String fail) {
        this(type, success, fail, 0);
    }

    public Roll(ROLL_TYPES type, String success, String fail, int drawRange) {
        this.type = type;
        this.success = success;
        this.fail = fail;
        this.drawRange = drawRange;
    }


    public Boolean roll(Ref ref) {
        boolean result = RollMaster.roll(type, success, fail, ref);
        if (drawRange > 0) {
            int val1 = Math.max(1, RollMaster.getRolledValue());
            int val2 = Math.max(1, RollMaster.getRolledValue2());
            if (100 - Math.min(val1 / val2, val2 / val1)
             * 100 <= drawRange) {
                return null;
            }
        }

        return result;
    }

    public ROLL_TYPES getType() {
        return type;
    }

    public void setType(ROLL_TYPES type) {
        this.type = type;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getFail() {
        return fail;
    }

    public void setFail(String fail) {
        this.fail = fail;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public int getRolledValue2() {
        return rolledValue2;
    }

    public void setRolledValue2(int rolledValue2) {
        this.rolledValue2 = rolledValue2;
    }

    public int getRolledValue() {
        return rolledValue;
    }

    public void setRolledValue(int rolledValue) {
        this.rolledValue = rolledValue;
    }

    public String getLogAppendix() {
        return logAppendix;
    }

    public void setLogAppendix(String logAppendix) {
        this.logAppendix = logAppendix;

    }

    public String getRollSource() {
        return rollSource;
    }

    public void setRollSource(String rollSource) {
        this.rollSource = rollSource;
    }

    public String getRollTarget() {
        return rollTarget;
    }

    public void setRollTarget(String rollTarget) {
        this.rollTarget = rollTarget;

    }

}