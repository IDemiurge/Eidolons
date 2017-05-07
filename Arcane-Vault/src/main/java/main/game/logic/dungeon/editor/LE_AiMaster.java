package main.game.logic.dungeon.editor;

import main.entity.obj.Obj;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.game.logic.dungeon.editor.logic.AiGroupData;
import main.swing.generic.services.dialog.DialogMaster;

public class LE_AiMaster {
    static String[] options = {"Edit Group", "New Group", "Show Group", "Remove Group",};
    static String[] edit_options = {"Pick Leader", "Edit Members", "Edit Behaviors",
            "Edit params", "Auto-Reset Members"};

    public static void editAI(Obj obj) {
        int result = DialogMaster.optionChoice("What to do?", options);
        if (result < 0) {
            return;
        }
        AiGroupData group = getAiGroup(obj);
        switch (options[result]) {
            case "Edit Group":
                editGroup(group);
                break;
            case "Show Group":
                showGroup(group);
                break;
            case "Remove Group":
                removeGroup(group);
                break;
            case "New Group":
                newGroup(obj);
                break;
        }
    }

    private static void showGroup(AiGroupData group) {

    }

    private static AiGroupData getAiGroup(Obj obj) {
        for (AiGroupData group : LevelEditor.getCurrentLevel().getAiGroups()) {
            if (group.getLeader().equals(obj)) {
                return group;
            }
        }
        return null;
    }

    private static void removeGroup(AiGroupData group) {
        LevelEditor.getCurrentLevel().getAiGroups().remove(group);
        resetAiGroups();
    }

    private static void resetAiGroups() {
        // GroupManager.

    }

    private static void editGroup(AiGroupData aiGroup) {
        // if (aiGroup.getMembers(). contains())
        // aiGroup.remove();
    }

    private static void newGroup(Obj obj) {
        AiGroupData aiGroup = new AiGroupData(obj);
        while (true) {
            Coordinates c = LevelEditor.getMouseMaster().pickCoordinate();
            if (c == null) {
                break;
            }
            // per level!
            for (Obj u : LevelEditor.getSimulation().getUnitsForCoordinates(c)) {

                ObjAtCoordinate objAtCoordinate = new ObjAtCoordinate(u.getType(), c);

                aiGroup.add(objAtCoordinate);

            }
        }
        LevelEditor.getCurrentLevel().getAiGroups().add(aiGroup);
    }

    public enum EDIT_OPTION {

    }

    public enum AI_GROUP_PARAM {

    }

}
