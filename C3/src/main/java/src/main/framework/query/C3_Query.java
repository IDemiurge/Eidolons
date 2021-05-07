package src.main.framework.query;

import src.main.data.C3Enums;
import src.main.framework.C3Item;

public class C3_Query extends C3Item<C3Enums.QueryCategory> {

    public C3_Query(C3Enums.QueryCategory category, String subCategory, String text) {
        super(category, subCategory, text);
    }

    @Override
    public String toString() {
        return "C3_Query: " +
                "category=" + category +
                ", subCategory='" + subCategory + '\'' +
                ", text='" + text + '\'';
    }
}
