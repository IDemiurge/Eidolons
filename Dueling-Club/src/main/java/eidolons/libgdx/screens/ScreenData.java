package eidolons.libgdx.screens;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.view.DialogueView;
import main.system.EventCallbackParam;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ScreenData {
    private SCREEN_TYPE type;
    private String name; //
    private Supplier<List<DialogueView>> dialogScenariosFactory;
    private List<DialogueView> dialogViews;
    private EventCallbackParam param;

    public ScreenData(SCREEN_TYPE type, String name) {
        this.type = type;
        this.name = name;
        this.dialogViews = new ArrayList<>();
    }

    public ScreenData(SCREEN_TYPE type, EventCallbackParam param) {
        this.type = type;
        this.param = param;
    }

    public ScreenData(SCREEN_TYPE type, Object data) {
        this.type = type;
        this.param = new EventCallbackParam(data);
    }

    public ScreenData(SCREEN_TYPE type, String name, Supplier<List<DialogueView>> factory) {
        this.type = type;
        this.name = name;
        this.dialogScenariosFactory = factory;
    }

    public ScreenData(ScreenData screenData, Supplier<List<DialogueView>> factory) {
        this(screenData.type, screenData.name, factory);
    }

    public ScreenData(SCREEN_TYPE type) {
        this.type = type;
    }

    public List<DialogueView> getDialogViews() {
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
