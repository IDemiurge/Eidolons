package main.elements.conditions;

import main.entity.Entity;
import main.entity.Ref;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;

public abstract class ConditionImpl  implements Condition {
    public static final int MAX_TOOLTIP_LENGTH = 50;
    private static final boolean FORCE_LOG =false ;
    protected Game game;
    private boolean isTrue;
    private Entity match;
    private  String xml;

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Condition join(Condition condition) {
        return new Conditions(this, condition);
    }

    @Override
    public void setXml(String xml) {
      this.xml=xml;

    }

    @Override
    public String toXml() {
//        probably same as effects then?
        return xml;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return this == null;
        }
        return toString().equals(obj.toString());
    }

    @Override
    public String getTooltip() {
        return StringMaster.cropByLength(MAX_TOOLTIP_LENGTH, StringMaster
                .getWellFormattedString(toString()));
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean preCheck(Ref ref) {
        ref =ref.getCopy();
       setGame(ref.getGame());
        boolean logged = false;
        if (!isLoggingBlocked()) {
            if (ref.getGame().getManager().isSelecting()
                    || ref.getGame().getManager().isTriggerBeingChecked()) {
                logged = true;
            }
        }
        try {
            setTrue( check(ref));
            if (logged) {
//                LogMaster.log((FORCE_LOG ? 1 : LogMaster.CONDITION_DEBUG),
//                        toString() + " checks " + isTrue + " on " + ref);
            }

        } catch (Exception e) {
//            LogMaster.log(1, "*" + toString() + " failed on " + ref);
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        if (isTrue) {
//             LogMaster.log(LogMaster.CONDITION_DEBUG, "" + toString()
//             + " checks TRUE for " + ref);

            return true;
        }
        // LogMaster.log(LogMaster.CONDITION_DEBUG, "" + toString()
        // + " checks FALSE for " + ref);
        // TODO init REASON!
       setTrue(false);
        return false;
    }

    public boolean isLoggingBlocked() {
        return false;
    }

    @Override
    public boolean check(Entity match) {
        Ref REF = match.getRef().getCopy();
        REF.setMatch(match.getId());
        this.match = match;
        return preCheck(REF);
    }
    @Override
    public boolean check(Entity source, Entity match) {
        Ref REF = source.getRef().getCopy();
        REF.setMatch(match.getId());
        this.match = match;
        return preCheck(REF);
    }




    @Override
    public abstract boolean check(Ref ref);

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }

}
