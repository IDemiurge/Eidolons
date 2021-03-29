package query;

import com.graphbuilder.math.FuncNode;
import data.C3Enums;
import data.C3Writer;
import framework.C3Handler;
import framework.C3Manager;
import main.data.xml.XML_Reader;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.WeightMap;

import javax.swing.*;
import java.util.*;

import static data.C3Enums.*;

public class C3QueryManager extends C3Handler {
    WeightMap<QueryCategory> categoriesWeightMap;
    Map<QueryCategory, Map<String, List<String>>> queryTextMap;

    public C3QueryManager(C3Manager manager, WeightMap<QueryCategory> categoriesWeightMap, Map<QueryCategory, Map<String, List<String>>> queryTextMap) {
        super(manager);
        this.categoriesWeightMap = categoriesWeightMap;
        this.queryTextMap = queryTextMap;
    }

    public void persist() {
        manager.getWriter().writeMap(queryTextMap, true);
    }

    public void querySucceeded(C3_Query query) {
        QueryCategory key = query.getCategory();
        Integer decremented = categoriesWeightMap.get(key);
        categoriesWeightMap.put(key, decremented);
        queryTextMap.get(query.getSubCategory()).remove(query.getText());
        persist();
    }

    public C3_Query createRandomQuery() {
        QueryMode mode = QueryMode.normal;
        QueryCategory category = getCategory(mode);
        return createQuery(category);
    }

    public C3_Query createQuery() {
        QueryCategory category = new EnumMaster<QueryCategory>().selectEnum(QueryCategory.class);
        return createQuery(category);
    }

    private C3_Query createQuery(QueryCategory category) {
        String sub = getSubcategory(category);
        String text = getQueryText(category, sub);
        return new C3_Query(category, sub, text);
    }

    private String getQueryText(QueryCategory category, String sub) {
        Map<String, List<String>> subcateg = queryTextMap.get(category);
        List<String> list = subcateg.get(sub.toLowerCase(Locale.ROOT));
        if (list != null) {
            return list.get(RandomWizard.getRandomIndex(list));
        }
        return "No valid queries for " + category + "::" + sub + "!";
    }

    private String getSubcategory(QueryCategory category) {
        return category.subcategories[RandomWizard.getRandomInt(category.subcategories.length)];
    }

    private QueryCategory getCategory(QueryMode mode) {
        return   categoriesWeightMap.getRandomByWeight();
    }

    public void addQuery() {
        Object[] vals = QueryCategory.values();
        Object input = JOptionPane.showInputDialog(null, "Query category?", "Input", JOptionPane.QUESTION_MESSAGE, null, vals, vals[0]);
        if (input == null)
            return;
        QueryCategory category = (QueryCategory) input;
        vals = category.subcategories;
        input = JOptionPane.showInputDialog(null, "Query subcategory?", "Input", JOptionPane.QUESTION_MESSAGE, null, vals, vals[0]);
        if (input == null)
            return;
        String subcategory = input.toString();
        input = JOptionPane.showInputDialog("What's it about?");
        if (input == null)
            return;

        queryTextMap.get(category).get(subcategory).add(input.toString());
        persist();
    }
}
