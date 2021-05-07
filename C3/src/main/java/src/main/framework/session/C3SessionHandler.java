package src.main.framework.session;

import src.main.data.C3Enums;
import src.main.framework.C3Handler;
import src.main.framework.C3Manager;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.*;
import main.system.auxiliary.data.FileManager;
import main.system.util.DialogMaster;
import music.PlaylistHandler;
import src.main.framework.task.C3_Task;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class C3SessionHandler extends C3Handler {
    //TODO pomodoro mode!
    private C3Session currentSession;
    public static final long musicMaxChoiceSize = 5;

    public C3SessionHandler(C3Manager manager) {
        super(manager);
    }

    private Integer getInterval(C3Session currentSession) {
        Object chosenOption = DialogMaster.getChosenOption("Break interval?", new Integer[]{
                10, 15, 20, 25
        });
        return (Integer) chosenOption;
    }

    ;

    public Integer[] getDurationOptions(int minBreaks, int max, Integer interval) {
        List<Integer> list = new LinkedList<>();
        for (int i = minBreaks; i <= max; i++) {
            list.add(i);
        }
        return list.stream().map(i-> i*interval).collect(Collectors.toList()).toArray(new Integer[0]);
    }

    public void abortSession(C3Session session) {
        finished(session);
        manager.getSessionLogger().aborted(session);
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
        Integer interval = getInterval(currentSession);
        Integer[] durationOptions = getDurationOptions(sessionType. getMinBreaks( ),sessionType. getMaxBreaks( ), interval);

        Integer duration =(Integer) DialogMaster.getChosenOption("What's the timing?", durationOptions);

        if (currentSession != null && !currentSession.isFinished()) {
            abortSession(currentSession);
        }
        currentSession = new C3Session(sessionType, direction, duration, "", "");
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
        while (DialogMaster.confirm("Add custom src.main.framework.task?")) {
            addTask(true);
        }
        if (!DialogMaster.confirm("Add prepared tasks?"))
            return;
        C3Enums.TaskCategory[] categories = getCategories(currentSession.getDirection());
        List<String> names = manager.getTaskManager().getTaskNamesFor(categories);
        String picked = ListChooser.chooseStrings(names);
        List<String> list = ContainerUtils.openContainer(picked);
        List<C3_Task> tasks = manager.getTaskManager().getTasksFor(list );
        currentSession.setTasksPending(tasks);
    }

    private C3Enums.TaskCategory[] getCategories(C3Enums.Direction direction) {
        return direction.categories;
    }

    public void taskDone() {
        List<C3_Task> tasks = currentSession.getTasksPending();
        String picked = ListChooser.chooseMultiple(tasks);
        List<Object> collect = ContainerUtils.openContainer(picked).stream().map(
                string -> SearchMaster.findClosest(string, tasks.toArray())).collect(Collectors.toList());
        for (Object o : collect) {
            //TODO input comments!
            C3_Task task = (C3_Task) o;
            String input = JOptionPane.showInputDialog("Any comments for [" + task.getText() + "] ?");
            manager.getTLogger().done(task, input);
            taskDone(task);
        }
    }

    private void taskDone(C3_Task task) {
        currentSession.getTasksPending().remove(task);
        currentSession.getTasksDone().add(task);
        manager.getTaskManager().tasksCompleted(task);
    }

    public void addTask(boolean custom) {
        C3_Task task = manager.getTaskManager().createTask(custom);
        currentSession.getTasksPending().add(task);
    }

    public void displayActiveTasks() {
        if (!currentSession.getTasksPending().isEmpty()) {
            DialogMaster.inform(                    "Pending tasks: \n"+
                    StringMaster.formatList(currentSession.getTasksPending()));
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
            Object[] m3uOptions = filesFromDirectory.stream().map(file -> file.getName()).limit(musicMaxChoiceSize).collect(
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

        return currentSession.toString()+
                StringMaster.lineSeparator+
                currentSession.getDurationString()+
                StringMaster.lineSeparator+
                currentSession.getTasksString();
    }

    public void finished(C3Session session) {
        taskDone();
        manager.getTaskManager().tasksPostponed(session.getTasksPending());
        session.setFinished(true);
    }

    public void resetMusic() {
        initSessionMusic(currentSession.getSessionType(), currentSession.getDirection());
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




















