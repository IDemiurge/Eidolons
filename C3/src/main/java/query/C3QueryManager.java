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

public class C3QueryManager  extends C3Handler {
    WeightMap<C3Enums.QueryCategory> categoriesWeightMap;
    Map<C3Enums.QueryCategory, Map<String, List<String>>> queryTextMap;

    public C3QueryManager(C3Manager manager, WeightMap<C3Enums.QueryCategory> categoriesWeightMap, Map<C3Enums.QueryCategory, Map<String, List<String>>> queryTextMap) {
        super(manager);
        this.categoriesWeightMap = categoriesWeightMap;
        this.queryTextMap = queryTextMap;
    }


    public C3_Query createRandomQuery() {
        C3Enums.QueryMode mode = C3Enums.QueryMode.normal;
        C3Enums.QueryCategory category = getCategory(mode);
        String sub = getSubcategory(category);
        String text = getQueryText(category, sub);
        return new C3_Query(category, sub, text);
    }

    private String getQueryText(C3Enums.QueryCategory category, String sub) {
        for (Map<String, List<String>> value : queryTextMap.values()) {
            List<String> list = value.get(sub.toLowerCase(Locale.ROOT));
            if (list != null) {
                return list.get(RandomWizard.getRandomIndex(list));
            }
        }
        return "No valid queries for this category!";
    }

    public void persist() {
        manager.getWriter().writeMap(queryTextMap, true);
    }

    public void querySucceeded(C3_Query query) {
        C3Enums.QueryCategory key = query.category;
        Integer decremented = categoriesWeightMap.get(key);
        categoriesWeightMap.put(key, decremented);
        queryTextMap.get(query.subCategory).remove(query.text);
        persist();
    }

    private String getSubcategory(C3Enums.QueryCategory category) {
        return category.subcategories[RandomWizard.getRandomInt(category.subcategories.length)];
    }

    private C3Enums.QueryCategory getCategory(C3Enums.QueryMode mode) {

        return C3Enums.QueryCategory.CS;
    }

    public void addQuery() {
        Object[] vals = C3Enums.QueryCategory.values();
        Object input = JOptionPane.showInputDialog(null, "Query category?", "Input", JOptionPane.QUESTION_MESSAGE, null, vals, vals[0]);
        if (input == null)
            return;
        C3Enums.QueryCategory category = (C3Enums.QueryCategory) input;
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
