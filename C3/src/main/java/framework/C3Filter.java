package framework;

import data.C3Enums;
import main.system.auxiliary.data.ArrayMaster;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class C3Filter<T extends C3Enums.Category> {
    T[] category;
    String subcategory;

    public C3Filter(String subcategory,T... category ) {
        this.category = category;
        this.subcategory = subcategory;
    }

    public T[] getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public boolean filter(C3Item item){
        if (!ArrayMaster.contains_(category, item.category)) {
            return false;
        }
        if (!subcategory.isEmpty())
        if (!subcategory.toLowerCase(Locale.ROOT)
                .contains(item.subCategory.toLowerCase(Locale.ROOT))) {
            return false;
        }
        return true;
    }

    public Map<T, Map<String, List<String>>> filter(Map<T, Map<String, List<String>>> readTaskData) {
        return readTaskData; //TODO
    }
}
