package main.handlers.util;

import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.gui.builders.TreeViewBuilder;
import main.launch.ArcaneVault;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * sort existing tabs/trees
 *
 * @author JustMe
 */
public class TypeFilter {

    public static final String TAB_TITLE = "filter";
    public static int filterN = 1;
    static TreeViewBuilder treeBuilder;
    private static boolean filtering = false;
    private static String sub;
    private static JComponent tree;
    private Condition condition;
    private OBJ_TYPE TYPE;

    public TypeFilter(Condition c, OBJ_TYPE TYPE) {
        this.condition = c;
        this.TYPE = TYPE;
    }

    public static void filter(Filter<ObjType> filter, VALUE groupingValue) {

        List<String> typeList = DataManager
                .toStringList(new ArrayList<>(filter.getTypes()));
        treeBuilder = new TreeViewBuilder(typeList, sub, filter.getTYPE()
                .getName());
        tree = treeBuilder.build();
        setFiltering(true);
        filterN++;
        ArcaneVault.getMainBuilder().refresh();
    }

    public static boolean isFiltering() {
        return filtering;
    }

    public static void setFiltering(boolean filtering) {
        TypeFilter.filtering = filtering;
    }

    public static JComponent getTree() {
        return tree;
    }

    public List<ObjType> getTypes() {
        Ref ref = new Ref(ArcaneVault.getGame());
        return new Filter<ObjType>(ref, condition, (TYPE)).getTypes();
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPE tYPE) {
        TYPE = tYPE;
    }

}
