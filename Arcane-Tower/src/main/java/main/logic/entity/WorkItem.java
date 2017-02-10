package main.logic.entity;

import main.ArcaneTower;
import main.entity.type.ObjType;
import main.enums.StatEnums.TASK_STATUS;
import main.enums.StatEnums.TASK_TYPE;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.logic.ArcaneEntity;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.TimeMaster;
import main.time.ZeitMaster;

public class WorkItem extends ArcaneEntity {

    public WorkItem(ObjType type) {
        super(type);
        // TODO Auto-generated constructor stub
    }

    public void setWasDone() {
        Integer time = DialogMaster.inputInt("Estimate minutes spent...", 50);
        setParam(AT_PARAMS.TIME_STARTED, (int) (TimeMaster.getTime() - time * 601000));
        done();
    }

    public boolean isRecurring() {
        return getTaskType() == TASK_TYPE.RECURRING;
    }

    public void started() {
        ZeitMaster.started(this);
    }

    public void done() {
        ZeitMaster.finished(this);
        setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.DONE);
        ArcaneTower.saveVersion();
        ArcaneTower.getSessionWindow(this).refresh();
    }

    public void block() {
        if (isBlocked()) {
            setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.PENDING);
        } else {
            setProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.BLOCKED);
        }

    }

    public TASK_TYPE getTaskType() {
        return new EnumMaster<TASK_TYPE>().retrieveEnumConst(TASK_TYPE.class,
                getProperty(AT_PROPS.TASK_TYPE));
    }

    public boolean isDone() {
        return checkProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.DONE);
    }

    public boolean isBlocked() {
        return checkProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.BLOCKED);
    }

    public boolean isFailed() {
        return checkProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.FAILED);
    }

    public boolean isPrototyped() {
        return checkProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.PROTOTYPED);

    }

    public boolean isActive() {
        return checkProperty(AT_PROPS.TASK_STATUS, "" + TASK_STATUS.ACTIVE);

    }
}
