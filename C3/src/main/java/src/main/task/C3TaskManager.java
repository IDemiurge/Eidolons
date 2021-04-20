package src.main.task;

import src.main.framework.C3Filter;
import src.main.framework.C3Handler;
import src.main.framework.C3Manager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import main.system.util.DialogMaster;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static src.main.data.C3Enums.*;

public class C3TaskManager extends C3Handler {

    private final String separator = "::";
    private final String separator_task = ">>";
    WeightMap<TaskCategory> categoriesWeightMap;
    Map<TaskCategory, Map<String, List<String>>> taskTextMap;
    private Map<String, String> taskStatusMap;

    C3Filter<TaskCategory> filter;

    public C3TaskManager(C3Manager manager, WeightMap<TaskCategory> categoriesWeightMap,
                         Map<TaskCategory, Map<String, List<String>>> taskTextMap,
                         Map<String, String> taskStatusMap,
                         C3Filter<TaskCategory> filter) {
        super(manager);
        this.categoriesWeightMap = categoriesWeightMap;
        this.taskTextMap = taskTextMap;
        this.taskStatusMap = taskStatusMap;

        this.filter = filter;
    }

    public void tasksPostponed(List<C3_Task> tasks) {
        //add to backlog...
        for (C3_Task task : tasks) {
            //TODO what if we aint got the  cat/sub?
            taskTextMap.get(task.getCategory()).get(task.getSubCategory()).add(task.getText());
        }
        persist();
    }

    public void tasksCompleted(C3_Task... tasks) {
        for (C3_Task task : tasks) {
            taskTextMap.get(task.getCategory()).get(task.getSubCategory()).remove(task.getText());
        }
        persist();
    }

    public List<String> getTaskNamesFor(TaskCategory... categories) {
        List<String> list = new LinkedList<>();
        for (TaskCategory category : categories) {
            if (taskTextMap.get(category) != null)
                for (String sub : taskTextMap.get(category).keySet()) {
                    for (String s : taskTextMap.get(category).get(sub)) {
                        list.add(category + separator_task + sub + separator_task + s);
                    }
                }
        }
        return list;
    }

    public List<C3_Task> getTasksFor(List<String> names) {
        List<C3_Task> list = new LinkedList<>();
        for (String name : names) {
            // List objs =
            //         new MapMaster<C3_Task>().searchLayered(taskTextMap, obj -> src.main.task.category == obj);
            String[] split = name.split(separator_task);
            TaskCategory cat = new EnumMaster<TaskCategory>().retrieveEnumConst(TaskCategory.class, split[0]);
            list.add(new C3_Task(cat, split[1], split[2], ""));
        }
        return list;
    }

    public C3_Task createRandomTask() {
        TaskCategory category = categoriesWeightMap.getRandomByWeight();
        return createTask(false, category);
    }

    public C3_Task createTask(boolean custom) {
        return createTask(null, custom);
    }

    public C3_Task createTask(C3Filter<TaskCategory> filter, boolean custom) {
        if (filter != null) {
            TaskCategory[] categories = filter.getCategory();
            TaskCategory category = new EnumMaster<TaskCategory>().selectEnum(TaskCategory.class,
                    Arrays.asList(categories));
            return createTask(custom, category);
        }
        TaskCategory category = new EnumMaster<TaskCategory>().selectEnum(TaskCategory.class);
        return createTask(custom, category);
    }

    private C3_Task createTask(boolean custom, TaskCategory category) {
        return createTask(category, false, false, custom);
    }

    private C3_Task createTask(TaskCategory category, boolean randomSub, boolean randomTask, boolean customTask) {
        Map<String, List<String>> pool = taskTextMap.get(category);
        String task = null;
        Object sub = null;
        String taskString = null;
        if (customTask) {
            sub = DialogMaster.inputText("Sub-category?");
            taskString = DialogMaster.inputText("Input src.main.task text");
        } else {
            int random = RandomWizard.getRandomInt(pool.size());
            sub = randomSub ? pool.keySet().toArray()[random] : chooseSub(pool);
            random = RandomWizard.getRandomInt(pool.get(sub).size());
            taskString = randomTask ? pool.get(sub).get(random) :
                    chooseTask(pool.get(sub));
        }
        task = taskString.split(separator)[0];
        String comments = "";
        if (taskString.contains(separator)) {
            comments = taskString.split(separator)[1].replace(separator, "");
        }
        String string = taskStatusMap.get(task);
        CodeTaskStatus status = new EnumMaster<CodeTaskStatus>().retrieveEnumConst(CodeTaskStatus.class, string);
        if (status == null) {
            status = CodeTaskStatus.Review;
        }
        return new C3_Task(category, sub.toString(), task, comments);

    }

    public void taskSucceeded(C3_Task task) {
/*
tasks should be possible to postpone etc!
upgrade src.main.task status?
 */
    }

    public void upgradeStatus(C3_Task task, CodeTaskStatus status) {
        taskStatusMap.put(task.getText(), status.toString());
        persist();

    }

    public void persist() {
        manager.getWriter().writeMap(taskTextMap, false);
        manager.getWriter().writeTaskStatusMap(taskStatusMap);
    }

    public void addTask() {
        TaskCategory[] vals = TaskCategory.values();
        Object input = JOptionPane.showInputDialog(null, "Task category?", "Input", JOptionPane.QUESTION_MESSAGE, null, vals, vals[0]);

    }

    private String chooseTask(List<String> strings) {
        Object option = DialogMaster.getChosenOption("Select src.main.task", strings.toArray());
        if (option != null) {
            return option.toString();
        }
        return null;
    }

    private Object chooseSub(Map<String, List<String>> pool) {
        Object option = DialogMaster.getChosenOption("Subcategory?", pool.keySet().toArray());
        if (option != null) {
            return option.toString();
        }
        return null;
    }

    public CodeTaskStatus getStatus(String text) {
        String string = taskStatusMap.get(text);
        return new EnumMaster<CodeTaskStatus>().retrieveEnumConst(CodeTaskStatus.class, string);
    }

}
