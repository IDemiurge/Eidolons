package main.elements.conditions;

import main.entity.Entity;
import main.entity.Ref;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

public abstract class ConditionImpl implements Condition {
    public static final int MAX_TOOLTIP_LENGTH = 50;
    private static final boolean FORCE_LOG = false;
    protected Game game;
    private Boolean isTrue;
    private Entity match;
    private String xml;

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
        this.xml = xml;

    }

    @Override
    public String toXml() {
//        probably same as effects then?
        return xml;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return toString().equals(obj.toString());
    }

    @Override
    public String getTooltip() {
        return StringMaster.cropByLength(MAX_TOOLTIP_LENGTH, StringMaster
         .format(toString()));
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean preCheck(Ref ref) {
        //TODO EA check
//        ref = ref.getCopy();
        setGame(ref.getGame());
        try {
            setTrue(check(ref));
        } catch (Exception e) {
           LogMaster.log(1, "*" + toString() + " failed on " + ref);
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        return isTrue;
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

    public Boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
    }

}
