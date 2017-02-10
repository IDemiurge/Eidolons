package main.logic;

import main.ArcaneTower;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.gui.sub.TaskComponent.TASK_COMMAND;
import main.logic.ArcaneRef.AT_KEYS;
import main.logic.entity.WorkItem;
import main.system.auxiliary.EnumMaster;
import main.time.ZeitMaster;

public class Task extends WorkItem {

    public Task(ObjType type) {
        super(type);
        if (!checkProperty(AT_PROPS.TASK_STATUS)) {
            setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.IDEA);
        }
    }

    @Override
    public void setRef(Ref ref) {
        ref.setID(AT_KEYS.TASK.toString(), id);
        super.setRef(ref);
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

    public String getTaskString() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean checkCommandShown(TASK_COMMAND cmd) {
        switch (cmd) {
            case TOGGLE:

                break;
            case BLOCK:
                break;
            case DONE:
                return !isDone();
            case REMOVE:
                break;
        }
        return true;
    }

    public void done() {
        // setParam(AT_PARAMS.TIME_DONE, (int) TimeMaster.getTime());

        if (isRecurring()) {
            getSession().incrementParam(AT_PARAMS.TIMES_COMPLETED);
            // TODO OR APPEND *TIME*
            return;
        }
        if (isDone()) {
            return;
        }
        ZeitMaster.finished(this);
        setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.DONE);
        getSession().incrementParam(AT_PARAMS.TASKS_COMPLETED);
        getGoal().refreshStatus();
        ArcaneTower.saveVersion();
        ArcaneTower.getSessionWindow(this).refresh();
    }

    public Goal getGoal() {
        return (Goal) getParent();
    }

    public void beforeSave() {
        super.beforeSave();
    }

    public void toggle() {
        // if (ArcaneTower.isControlSoundsOn())
        // if (isActive())
        // SoundMaster.playStandardSound(STD_SOUNDS.PAUSE);
        // else
        // SoundMaster.playStandardSound(STD_SOUNDS.RESUME);

        if (isActive()) {
            setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.PENDING);
        } else {
            setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.ACTIVE);
        }
        // TODO TOTAL TIME ACTIVE/PAUSED CALC - WHEN? KEEP INCREMENTING PER
        // STATE

    }

    public TASK_STATUS getStatusEnum() {
        return new EnumMaster<TASK_STATUS>().retrieveEnumConst(TASK_STATUS.class,
                getProperty(AT_PROPS.TASK_STATUS));
    }

    public String getStatus() {
        return getProperty(AT_PROPS.TASK_STATUS);
    }

    public void setStatus(TASK_STATUS status) {
        setProperty(AT_PROPS.TASK_STATUS, "" + status, true);
        if (getStatusEnum() == TASK_STATUS.PINNED) {
            ArcaneTower.getSession().unpinTask(this);
        }
        if (status == TASK_STATUS.DONE) {
            done();
        }
        if (status == TASK_STATUS.PENDING) {
            ZeitMaster.paused(this);
        }
        if (status == TASK_STATUS.ACTIVE) {
            ZeitMaster.started(this);
        }
        if (status == TASK_STATUS.PINNED) {
            ArcaneTower.getSession().pinTask(this);
        }
        ArcaneTower.saveEntity(this, true);
    }

}
