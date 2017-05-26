package main.libgdx.screens;

import main.libgdx.DialogScenario;
import main.system.EventCallbackParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ScreenData {
    private ScreenType type;
    private String name; //
    private Supplier<List<DialogScenario>> dialogScenariosFactory;
    private List<DialogScenario> dialogScenarios;
    private EventCallbackParam param;

    public ScreenData(ScreenType type, String name) {
        this.type = type;
        this.name = name;
        this.dialogScenarios = new ArrayList<>();
    }

    public ScreenData(ScreenType type, String name, Supplier<List<DialogScenario>> factory) {
        this.type = type;
        this.name = name;
        this.dialogScenariosFactory = factory;
    }

    public List<DialogScenario> getDialogScenarios() {
        if (dialogScenarios == null) {
            dialogScenarios = dialogScenariosFactory.get();
        }
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

    public EventCallbackParam getParams() {
        return param;
    }
}
