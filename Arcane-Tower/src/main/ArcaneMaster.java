package main;

import main.data.DataManager;
import main.entity.Entity;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.Direction;
import main.logic.Goal;
import main.logic.Task;
import main.session.SessionMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.secondary.BooleanMaster;

import java.util.List;

public class ArcaneMaster {
	public static Goal getCurrentGoal() {
		return SessionMaster.getSession().getCurrentlyDisplayedGoal();
	}

	public static void setPriorityTopToBottom(List<? extends Entity> types) {
		int highestPoint = DialogMaster.inputInt("Highest point of priority?", 300);
		setPriorityTopToBottom(types, highestPoint, false);
	}

	public static Goal chooseGoal() {
		return chooseGoal(null);
	}

	public static boolean checkDisplayed(Task task) {
		if (task.isDone()) {
			if (TimeMaster.isToday(task.getIntParam(AT_PARAMS.TIME_FINISHED)))
				return false;
		}
		return true;
	}

	public static Goal chooseGoal(Direction d) {
		String result = ListChooser.chooseObj((d == null) ? ArcaneTower.getGoals() : d.getGoals(),
				SELECTION_MODE.SINGLE);
		if (result == null) {
			return null;
		}
		return ArcaneTower.getGoals(DataManager.toTypeList(result, AT_OBJ_TYPE.GOAL)).get(0);

	}

	public static void setPriorityTopToBottom(List<? extends Entity> types, int highestPoint,
			Boolean dynamic_not_both) {
		int i = highestPoint;
		for (Entity type : types) {
			if (!BooleanMaster.isFalse(dynamic_not_both))
				type.setParam(AT_PARAMS.DYNAMIC_PRIORITY, i);
			if (!BooleanMaster.isTrue(dynamic_not_both))
				type.setParam(AT_PARAMS.PRIORITY, i);
			i--;
		}
	}

}
