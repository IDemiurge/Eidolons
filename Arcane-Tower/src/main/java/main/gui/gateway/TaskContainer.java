package main.gui.gateway;

import main.gui.gateway.node.GatewayTaskComp;
import main.logic.Goal;
import main.logic.Task;
import main.swing.generic.components.G_Panel;

import java.util.List;

public class TaskContainer extends GatewayContainer<Task> {

	private Goal goal;

	public TaskContainer(Goal sub) {
		goal = sub;
	}

	@Override
	public List<Task> getData() {
		return goal.getTasks();
	}

	@Override
	public List<Task> initData() {
		return null;
	}

	@Override
	public G_Panel createComp(Task e) {
		return new GatewayTaskComp(e);
	}

	@Override
	protected boolean checkCustomCompRequired(Task sub) {
		return false;
	}

	@Override
	protected G_Panel getCustomComp(Task sub) {
		return null;
	}

}
