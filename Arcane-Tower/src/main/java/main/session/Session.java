package main.session;

import main.ArcaneTower;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.enums.StatEnums.SESSION_STATUS;
import main.enums.StatEnums.STATE;
import main.enums.StatEnums.WORK_STYLE;
import main.gui.SessionWindow;
import main.logic.*;
import main.logic.ArcaneRef.AT_KEYS;
import main.logic.util.AT_SortMaster;
import main.system.auxiliary.ListMaster;
import main.time.ZeitMaster;

import java.util.LinkedList;
import java.util.List;

public class Session extends ArcaneEntity {

    private SessionWindow window;
    private List<Goal> goals;
    private List<Task> tasks; // active
    private Direction direction;
    private WORK_STYLE style;
    private STATE state;
    private List<Goal> pinnedGoals;
    private List<Task> pinnedTasks;
    private boolean locked;
    private boolean multiDirection;

    public Session(ObjType templateType) {
        super(templateType);
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID(AT_KEYS.SESSION.toString(), id);
        super.setRef(ref);
    }

    public void setStyle(WORK_STYLE style) {
        this.style = style;

    }

    @Override
    public void init() {
        // default direction? last modified? first in list?

        super.init();
        ObjType type = DataManager.getType(getProperty(AT_PROPS.DIRECTION), AT_OBJ_TYPE.DIRECTION);
        if (type != null) {
            direction = (Direction) ArcaneTower.getEntity(type);
        } else {
            List<ObjType> list = DataManager.toTypeList(getProperty(AT_PROPS.SESSION_DIRECTIONS),
                    AT_OBJ_TYPE.DIRECTION);
            if (list.isEmpty()) {
                direction = ArcaneTower.getDirections().get(0);
            } else {
                direction = (Direction) ArcaneTower.getEntity(list.get(0));
            }
        }
        tasks = ArcaneTower.getTasks(getListFromProperty(AT_OBJ_TYPE.TASK, AT_PROPS.TASKS));
        goals = ArcaneTower.getGoals(getListFromProperty(AT_OBJ_TYPE.GOAL, AT_PROPS.GOALS));

    }

    public void setState(STATE state) {
        String time = ZeitMaster.getTimeStamp();
        addProperty(AT_PROPS.STATE_TIMEMARKS, getProperty(AT_PROPS.STATE) + " finished at " + time
                + ";");
        this.state = state;
        setProperty(AT_PROPS.STATE, state.toString());
        addProperty(AT_PROPS.STATE_TIMEMARKS, getProperty(AT_PROPS.STATE) + " started at "
                + ZeitMaster.MARK_SEPARATOR + time + ";");
    }

    public boolean isPaused() {
        return checkProperty(AT_PROPS.SESSION_STATUS, "" + SESSION_STATUS.PAUSED);
    }

    public void pinGoal(Goal g) {
        getPinnedGoals().add(g);
        g.setProperty(AT_PROPS.SESSION, getName());
    }

    public void unpinGoals(Goal g) {
        getPinnedGoals().remove(g);
        g.removeProperty(AT_PROPS.SESSION, getName());
    }

    public void unpinTask(Task task) {
        getPinnedTasks().remove(task);
        task.removeProperty(AT_PROPS.SESSION, getName());
    }

    public void pinTask(Task t) {
        getPinnedTasks().add(t);
        t.setProperty(AT_PROPS.SESSION, getName());
    }

    @Override
    public void toBase() {
        super.toBase();
        if (getDisplayedGoals() == null) {
            return;
        }
        AT_PROPS prop = AT_PROPS.GOALS;
        List<? extends Entity> list = getDisplayedGoals();
        resetPropertyFromList(prop, list);

        prop = AT_PROPS.TASKS;
        list = getTasks();
        resetPropertyFromList(prop, list);
    }

    public List<Goal> getDisplayedGoals() {
        if (goals == null) {
            goals = new LinkedList<>();
        }
        if (isLocked()) {
            return goals;
        }
        if (direction == null) {
            return null;
        }
        AT_SortMaster.sortGoals(goals, this);
        if (goals.isEmpty())
        // in non-locked mode, update all Direction's goals?
        // otherwise, all Session's goals
        {
            return new ListMaster<Goal>().join(false, goals, getDirection().getGoals());
        }
        return new ListMaster<Goal>().getCommonElements(goals, getDirection().getGoals());
        // return goals;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public boolean isMultiDirection() {
        return multiDirection;
    }

    public void setMultiDirection(boolean multiDirection) {
        this.multiDirection = multiDirection;

    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
        toBase();
    }

    public Goal getCurrentlyDisplayedGoal() {
        return getDisplayedGoals().get(window.getGoalsPanel().getCurrentIndex());
    }

    public void removeCustomGoals() {
        for (Goal sub : new LinkedList<>(goals)) {
            if (sub.getDirection() == null) {
                goals.remove(sub);
            }
        }
    }

    public List<Task> getTasks() {
        if (tasks == null) {
            tasks = new LinkedList<>();
        }
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        setProperty(AT_PROPS.DIRECTION, direction.toString());
        // removeCustomGoals();
    }

    public List<Goal> getPinnedGoals() {
        if (pinnedGoals == null) {
            pinnedGoals = new LinkedList<>();
        }
        return pinnedGoals;
    }

    public List<Task> getPinnedTasks() {
        if (pinnedTasks == null) {
            pinnedTasks = new LinkedList<>();
        }
        return pinnedTasks;
    }

    public SessionWindow getWindow() {
        return window;
    }

    public void setWindow(SessionWindow window) {
        this.window = window;
    }

}
