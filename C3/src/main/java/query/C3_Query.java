package query;

import data.C3Enums;

public class C3_Query {
    C3Enums.QueryCategory category;
    String subCategory;
    String text;

    public C3_Query(C3Enums.QueryCategory category, String subCategory, String text) {
        this.category = category;
        this.subCategory = subCategory;
        this.text = text;
    }
}
