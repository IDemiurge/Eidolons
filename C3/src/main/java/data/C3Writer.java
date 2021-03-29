package data;

import framework.C3Handler;
import framework.C3Manager;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnitFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static data.C3Reader.*;

public class C3Writer extends C3Handler {

    public static final String categorySplit = "===";
    public static final String subcategorySplit = "---";

    public C3Writer(C3Manager manager) {
        super(manager);
    }

    public C3Node mapToNode(Map map) {
        List<C3Node> categories = new LinkedList<>();
        C3Node root = new C3Node("root", categories);
        for (Object o : map.keySet()) {
            List<C3Node> subs = new LinkedList<>();
            categories.add(new C3Node(o, subs));
            Map subMap = (Map) map.get(o);
            for (Object sub : subMap.keySet()) {
                List list = (List) subMap.get(sub);
                list = (List) list.stream().map(obj -> new C3Node(obj, null)).collect(Collectors.toList());
                subs.add(new C3Node(o, list));
            }
        }
        return root;
    }

    public void writeMap(Map map, boolean queryOrTask) {
        C3Node c3Node = mapToNode(map);
        String path = queryOrTask ? QUERY_DATA_PATH : TASK_DATA_PATH;
        write(c3Node, path);
    }

    public void writeTaskStatusMap(Map<String, String> taskStatusMap) {
        String contents = new DataUnitFactory<>()
                .setValues(taskStatusMap.values().toArray(new String[0]))
                .setValueNames(taskStatusMap.keySet().toArray())
                .constructDataString();
        FileManager.write(contents, TASK_STATUS_PATH);
    }
    public void write(C3Node root, String path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (C3Node category : root.children) {
            stringBuilder.append(category.value + "\n");
            for (C3Node sub : category.children) {
                stringBuilder.append(sub.value + "\n");
                for (C3Node query_task : sub.children) {
                    stringBuilder.append(query_task.value + "\n");
                }
                stringBuilder.append(subcategorySplit + "\n");
            }
            stringBuilder.append(categorySplit + "\n");
        }
        FileManager.write(stringBuilder.toString(), path);
    }

}
