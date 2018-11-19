package eidolons.libgdx.screens;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogView;
import main.system.EventCallbackParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ScreenData {
    private SCREEN_TYPE type;
    private String name; //
    private Supplier<List<DialogView>> dialogScenariosFactory;
    private List<DialogView> dialogViews;
    private EventCallbackParam param;

    public ScreenData(SCREEN_TYPE type, String name) {
        this.type = type;
        this.name = name;
        this.dialogViews = new ArrayList<>();
    }

    public ScreenData(SCREEN_TYPE type, String name, Supplier<List<DialogView>> factory) {
        this.type = type;
        this.name = name;
        this.dialogScenariosFactory = factory;
    }

    public ScreenData(ScreenData screenData, Supplier<List<DialogView>> factory) {
        this(screenData.type, screenData.name, factory);
    }

    public List<DialogView> getDialogViews() {
        if (dialogViews == null) {
            dialogViews = dialogScenariosFactory.get();
        }
        return dialogViews;
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
