package eidolons.system.libgdx.datasource;

import main.system.EventCallbackParam;

import static eidolons.content.consts.VisualEnums.SCREEN_TYPE;

public class ScreenData {
    private SCREEN_TYPE type;
    private String name; //
    private EventCallbackParam param;

    public ScreenData(SCREEN_TYPE type, String name) {
        this.type = type;
        this.name = name;
    }

    public ScreenData(SCREEN_TYPE type, EventCallbackParam param) {
        this.type = type;
        this.param = param;
    }

    public ScreenData(SCREEN_TYPE type, Object data) {
        this.type = type;
        this.param = new EventCallbackParam(data);
    }



    public ScreenData(SCREEN_TYPE type) {
        this.type = type;
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
