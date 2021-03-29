package task;

import framework.C3Filter;
import framework.C3Handler;
import framework.C3Manager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.datatypes.WeightMap;
import main.system.util.DialogMaster;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static data.C3Enums.*;

public class C3TaskManager extends C3Handler {

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

        this.filter = filter;}

    public C3_Task createRandomTask() {
        TaskCategory category = categoriesWeightMap.getRandomByWeight();
        return createTask(category);
    }

    public C3_Task createTask() {
        if (filter != null) {
            TaskCategory[] categories = filter.getCategory();
            TaskCategory category = new EnumMaster<TaskCategory>().selectEnum(TaskCategory.class,
                    Arrays.asList(categories));
            return createTask(category);
        }
        TaskCategory category = new EnumMaster<TaskCategory>().selectEnum(TaskCategory.class );
        return createTask(category);
    }

    private C3_Task createTask(TaskCategory category ) {
        return createTask(category, false, false);
    }
    private C3_Task createTask(TaskCategory category, boolean randomSub, boolean randomTask) {
        Map<String, List<String>> pool = taskTextMap.get(category);
        int random = RandomWizard.getRandomInt(pool.size());

        Object sub =randomSub ?   pool.keySet().toArray()[random] : chooseSub(pool);
        random = RandomWizard.getRandomInt(pool.get(sub).size());

        String taskString =randomTask ?  pool.get(sub).get(random) : chooseTask(pool.get(sub));

        String task = taskString.split("::")[0];
        String comments = "";
        if (taskString.contains("::") ){
            comments =  taskString.split("::")[1].replace("::", "");
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
upgrade task status?
 */
    }

    public void upgradeStatus(C3_Task task, CodeTaskStatus status) {
        taskStatusMap.put(task.getText(), status.toString());
        persist();

    }

    public void persist() {
        manager.getWriter().writeMap(taskTextMap, true);
        manager.getWriter().writeTaskStatusMap(taskStatusMap);
    }

    public void addTask() {
        TaskCategory[] vals = TaskCategory.values();
        Object input = JOptionPane.showInputDialog(null, "Task category?", "Input", JOptionPane.QUESTION_MESSAGE, null, vals, vals[0]);

    }

    private String chooseTask(List<String> strings) {
        Object option = DialogMaster.getChosenOption("Select task", strings.toArray());
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
