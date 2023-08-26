package framework.entity.sub;

/**
 * Created by Alexander on 8/23/2023
 */
public class ActionSet {
    //is there any special sense in unifying the 3 actions?
    private final UnitAction standard;
    private final  UnitAction power;
    private final UnitAction defense;

    private UnitAction lastAction;

    public ActionSet(UnitAction standard, UnitAction power, UnitAction defense) {
        this.standard = standard;
        this.power = power;
        this.defense = defense;
    }

    public UnitAction getStandard() {
        return standard;
    }

    public UnitAction getPower() {
        return power;
    }

    public UnitAction getDefense() {
        return defense;
    }

    public UnitAction getLastAction() {
        return lastAction;
    }

    public void setLastAction(UnitAction lastAction) {
        this.lastAction = lastAction;
    }
}
