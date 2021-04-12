package session;

import data.C3Enums;
import framework.C3Handler;
import framework.C3Manager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.util.DialogMaster;
import music.PlaylistHandler;
import task.C3_Task;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C3SessionHandler extends C3Handler {
    private C3Session currentSession;

    public C3SessionHandler(C3Manager manager) {
        super(manager);
    }

    private Integer getInterval(C3Session currentSession) {
        Object chosenOption = DialogMaster.getChosenOption("Break interval?", new Integer[]{
                10, 15, 20
        });
        return (Integer) chosenOption;
    }

    ;

    public Integer[] getDurationOptions(C3Enums.SessionType sessionType) {
        switch (sessionType) {
            case Preparation -> {
                return new Integer[]{
                        15, 30, 45
                };
            }
            case Perseverance -> {
                return new Integer[]{
                        90, 120, 150
                };
            }
            case Liberation_Short, Night_Short -> {
                return new Integer[]{
                        45, 60, 75
                };
            }
            case Liberation_Long, Night_Long -> {
                return new Integer[]{
                        75, 90, 120
                };
            }
            case Freedom -> {
                return new Integer[]{
                        60, 90, 120, 150
                };
            }
        }
        return null;
    }

    public void abortSession(C3Session session) {
        finished(session);
    }

    // via separate launch?
    public void initSession() {
        boolean manual = true;
        C3Enums.SessionType sessionType = null;
        if (manual) {
            sessionType = new EnumMaster<C3Enums.SessionType>().selectEnum(C3Enums.SessionType.class);
        } else {
            sessionType = getSessionTypeAuto();
        }

        C3Enums.Direction direction = new EnumMaster<C3Enums.Direction>().selectEnum(C3Enums.Direction.class);
        Integer[] durationOptions = getDurationOptions(sessionType);
        Integer duration = (Integer) DialogMaster.getChosenOption("What's the timing?", durationOptions);

        if (currentSession != null && !currentSession.isFinished()) {
            abortSession(currentSession);
        }
        currentSession = new C3Session(sessionType, direction, duration, "", "");
        Integer interval = getInterval(currentSession);
        currentSession.setMinsBreakInverval(interval);
        if (DialogMaster.confirm("Want some mus?"))
            try {
                initSessionMusic(sessionType, direction);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (DialogMaster.confirm("Choose tasks?")) initTasks(currentSession);
        manager.getTimerHandler().initTimer(currentSession);
    }


    private void initTasks(C3Session currentSession) {
        C3Enums.TaskCategory[] categories = getCategories(currentSession.getDirection());
        List<String> names = manager.getTaskManager().getTaskNamesFor(categories);
        String picked = ListChooser.chooseStrings(names);
        List<String> list = ContainerUtils.openContainer(picked);
        List<C3_Task> tasks = manager.getTaskManager().getTasksFor(list, categories);
        currentSession.setTasksPending(tasks);
    }

    private C3Enums.TaskCategory[] getCategories(C3Enums.Direction direction) {
        return direction.categories;
    }

    public void addTask(boolean custom) {
        C3_Task task = manager.getTaskManager().createTask(custom);
        currentSession.getTasksPending().add(task);
    }

    public void displayActiveTasks() {
        if (!currentSession.getTasksPending().isEmpty()) {
            DialogMaster.inform(StringMaster.formatList(currentSession.getTasksPending()));
        }
    }

    private void initSessionMusic(C3Enums.SessionType sessionType, C3Enums.Direction direction) {
        String musPath = getMusForSession(sessionType, direction);
        List<File> filesFromDirectory = FileManager.getFilesFromDirectory(musPath, false, false, false);
        String playlistPath = null;
        boolean randomMus = false;
        if (randomMus) {
            playlistPath = (String) RandomWizard.getRandomListObject(filesFromDirectory);
        } else {
            Collections.shuffle(filesFromDirectory);
            long maxChoiceSize = 3;
            Object[] m3uOptions = filesFromDirectory.stream().map(file -> file.getName()).limit(maxChoiceSize).collect(
                    Collectors.toList()
            ).toArray();
            playlistPath = (String) DialogMaster.getChosenOption("What's the tune?", m3uOptions);
        }
        if (!PlaylistHandler.play(playlistPath)) {
            PlaylistHandler.play(musPath, playlistPath);
        }

    }

    private String getMusForSession(C3Enums.SessionType sessionType, C3Enums.Direction direction) {
        return PlaylistHandler.ROOT_PATH_PICK + "SESSION/" +
                direction + "/" + sessionType + "/";
    }

    public C3Session getCurrentSession() {
        return currentSession;
    }


    public String getSessionInfo() {

        return currentSession.toString();
    }

    public void finished(C3Session session) {
        manager.getTaskManager().tasksCompleted(session.getTasksDone());
        manager.getTaskManager().tasksPostponed(session.getTasksPending());
        session.setFinished(true);
    }

    public void resetMusic() {
        initSessionMusic(currentSession.getSessionType(), currentSession.getDirection());
    }

    public void shiftBreak() {
        manager.getTimerHandler().shiftBreak();
    }


    private C3Enums.SessionType getSessionTypeAuto() {
        // int day = Calendar.getInstance().getTime().getDay();
        // int hour = Calendar.getInstance().getTime().getHours();
        // boolean altGymDay;
        // boolean weekend;
        // switch (day) {
        //     case 1:
        //     case 2:
        //     case 3:
        //     case 4:
        //     case 5:
        //     case 6:
        //     case 0:
        // }
        return null;
    }
}




















