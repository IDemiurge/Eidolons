package data;

import framework.C3Handler;
import framework.C3Manager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import main.system.datatypes.WeightMap;

import java.util.*;

import static data.C3Writer.categorySplit;
import static data.C3Writer.subcategorySplit;

public class C3Reader extends C3Handler {
    private static String QCATEGORY_MAP = "";
    private static String TCATEGORY_MAP = "";
    public static final String QUERY_DATA_PATH = "resources/query_map.txt";
    public static final String TASK_DATA_PATH = "resources/task_map.txt";
    public static final String TASK_STATUS_PATH = "resources/task_status_map.txt";

    public C3Reader(C3Manager manager) {
        super(manager);
        //IDEA - modify this map in string form persistenly every time a category is picked!
        for (C3Enums.QueryCategory category : C3Enums.QueryCategory.values()) {
            QCATEGORY_MAP += category + StringMaster.wrapInParenthesis(category.weight + "") + ";";
        }
        for (C3Enums.TaskCategory category : C3Enums.TaskCategory.values()) {
            TCATEGORY_MAP += category + StringMaster.wrapInParenthesis(category.weight + "") + ";";
        }
        //TODO only add categories that have tasks!!!
    }


    public WeightMap createQCategoryMap() {
        return new WeightMap<>(QCATEGORY_MAP, C3Enums.QueryCategory.class);
    }

    public WeightMap createTCategoryMap() {
        return new WeightMap<>(TCATEGORY_MAP, C3Enums.QueryCategory.class);
    }

    public Map<C3Enums.TaskCategory, Map<String, List<String>>> readTaskData() {
        return readDataMap(C3Enums.TaskCategory.class, false);
    }

    public Map<C3Enums.QueryCategory, Map<String, List<String>>> readQueryData() {
        return readDataMap(C3Enums.QueryCategory.class, true);
    }

    public <T extends C3Enums.Category> Map<T, Map<String, List<String>>> readDataMap(Class<T> clazz, boolean query) {
        String contents = FileManager.readFile(query ? QUERY_DATA_PATH : TASK_DATA_PATH);
        String[] categories = contents.split(categorySplit);
        Map<T, Map<String, List<String>>> map = new LinkedHashMap<>();
        for (String category : categories) {
            category = category.trim();
            if (category.isEmpty() || category.indexOf("\n") == -1) {
                continue;
            }
            String cat = category.substring(0, category.indexOf("\n")).trim().toLowerCase(Locale.ROOT);
            category = category.substring(category.indexOf("\n"));
            String[] subs = category.split(subcategorySplit);
            List<String> subList = new LinkedList<>();
            Map<String, List<String>> submap = new LinkedHashMap<>();
            for (String subQueries : subs) {
                subQueries = subQueries.trim();
                if (subQueries.isEmpty()) {
                    continue;
                }
                try {
                    String sub = subQueries.substring(0, subQueries.indexOf("\n")).trim().toLowerCase(Locale.ROOT);
                    String subcategory = subQueries.substring(subQueries.indexOf("\n"));
                    subList.add(sub);
                    String[] queries = StringMaster.splitLines(subcategory);
                    List<String> list = new ArrayList<>(queries.length);
                    list.addAll(Arrays.asList(queries));
                    submap.put(sub, list);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            try {
                T queryCategory = new EnumMaster<T>().retrieveEnumConst(clazz, cat);
                queryCategory.setSubcategories(subList.toArray(new String[subList.size()]));
                map.put(queryCategory, submap);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return map;
    }

    public Map<String, String> readTaskStatusMap() {
        String contents = FileManager.readFile(TASK_STATUS_PATH);
        return new DataUnitFactory<>().deconstructDataString(contents);

    }
}
