package framework;

import data.C3Enums;
import data.C3Reader;
import data.C3Writer;
import gui.tray.C3TrayHandler;
import log.C3Logger;
import query.C3QueryManager;
import query.C3QueryResolver;
import query.C3_Query;
import task.C3TaskManager;
import task.C3TaskResolver;
import task.C3_Task;

public class C3Manager {

    protected final C3TaskResolver taskResolver;
    protected final C3TaskManager taskManager;
    protected final C3QueryResolver queryResolver;
    protected final C3QueryManager queryManager;
    protected final C3Logger qlogger;
    protected final C3Logger tlogger;

    protected final C3Reader reader;
    protected final C3Writer writer;
    protected final C3TrayHandler trayHandler;
    protected C3_Query currentQuery;
    protected C3_Task currentTask;

    public C3Manager() {
        reader = new C3Reader(this);
        writer = new C3Writer(this);
        qlogger = new C3Logger(this, true);
        tlogger = new C3Logger(this, false);
        queryResolver = new C3QueryResolver(this);
        queryManager = new C3QueryManager(this,reader.createQCategoryMap(), reader.readQueryData());

        taskResolver = new C3TaskResolver(this);
        C3Filter<C3Enums.TaskCategory> filter = generateTaskFilter();
        taskManager = new C3TaskManager(this,reader.createTCategoryMap(), filter.filter(reader.readTaskData()),
                reader.readTaskStatusMap(),filter
              );

        trayHandler = new C3TrayHandler(this);

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
}
