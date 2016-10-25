package main.logic;

import main.ArcaneTower;
import main.data.DataManager;
import main.entity.EntityMaster;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.logic.ArcaneRef.AT_KEYS;
import main.logic.entity.WorkItem;
import main.logic.util.AT_SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.time.ZeitMaster;

import java.util.LinkedList;
import java.util.List;

public class Goal extends WorkItem {
	List<Task> tasks;
	Direction direction;

	public Goal(ObjType type) {
		super(type);
	}

	@Override
	public void init() {
		initTasks();

		super.init();
	}

	@Override
	public void beforeSave() {
		// TODO Auto-generated method stub
		super.beforeSave();
	}

	private void initTasks() {
		tasks = new LinkedList<>();
		if (type.checkProperty(AT_PROPS.TASKS)) {
			for (ObjType t : DataManager.toTypeList(type.getProperty(AT_PROPS.TASKS),
					AT_OBJ_TYPE.TASK)) {
				addTask((Task) ArcaneTower.getEntity(t));
			}
		}
		for (Task g : ArcaneTower.getTasks()) {
			String goal = g.getProperty(AT_PROPS.GOAL);
			if ((g.isPending() || ArcaneTower.isTestMode())
					&& StringMaster.compare(goal, getName()))
				addTask(g);
		}
	}

	@Override
	public List<? extends ArcaneEntity> getChildren() {
		return getTasks();
	}

	public void addTask(Task g) {
		if (tasks.contains(g))
			return;
		tasks.add(g);
		g.getRef().setID(AT_KEYS.GOAL.toString(), id);
		if (isInitialized())
			toBase();
	}

	public void refreshStatus() {
		TASK_STATUS newStatus = null;
		if (EntityMaster.checkPropertyAny(getTasks(), AT_PROPS.TASK_STATUS, TASK_STATUS.ACTIVE))
			newStatus = TASK_STATUS.ACTIVE;
		if (EntityMaster.checkPropertyAll(getTasks(), AT_PROPS.TASK_STATUS, TASK_STATUS.DONE))
			newStatus = TASK_STATUS.DONE;
		switch (newStatus) {
			case ACTIVE:
				if (getStatusEnum() != TASK_STATUS.ACTIVE)
					started();
				break;
			case DONE:
				if (getStatusEnum() != TASK_STATUS.DONE)
					done();
				break;
			case PENDING:
				break;
		}
		setStatus(newStatus);
	}

	public void setStatus(TASK_STATUS status) {
		setProperty(AT_PROPS.TASK_STATUS, "" + status, true);
		if (getStatusEnum() == TASK_STATUS.PINNED) {
			ArcaneTower.getSession().unpinGoals(this);
		}
		if (status == TASK_STATUS.DONE) {
			done();
		}
	}

	private void initDirection() {
		// g.checkProperty(AT_PROPS.DIRECTION, getName())

		for (Direction sub : ArcaneTower.getDirections()) {
			if (sub.getGoals().contains(this)) {
				direction = sub;
				break;
			}
		}
	}

	public void done() {
		if (isDone()) {
			return;
		}
		ZeitMaster.finished(this);
		setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.DONE);
		ArcaneTower.saveVersion();
		ArcaneTower.getSessionWindow(this).refresh();
	}

	// remove() {
	// session.getGoals().remove(this);
	// }
	@Override
	public void toBase() {
		super.toBase();
		resetPropertyFromList(AT_PROPS.TASKS, tasks);
	}

	@Override
	public void setRef(Ref ref) {
		ref.setID(AT_KEYS.SESSION.toString(), id);
		super.setRef(ref);
	}

	public List<Task> getTasks() {
		initTasks();
		AT_SortMaster.sortTasks(tasks);
		return tasks;
	}

	public TASK_STATUS getStatusEnum() {
		return new EnumMaster<TASK_STATUS>().retrieveEnumConst(TASK_STATUS.class,
				getProperty(AT_PROPS.TASK_STATUS));
	}

	public String getStatus() {
		return getProperty(AT_PROPS.TASK_STATUS);
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		if (direction == null) {
			initDirection();
		}
		return direction;
	}

}
