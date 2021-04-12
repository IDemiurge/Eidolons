package session;

import data.C3Enums;
import framework.C3Item;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;
import task.C3_Task;

import java.util.LinkedList;
import java.util.List;

public class C3Session extends C3Item<C3Enums.Direction> {
    private C3Enums.SessionType sessionType;
    private C3Enums.Direction direction;
    private Integer duration;

    String status;
    private int minutesLeft;
    private boolean finished;

    private List<C3_Task> tasksDone=    new LinkedList<>() ;
    private  List<C3_Task> tasksPending=    new LinkedList<>() ;
    private Integer minsBreakInverval;
    private  C3Timer timer;

    public C3Session(C3Enums.SessionType sessionType, C3Enums.Direction direction, Integer duration, String subCategory, String text) {
        super(direction, subCategory, text);
        this.sessionType = sessionType;
        this.direction = direction;
        this.duration = duration;
    }

    public C3Enums.SessionType getSessionType() {
        return sessionType;
    }

    public C3Enums.Direction getDirection() {
        return direction;
    }

    public Integer getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return sessionType + " " + direction +
                "Session\n Time: " +
                StringMaster.getCurOutOfMax(minutesLeft + "", "" + duration);
    }

    public String getTimeString() {
        return "Started at: "+ TimeMaster.getFormattedTime(timer.timeStarted, false, true)
                +"Started at: "+ TimeMaster.getFormattedTime(timer.timeStarted, false, true)
                ;
    }
    public void setMinutesLeft(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    public int getMinutesLeft() {
        return minutesLeft;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public List<C3_Task> getTasksDone() {
        return tasksDone;
    }

    public void setTasksDone(List<C3_Task> tasksDone) {
        this.tasksDone = tasksDone;
    }

    public List<C3_Task> getTasksPending() {
        return tasksPending;
    }

    public void setTasksPending(List<C3_Task> tasksPending) {
        this.tasksPending = tasksPending;
    }

    public Integer getMinsBreakInverval() {
        return minsBreakInverval;
    }

    public void setMinsBreakInverval(Integer minsBreakInverval) {
        this.minsBreakInverval = minsBreakInverval;
    }

    public C3Timer getTimer() {
        return timer;
    }

    public void setTimer(C3Timer timer) {
        this.timer = timer;
    }
}
