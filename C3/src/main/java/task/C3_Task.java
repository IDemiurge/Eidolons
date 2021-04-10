package task;

import data.C3Enums;
import framework.C3Item;

public class C3_Task extends C3Item<C3Enums.TaskCategory> {
    protected String comments;

    public String getComments() {
        return comments;
    }

    public C3_Task(C3Enums.TaskCategory category, String subCategory, String text, String comments) {
        super(category, subCategory, text);
        this.comments = comments;
    }
}
