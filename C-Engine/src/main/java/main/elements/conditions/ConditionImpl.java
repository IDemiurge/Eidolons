package main.elements.conditions;

import main.elements.ReferredElement;
import main.entity.Entity;
import main.entity.Ref;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.StringMaster;

public abstract class ConditionImpl extends ReferredElement implements Condition {
    public static final int MAX_TOOLTIP_LENGTH = 50;
    private boolean isTrue;
    private boolean forceLog;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Condition join(Condition condition) {
        return new Conditions(this, condition);
    }

    @Override
    public String getTooltip() {
        return StringMaster.cropByLength(MAX_TOOLTIP_LENGTH, StringMaster
                .getWellFormattedString(toString()));
    }

    @Override
    public boolean check(Ref ref) {
        setRef(ref);
        boolean logged = false;
        if (!isLoggingBlocked()) {
            if (ref.getGame().getManager().isSelecting()
                    || ref.getGame().getManager().isTriggerBeingChecked()) {
                logged = true;
            }
        }
        // ++ trigger
        try {
            isTrue = check();
            if (logged) {
                LogMaster.log((forceLog ? 1 : LogMaster.CONDITION_DEBUG),
                        toString() + " checks " + isTrue + " on " + ref);
            }

        } catch (Exception e) {
            LogMaster.log(1, "*" + toString() + " failed on " + ref);
            e.printStackTrace();
            return false;
        }
        if (isTrue) {
            // LogMaster.log(LogMaster.CONDITION_DEBUG, "" + toString()
            // + " checks TRUE for " + ref);

            return true;
        }
        // LogMaster.log(LogMaster.CONDITION_DEBUG, "" + toString()
        // + " checks FALSE for " + ref);
        // TODO init REASON!
        isTrue = false;
        return false;
    }

    public boolean isLoggingBlocked() {
        return false;
    }

    @Override
    public boolean check(Entity match) {
        // match.getRef() TODO - give ObjType's per 1 ID! Any simulation will
        // need this!
        Ref REF = match.getRef().getCopy();
        REF.setMatch(match.getId());

        return check(REF);
    }

    // public abstract boolean check();

    @Override
    public abstract boolean check();

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }

}
