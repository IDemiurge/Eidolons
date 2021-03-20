package framework;

import data.C3Reader;
import data.C3Writer;
import log.C3Logger;
import query.C3QueryManager;
import query.C3QueryResolver;
import task.C3TaskManager;
import task.C3TaskResolver;

public class C3Manager {

    protected final C3TaskResolver taskResolver;
    protected final C3TaskManager taskManager;
    protected final C3QueryResolver queryResolver;
    protected final C3QueryManager queryManager;
    protected final C3Logger qlogger;
    protected final C3Logger tlogger;

    protected final C3Reader reader;
    protected final C3Writer writer;

    public C3Manager() {
        reader = new C3Reader(this);
        writer = new C3Writer(this);
        qlogger = new C3Logger(this, true);
        tlogger = new C3Logger(this, false);
        queryResolver = new C3QueryResolver(this);
        queryManager = new C3QueryManager(this,reader.createQCategoryMap(), reader.readQueryData());

        taskResolver = new C3TaskResolver(this);
        taskManager = new C3TaskManager(this,reader.createTCategoryMap(), reader.readTaskData(), reader.readTaskStatusMap());
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
}
