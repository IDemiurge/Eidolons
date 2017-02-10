package main.logic;

import main.ArcaneTower;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.logic.ArcaneRef.AT_KEYS;

import java.util.LinkedList;
import java.util.List;

public class Direction extends ArcaneEntity {

    private List<Goal> goals;

    public Direction(ObjType type) {
        super(type);
        initGoals();
    }

    public void initGoals() {
        goals = new LinkedList<>();
        for (Goal g : ArcaneTower.getGoals()) {
            if (g.checkProperty(AT_PROPS.DIRECTION, getName())) {
                g.setDirection(this);
                goals.add(g);
            }
        }
    }

    @Override
    public List<? extends ArcaneEntity> getChildren() {
        return getGoals();
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID(AT_KEYS.DIRECTION.toString(), id);
        super.setRef(ref);
    }

    public List<Goal> getGoals() {
        return goals;
    }

}
