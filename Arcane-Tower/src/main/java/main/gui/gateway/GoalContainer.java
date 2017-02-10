package main.gui.gateway;

import main.gui.gateway.node.GoalGatewayComp;
import main.logic.Direction;
import main.logic.Goal;
import main.swing.generic.components.G_Panel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GoalContainer extends GatewayContainer<Goal> {

    private Direction direction;

    public GoalContainer(Direction d) {
        super();
        direction = d;
    }

    @Override
    public List<Goal> initData() {
        return direction.getGoals();
    }

    @Override
    protected void sortData() {
        Collections.sort(data, getSorter());
    }

    private Comparator<Goal> getSorter() {
        return new Comparator<Goal>() {

            @Override
            public int compare(Goal o1, Goal o2) {
                if (getComp(o1).isExpanded()) {
                    if (!getComp(o2).isExpanded()) {
                        return 1;
                    }
                }
                if (!getComp(o1).isExpanded()) {
                    if (getComp(o2).isExpanded()) {
                        return -1;
                    }
                }

                return 0;
            }
        };
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    protected boolean checkCustomCompRequired(Goal sub) {
        return getComp(sub).isExpanded();
    }

    @Override
    protected GoalGatewayComp getComp(Goal e) {
        return (GoalGatewayComp) super.getComp(e);
    }

    protected TaskContainer getCustomComp(Goal sub) {
        return new TaskContainer(sub);
    }

    @Override
    public G_Panel createComp(Goal e) {
        return new GoalGatewayComp(e);
    }

}
