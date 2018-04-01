package main.session;

import main.ArcaneMaster;
import main.ArcaneTower;
import main.content.ContentManager;
import main.content.VALUE;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.enums.StatEnums.SESSION_STATUS;
import main.file.CaptureParser;
import main.file.ReportGenerator;
import main.file.VersionMaster;
import main.gui.SessionWindow;
import main.io.PromptMaster;
import main.logic.*;
import main.stats.StatsMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.TimeMaster;
import eidolons.system.text.NameMaster;
import main.time.ZeitMaster;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class SessionMaster {

    public static final String TEMPLATE = "Template";
    public static final String PENDING = "Pending";
    static SessionWindow sessionWindow;
    private static List<Session> sessions = new ArrayList<>();
    private static VALUE[] copiedVals = {
            // AT_PARAMS.
    };
    private static boolean testMode;

    public static List<Session> getSessions() {
        return sessions;
    }

    public static Session getSession() {
        if (sessionWindow != null) {
            return sessionWindow.getSession();
        }
        return sessions.get(0);
    }

    public static void setActiveWindow(SessionWindow window) {
        sessionWindow = window;
    }

    public static Session reviveLastSession() {
        ObjType type = getLastSessionType();
        // type.setParam(time, x);

        // tasks, goals... updated
        return newSession(type, false);
    }

    public static void generateSessionTypes() {
        for (ObjType type : DataManager.getTypes(AT_OBJ_TYPE.DIRECTION)) {
            String name = type.getName() + " Session";
            ObjType e = DataManager.getType(name, AT_OBJ_TYPE.DIRECTION);
            if (e != null) {
                continue;
            }
            e = new ObjType(name, AT_OBJ_TYPE.SESSION);
            e.setProperty(AT_PROPS.SESSION_TYPE, TEMPLATE);
            DataManager.addType(e);
        }
    }

    public static Session createSessionFromText() {
        return createSessionFromText(DialogMaster.inputText("Session data"), false);
    }

    public static Session createSessionFromText(String text, boolean mergeWithLast) {
        String typeName = getSessionName(text);
        Entity oldType = null;
        if (mergeWithLast) {
            oldType = ZeitMaster.getLatest(AT_OBJ_TYPE.SESSION, true);
            typeName = oldType.getName();
            typeName = NameMaster.getUniqueVersionedName(DataManager.getTypes(AT_OBJ_TYPE.SESSION),
                    typeName);

        }
        typeName = DialogMaster.inputText("Session/Outcome Name?", typeName);
        // TODO fill out - deadline, priority, type

        ObjType type = new ObjType(typeName, AT_OBJ_TYPE.SESSION);
        // type.setGroup(group, false);
        if (oldType != null) {
            type.copyValues(oldType, copiedVals);
            type.copyValues(oldType, ContentManager.getValuesForType(AT_OBJ_TYPE.SESSION.getName(),
                    false));
        }
        Session session = getNewSession(type, false);
        CaptureParser.initSessionCapture(session, text);
        session.setLocked(true);
        // initSession(session);
        session.toBase();
        SessionWindow window = showInWindow(session);
        window.refresh();
        return session;
    }

    private static String getSessionName(String text) {
        String name = null;
        if (name == null) {
            name = TimeMaster.getDateString() + " Session from Capture";
        }
        return name;
    }

    public static ObjType getLastSessionType() {
        List<ObjType> types = DataManager.getTypes(AT_OBJ_TYPE.SESSION);
        Collections.sort(types, new Comparator<ObjType>() {

            @Override
            public int compare(ObjType arg0, ObjType arg1) {
                // int time = t.getIntParam(AT_PARAMS.time_)
                if (arg0.checkProperty(AT_PROPS.SESSION_STATUS, "" + SESSION_STATUS.UNFINISHED)) {
                    if (!arg1
                            .checkProperty(AT_PROPS.SESSION_STATUS, "" + SESSION_STATUS.UNFINISHED)) {
                        return -1;
                    }
                }
                if (!arg0.checkProperty(AT_PROPS.SESSION_STATUS, "" + SESSION_STATUS.UNFINISHED)) {
                    if (arg1.checkProperty(AT_PROPS.SESSION_STATUS, "" + SESSION_STATUS.UNFINISHED)) {
                        return 1;
                    }
                }
                int time = arg0.getIntParam(AT_PARAMS.TIME_LAST_MODIFIED);
                int time2 = arg1.getIntParam(AT_PARAMS.TIME_LAST_MODIFIED);
                if (time > time2) {
                    return -1;
                }
                return 1;
            }
        });
        return types.get(0);
    }

    public static void saveSessionAsTemplate(Entity e) {
        e.setProperty(AT_PROPS.SESSION_TYPE, TEMPLATE);
        // TODO prompt / clean values?
        XML_Writer.writeXML_ForType(e.getType());
    }

    public static Session newSession(boolean alt) {
        return newSession(0);
    }

    public static Session newSession() {
        if (ArcaneTower.isTestMode() && testMode) {
            return continueSession(true);
        }
        int result = DialogMaster.optionChoice("How to create your Session?", "From Capture Text",
                "From Direction", "Continue Pending", "Continue Last");
        return newSession(result);
    }

    private static Session newSession(int option) {
        ObjType type;
        switch (option) {
            case 0:
                return createSessionFromText();
            case 1:
                ObjType directionType = ListChooser.chooseType_(AT_OBJ_TYPE.DIRECTION);
                type = new ObjType(directionType.getName() + " Session", AT_OBJ_TYPE.SESSION);
                type.setGame(ArcaneTower.getSimulation());
                type.setProperty(AT_PROPS.DIRECTION, directionType.getName());
                return newSession(type, true);
            case 2:
                return continueSession(false);
            case 3:
                return continueSession(true);
        }
        return null;
    }

    public static Session continueSession(boolean last) {
        ObjType type;
        if (last) {
            type = ZeitMaster.getLatest(AT_OBJ_TYPE.SESSION, AT_PARAMS.TIME_STARTED);
        } else {
            type = ListChooser.chooseTypeFromGroup_(AT_OBJ_TYPE.SESSION, PENDING);
        }
        // increment(at_params.number_of_starts);
        return newSession(type, false);
    }

    public static Session newSession(ObjType type, boolean prompt) {
        Session session = getNewSession(type, prompt);
        session.toBase();
        showInWindow(session);
        return session;
    }

    private static SessionWindow showInWindow(Session session) {
        SessionWindow window = new SessionWindow(session);
        window.refresh();
        session.setWindow(window);
        // session.setStatus();
        // session.start();
        session.setProperty(AT_PROPS.SESSION_TYPE, PENDING);
        session.setParam(AT_PARAMS.TIME_LAST_MODIFIED, ZeitMaster.getTime());
        VersionMaster.saveVersionFolder();
        return window; // start timers, set status,
    }

    private static Session getNewSession(ObjType type, boolean prompt) {
        ObjType templateType = new ObjType(type);
        VersionMaster.setVersionToCurrent(templateType);
        Session session = (Session) ArcaneTower.getSimulation().getInstance(type);
        sessions.add(session);
        if (prompt) {
            initSession(session);
            PromptMaster.preSessionPrompt(session, false);
        }
        return session;
    }

    private static void initSession(Session session) {
        addGoals(session);
        addTasks(session);
        session.toBase();
        session.setLocked(true);
    }

    private static void addTasks(Session session) {
        List<ObjType> list = DataManager.toTypeList(getAllTasks(session));
        new ArrayList<>(DataManager.getTypes(AT_OBJ_TYPE.TASK));
        // list = (List<ObjType>) FilterMaster.filterByProp(list,
        // AT_PROPS.TASK_STATUS,
        // TASK_STATUS.PENDING);

        // if (DialogMaster.confirm("Select tasks for this Session?"))
        // list = ListChooser.chooseTypes_(list);

        session.setTasks(ArcaneTower.getTasks(list));
        // session.setProperty(AT_PROPS.TASKS, DataManager.toString(list));
    }

    private static void addGoals(Session session) {
        List<ObjType> list =
                // ArrayList<>(DataManager.getTypes(AT_OBJ_TYPE.GOAL));
                DataManager.toTypeList(session.getDirection().getGoals());
        // list = (List<ObjType>) FilterMaster.filterByProp(list,
        // AT_PROPS.GOAL_STATUS,
        // TASK_STATUS.PENDING);
        ListChooser.setTooltip("Order by priority");
        list = ListChooser.chooseTypes_(list);
        List<Goal> goals = ArcaneTower.getGoals(list);
        ArcaneMaster.setPriorityTopToBottom(goals, 100, true);
        session.setGoals(goals);
        // session.setProperty(AT_PROPS.GOAL, DataManager.toString(list));

    }

    public static List<Task> getAllTasks(Session session) {
        List<Task> list = new ArrayList<>();
        for (Goal sub : session.getDisplayedGoals()) {
            list.addAll((sub.getTasks()));
        }
        return list;
    }

    public static void sessionAutoSave(Session session) {
        ArcaneTower.saveEntity(session, true);
    }

    public static void sessionPaused(Session session) {
        ZeitMaster.paused(session);
        ArcaneTower.saveEntity(session, true);
    }

    public static void sessionResumed(Session session) {
        ZeitMaster.resumed(session);
        ArcaneTower.saveEntity(session, true);
    }

    public static void sessionStarted(Session session) {
        ZeitMaster.started(session);
        session.getWindow().started();
        ArcaneTower.saveEntity(session, true);
    }

    public static void sessionFinished(Session session) {
        ArcaneTower.getDay().sessionDone(session);
        if (DialogMaster.confirm("Save session as pending?")) {
            session.setProperty(AT_PROPS.SESSION_TYPE, PENDING);
        }
        ArcaneTower.saveEntity(session, true);
        List<Task> tasks = session.getTasks();
        for (Task sub : new ArrayList<>(tasks)) {
            if (sub.isDone()) {
                tasks.remove(sub);
            }
        }
        session.resetPropertyFromList(AT_PROPS.TASKS, tasks);
        // save

        // boolean result = PromptMaster.afterSessionPrompt(session);
        // CheckMaster.checkOutAll();
        // session.getWindow().close();

        // applySessionStats(session);
        // extractSessionStats(session);
        // VersionMaster.version_session++;

        ReportGenerator.newReport(session);
        // MainWriter.save();
    }

    private static void extractSessionStats(Session session) {
//		TaskMaster.updateTasks(session);
//		// TaskMaster.updateTasks(session);
//		TimeMaster.getTimeStamp();
        XML_Writer.saveAll();

    }

    private static void applySessionStats(Session session) {
        for (VALUE stat : StatsMaster.getSessionStats().keySet()) {
            // session.mod

        }

    }
}
