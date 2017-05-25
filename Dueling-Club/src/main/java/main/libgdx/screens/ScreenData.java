package main.libgdx.screens;

import main.libgdx.DialogScenario;
import main.system.EventCallbackParam;

import java.util.ArrayList;
import java.util.List;

public class ScreenData {
    private ScreenType type;
    private String name; //
    private List<DialogScenario> dialogScenarios;
    private EventCallbackParam param;

    public ScreenData(ScreenType type, String name) {
        this.type = type;
        this.name = name;
        this.dialogScenarios = new ArrayList<>();
    }

    public ScreenData(ScreenType type, String name, List<DialogScenario> dialogScenarios) {
        this.type = type;
        this.name = name;
        this.dialogScenarios = dialogScenarios;
    }

    public List<DialogScenario> getDialogScenarios() {
        return dialogScenarios;
    }

    public ScreenType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setParam(EventCallbackParam param) {
        this.param = param;
    }
}
