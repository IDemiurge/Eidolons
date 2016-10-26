package main.gui.gateway.node;

import main.content.VALUE;
import main.gui.AT_EntityNode;
import main.logic.AT_PARAMS;
import main.logic.Goal;

public class GoalGatewayComp extends AT_EntityNode<Goal> {

	public GoalGatewayComp(Goal e) {
		super(e);
	}

	@Override
	public VALUE[] getDisplayedValues() {
		return new VALUE[] { AT_PARAMS.TIME_SPENT, AT_PARAMS.TIME_ESTIMATED, AT_PARAMS.DEADLINE, };
	}
}
