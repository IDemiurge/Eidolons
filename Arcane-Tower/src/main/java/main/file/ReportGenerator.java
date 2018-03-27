package main.file;

import main.content.VALUE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.file.VersionMaster.VERSION_PERIOD;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.ArcaneEntity;
import main.logic.Task;
import main.logic.util.AT_SortMaster;
import main.session.Session;
import main.session.SessionMaster;
import main.system.auxiliary.StringMaster;
import main.time.ZeitMaster;

import java.util.List;
import java.util.Map;

public class ReportGenerator {

    private static final AT_PARAMS[] reportedParams = {AT_PARAMS.TIME_FINISHED,
            AT_PARAMS.TIME_STARTED, AT_PARAMS.TIME_LAST_MODIFIED,};

    public static String generateReport(REPORT_TYPE type, ArcaneEntity arg) {
        switch (type) {
            case SESSION:
                return getSessionReport((Session) arg);
            case DAILY:
                return getDailyReport();

        }

        return null;

    }

    private static String getDailyReport() {
        // filter sessions for this day
        String timeStamp = ZeitMaster.getTimeStamp();
        List<ObjType> list = DataManager.getTypes(AT_OBJ_TYPE.SESSION);
        // i need sessions that were started or finished on this day
//		m(list, AT_PROPS.TIME_LAST_MODIFIED, timeStamp);
        for (AT_PARAMS param : reportedParams) {
            addSessionsForDailyReport(list, param);
        }

//		FilterMaster.filterByParam(list, AT_PARAMS.TIME_FINISHED, value, TYPE, greater_less_equal)
        // or always init the day's sessions dynamically?
        for (Session s : SessionMaster.getSessions()) {
            list.add(s.getType());
        }
        for (ObjType s : list) {
//			reportText += getSessionDetail(s);
        }
        return null;
    }

    private static void addSessionsForDailyReport(List<ObjType> list, AT_PARAMS param) {
        for (ObjType s : list) {
            int time = s.getIntParam(param);
            if (ZeitMaster.isToday(time)) {
                list.add(s);
            }
        }
    }

    private static String getTaskDetails(Session arg) {
        List<Task> tasks = arg.getTasks();
        AT_SortMaster.sortTasks(tasks);
        String s = "Task Details: ";
        for (Task a : tasks) {
            s += a.getTaskString() + "\n";
        }
        return s;
    }

    public static void newReport(Session session) {
        String report = getSessionReport(session);
        MainWriter.writeToMain(report, "report", getReportName(session));

    }

    private static String getSessionReport(Session session) {
        // Session previousVersion
        Map<VALUE, String> map = VersionMaster.getVersionDifferenceMap(VERSION_PERIOD.SESSION, 1,
                session);
        String report = getReportName(session);
        for (VALUE v : map.keySet()) {
            report += v + ": " + map.get(v) + ";" + StringMaster.NEW_LINE;
        }

        // String result = VersionMaster.compareVersions(new Task(null),
        // AT_PARAMS.TIME_ESTIMATED);
        // String tasksCompleted;
        // String tasksStarted;
        // String tasksBlocked;
        // String timeTotal;
        // String timeDetails; // deep work
        // String timeOnGoal;
        // String taskDetails = getTaskDetails(arg);
        // String goalDetails;
        // String glory;
        // String notes;
        return report;
    }

    private static String getReportName(Session session) {
        return session.getName() + " Report";
    }

    public enum REPORT_TYPE {
        SESSION, DAILY, WEEKLY, MILESTONE
    }
}
