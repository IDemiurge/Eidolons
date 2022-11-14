package framework.session;

import data.C3Enums;
import framework.C3Handler;
import framework.C3Manager;
import framework.task.C3_Task;
import gui.TextRenderMaster;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.*;
import main.system.auxiliary.data.FileManager;
import main.system.util.DialogMaster;
import music.PlaylistHandler;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class C3SessionHandler extends C3Handler {
    public static final String MUS = "Want some mus?";
    public static final String TASKS = "Choose tasks?";
    public static final String TIMING = "What's the timing?";
    public static final String BREAK_INTERVAL = "Break interval?";
    //TODO pomodoro mode!
    private C3Session currentSession;
    public static final long musicMaxChoiceSize = 5;

    private Map<String, Object> defaultOptions = new HashMap<>();


    public C3SessionHandler(C3Manager manager) {
        super(manager);
    }

    private Integer getIntervals() {
        Object chosenOption = prompt(BREAK_INTERVAL, new Integer[]{
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
        return list.stream().map(i -> i * interval).collect(Collectors.toList()).toArray(new Integer[0]);
    }

    public void abortSession(C3Session session) {
        finished(session);
        manager.getSessionLogger().aborted(session);
    }


    public enum PromptType {
        text_input,
        option_choice,
        confirm,

    }

    private Object prompt(String query, Object arg, PromptType type) {
        Object o = defaultOptions.get(query);
        if (o != null)
            return o;
        switch (type) {
            case text_input -> {
                return DialogMaster.inputText(query); //, arg
            }
            case option_choice -> {
                return optionChoice(arg);
            }
            case confirm -> {
                return DialogMaster.confirm(query) ? new Object() : null;
            }
        }
        return o;
    }

    private boolean confirm(String query) {
        Object result = prompt(query, null, PromptType.confirm);
        if (result instanceof Boolean)
            return (boolean) result;
        return result !=null ;
    }
    private Object prompt(String query, Object arg) {
        return prompt(query, arg, PromptType.option_choice);
    }

    private Object optionChoice(Object arg) {
        Object options = arg;
        if (arg instanceof Class) {
            options = EnumMaster.getEnumConstants((Class<?>) arg).toArray();
        }
        return DialogMaster.getChosenOption("Choose one", options);
    }

    // via separate launch?
    public void initQuickSession() {
        defaultOptions.put(BREAK_INTERVAL, 20);
        defaultOptions.put(TIMING, 60);
        defaultOptions.put(MUS, false);
        defaultOptions.put(TASKS, false);
        defaultOptions.put(C3Enums.SessionType.class.getName(), C3Enums.SessionType.auto_quick);
        defaultOptions.put(C3Enums.Direction.class.getName(), C3Enums.Direction.Design);
        //auto-music

        initSession();
        defaultOptions.clear();
    }

    public void initSession() {
        boolean manual = true;
        C3Enums.SessionType sessionType = null;
        if (manual) {
            // sessionType = new EnumMaster<C3Enums.SessionType>().selectEnum(C3Enums.SessionType.class);
            sessionType = (C3Enums.SessionType) prompt(C3Enums.SessionType.class.getName(), C3Enums.SessionType.class);
        } else {
            sessionType = getSessionTypeAuto();
        }
        if (sessionType == null)
            return;
        C3Enums.Direction direction = (C3Enums.Direction) prompt(C3Enums.Direction.class.getName(), C3Enums.Direction.class);
        // C3Enums.Direction direction = new EnumMaster<C3Enums.Direction>().selectEnum(C3Enums.Direction.class);
        Integer interval = getIntervals();
        Integer[] durationOptions = getDurationOptions(sessionType.getMinBreaks(), sessionType.getMaxBreaks(), interval);

        Integer duration = (Integer) prompt(TIMING, durationOptions);

        if (currentSession != null && !currentSession.isFinished()) {
            abortSession(currentSession);
        }
        currentSession = new C3Session(sessionType, direction, duration, "", "");
        currentSession.setMinsBreakInverval(interval);
        if (confirm(MUS))
            try {
                initSessionMusic(sessionType, direction);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        if (confirm(TASKS)) initTasks(currentSession);
        manager.getTimerHandler().initTimer(currentSession);
    }


    private void initTasks(C3Session currentSession) {
        while (confirm("Add custom framework.task?")) {
            addTask(true);
        }
        if (!confirm("Add prepared tasks?"))
            return;
        C3Enums.TaskCategory[] categories = getCategories(currentSession.getDirection());
        List<String> names = manager.getTaskManager().getTaskNamesFor(categories);
        String picked = ListChooser.chooseStrings(names);
        List<String> list = ContainerUtils.openContainer(picked);
        List<C3_Task> tasks = manager.getTaskManager().getTasksFor(list);
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
            DialogMaster.inform("Pending tasks: \n" +
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
        if (currentSession == null) {
            return "Null session?";
        }
        return currentSession.toString() +
                StringMaster.lineSeparator
                // +"<html><center><u>"
                +currentSession.getDurationString()
                // +"</u></center></html>"
                //TODO +
                // StringMaster.lineSeparator +
                // currentSession.getGoal()
                ;
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




















