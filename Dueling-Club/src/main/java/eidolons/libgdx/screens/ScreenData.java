package eidolons.libgdx.screens;

import eidolons.libgdx.DialogScenario;
import main.system.EventCallbackParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ScreenData {
    private SCREEN_TYPE type;
    private String name; //
    private Supplier<List<DialogScenario>> dialogScenariosFactory;
    private List<DialogScenario> dialogScenarios;
    private EventCallbackParam param;

    public ScreenData(SCREEN_TYPE type, String name) {
        this.type = type;
        this.name = name;
        this.dialogScenarios = new ArrayList<>();
    }

    public ScreenData(SCREEN_TYPE type, String name, Supplier<List<DialogScenario>> factory) {
        this.type = type;
        this.name = name;
        this.dialogScenariosFactory = factory;
    }

    public ScreenData(ScreenData screenData, Supplier<List<DialogScenario>> factory) {
        this(screenData.type, screenData.name, factory);
    }

    public List<DialogScenario> getDialogScenarios() {
        if (dialogScenarios == null) {
            dialogScenarios = dialogScenariosFactory.get();
        }
        return dialogScenarios;
    }

    public Object getParameter() {
        if (param==null )
            return null;
        return param.get();
    }
    public SCREEN_TYPE getType() {
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
