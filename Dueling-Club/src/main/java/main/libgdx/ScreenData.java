package main.libgdx;

import main.system.EventCallbackParam;

public class ScreenData {
    private ScreenType type;
    private String name; //
    private String introScenarioName;
    private EventCallbackParam param;

    public ScreenData(ScreenType type, String name) {
        this.type = type;
        this.name = name;
    }

    public ScreenData(ScreenType type, String name, String introScenarioName) {
        this.type = type;
        this.name = name;
        this.introScenarioName = introScenarioName;
    }

    public String getIntroScenarioName() {
        return introScenarioName;
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
