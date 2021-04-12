package log;

import framework.C3Handler;
import framework.C3Item;
import framework.C3Manager;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;
import query.C3_Query;

public class C3Logger extends C3Handler {
    private static final String QLOG_FILE_PATH = "resources/c3_querylog.txt";
    private static final String TLOG_FILE_PATH = "resources/c3_tasklog.txt";

    static StringBuilder logContentsBuilder;
    private final String log_file_path;

    public C3Logger(C3Manager manager, boolean query) {
        this(manager, query ? QLOG_FILE_PATH : TLOG_FILE_PATH);
    }

    public C3Logger(C3Manager manager, String log_file_path ) {
        super(manager);
        this.log_file_path = log_file_path;
        logContentsBuilder = new StringBuilder(FileManager.readFile(log_file_path));

    }

    public void started(C3Item item) {
        appendSeparator();
        appendDate();
        appendLine(item + " started!");
        persist();
    }

    public void done(C3Item item, String input) {
        appendDate();
        appendLine(item + " complete!");
        appendLine( "Comment: "+ input);
        persist();
    }
    public void updated(C3Item item, String arg) { // status?
        appendDate();
        appendLine(item + " changed");
        persist();
    }

    public void logInput(C3Item item,String input) {
        appendDate();
        appendLine(item + " changed");
        persist();
    }
    private void appendSeparator() {
        appendLine("--------------------------------------");
    }

    public void appendLine(String string) {
        logContentsBuilder.append(string + "\n");

    }

    public void persist() {
        FileManager.write(logContentsBuilder.toString(), log_file_path);
    }

    public void appendDate() {
        appendLine(TimeMaster.getTimeStamp());
    }

}
