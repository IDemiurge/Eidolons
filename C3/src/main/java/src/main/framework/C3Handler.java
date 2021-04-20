package src.main.framework;

import src.main.data.C3Enums;

public class C3Handler {
    protected C3Manager manager;

    public C3Handler(C3Manager manager) {
        this.manager = manager;
    }

    public C3Manager getManager() {
        return manager;
    }

    static {
        C3Enums.Direction.Code.setCategories(new C3Enums.TaskCategory[]{
                C3Enums.TaskCategory.Code_Revamp,
                C3Enums.TaskCategory.Code_New,
                C3Enums.TaskCategory.Bug_fixing,
        });
        C3Enums.Direction.Design.setCategories(new C3Enums.TaskCategory[]{
                C3Enums.TaskCategory.Content_Design,
                C3Enums.TaskCategory.Global_Game_Design,
                C3Enums.TaskCategory.System_Design,
                C3Enums.TaskCategory.Lore_Writing,
        });
        C3Enums.Direction.Project.setCategories(new C3Enums.TaskCategory[]{
                C3Enums.TaskCategory.Team_management,
                C3Enums.TaskCategory.Public_Writing,
        });
        C3Enums.Direction.Meta.setCategories(new C3Enums.TaskCategory[]{
                //TODO
        });
    }
}
