package task;

import data.C3Enums;

public class C3_Task {
    protected C3Enums.TaskCategory category;
    protected String subcategory;
    protected String text;

    public C3_Task(C3Enums.TaskCategory category, String subcategory, String text) {
        this.category = category;
        this.subcategory = subcategory;
        this.text = text;
    }

    public C3Enums.TaskCategory getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getText() {
        return text;
    }
}
