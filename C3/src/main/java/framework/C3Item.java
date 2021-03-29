package framework;

import data.C3Enums;

public class C3Item<T extends C3Enums.Category> {
    protected T category;
    protected String subCategory;
    protected String text;

    public C3Item(T category, String subCategory, String text) {
        this.category = category;
        this.subCategory = subCategory;
        this.text = text;
    }

    public T getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getText() {
        return text;
    }

}
