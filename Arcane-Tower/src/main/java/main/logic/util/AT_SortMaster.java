package main.logic.util;

import main.enums.StatEnums.TASK_STATUS;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.logic.Goal;
import main.logic.Task;
import main.session.Session;
import main.system.SortMaster;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AT_SortMaster {

	public static void sortGoals(List<Goal> goals, Session session) {
		Collections.sort(goals, new Comparator<Goal>() {
			@Override
			public int compare(Goal o1, Goal o2) {
				int result = 0;

				// if (session.getGoals().contains(o))
				return result;
			}

		});
	}

	public static void sortTasks(List<Task> tasks) {
		Collections.sort(tasks, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {
				int result = 0;

				if (o1.isActive() || o2.isActive()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.ACTIVE, true);
				}
				if (o1.isActive() || o2.isActive()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.PINNED, true);
				}
				if (o1.isPrototyped() || o2.isPrototyped()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.PROTOTYPED, true);
				}

				if (o1.isFailed() || o2.isFailed()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.FAILED);
				}
				if (o1.isDone() || o2.isDone()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.DONE);
				}

				if (o1.isBlocked() || o2.isBlocked()) {
					return checkTaskStatus(o1, o2, TASK_STATUS.BLOCKED);
				}

				if (o1.getGlory() == o2.getGlory())
					return 0;
				if (o1.getGlory() > o2.getGlory())
					return 1;
				return -1;
			}

			private int checkTaskStatus(Task o1, Task o2, TASK_STATUS status) {
				return checkTaskStatus(o1, o2, status, false);
			}

			private int checkTaskStatus(Task o1, Task o2, TASK_STATUS status, boolean negative) {
				int result;
				result = SortMaster.compare(o1, o2, AT_PROPS.TASK_STATUS, status.toString(),
						negative);
				if (result != 0)
					return result;
				else
					return SortMaster.compare(o1, o2, AT_PARAMS.TIME_FINISHED);
			}

		});

	}

}
