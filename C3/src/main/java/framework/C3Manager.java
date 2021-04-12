package framework;

import data.C3Enums;
import data.C3Reader;
import data.C3Writer;
import gui.tray.C3TrayHandler;
import hotkey.dialog.C3DialogHandler;
import log.C3Logger;
import main.data.xml.XmlStringBuilder;
import query.C3QueryManager;
import query.C3QueryResolver;
import query.C3_Query;
import session.C3SessionHandler;
import session.C3Timer;
import session.C3TimerHandler;
import session.SessionLogger;
import task.C3TaskManager;
import task.C3TaskResolver;
import task.C3_Task;

public class C3Manager {

    protected C3TaskResolver taskResolver;
    protected C3TaskManager taskManager;
    protected C3QueryResolver queryResolver;
    protected C3QueryManager queryManager;
    protected C3Logger qlogger;
    protected C3Logger tlogger;

    protected C3Reader reader;
    protected C3Writer writer;
    protected C3TrayHandler trayHandler;
    protected C3TimerHandler timerHandler;
    protected  C3SessionHandler sessionHandler;
    protected C3_Query currentQuery;
    protected C3_Task currentTask;
    private SessionLogger sessionLogger;
    private C3DialogHandler dialogHandler;

    public C3Manager( ) {
        reader = new C3Reader(this);
        writer = new C3Writer(this);
        qlogger = new C3Logger(this, true);
        tlogger = new C3Logger(this, false);
        dialogHandler = new C3DialogHandler(this);
            sessionHandler = new C3SessionHandler(this);
            sessionLogger = new SessionLogger(this);

            queryResolver = new C3QueryResolver(this);
            queryManager = new C3QueryManager(this, reader.createQCategoryMap(), reader.readQueryData());
            taskResolver = new C3TaskResolver(this);
            C3Filter<C3Enums.TaskCategory> filter = generateTaskFilter();
            taskManager = new C3TaskManager(this, reader.createTCategoryMap(), filter.filter(reader.readTaskData()),
                    reader.readTaskStatusMap(), filter);

        trayHandler = new C3TrayHandler(this);
        timerHandler = new C3TimerHandler(this);

    }

    private C3Filter<C3Enums.TaskCategory> generateTaskFilter() {
        return new C3Filter<>("");
    }

    public C3TaskResolver getTaskResolver() {
        return taskResolver;
    }

    public C3Logger getQLogger() {
        return qlogger;
    }

    public C3Logger getTLogger() {
        return tlogger;
    }

    public C3TaskManager getTaskManager() {
        return taskManager;
    }

    public C3QueryResolver getQueryResolver() {
        return queryResolver;
    }

    public C3QueryManager getQueryManager() {
        return queryManager;
    }

    public C3Reader getReader() {
        return reader;
    }

    public C3Writer getWriter() {
        return writer;
    }

    public void setCurrentQuery(C3_Query currentQuery) {
        this.currentQuery = currentQuery;
    }

    public C3_Query getCurrentQuery() {
        return currentQuery;
    }

    public void setCurrentTask(C3_Task currentTask) {
        this.currentTask = currentTask;
    }

    public C3_Task getCurrentTask() {
        return currentTask;
    }

    public C3TrayHandler getTrayHandler() {
        return trayHandler;
    }

    public void notifyTimerElapsed(C3_Query query) {
        getTrayHandler().notify(query);
    }

    public C3TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public C3SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public SessionLogger getSessionLogger() {
        return sessionLogger;
    }

    public C3DialogHandler getDialogHandler() {
        return dialogHandler;
    }

}
