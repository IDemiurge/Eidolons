package main.level_editor.functions.display;

public class LE_DisplayMode {

    boolean showStacks;
    boolean showMetaAi;
    boolean showScripts;

    boolean showCoordinates;
    boolean showIllumination;
    boolean showSpace;

    boolean useColors;

    public void toggleAll(){

    }
    public void onAll(){

    }
    public void offAll(){

    }

    public boolean isShowStacks() {
        return showStacks;
    }

    public void setShowStacks(boolean showStacks) {
        this.showStacks = showStacks;
    }

    public boolean isShowMetaAi() {
        return showMetaAi;
    }

    public void setShowMetaAi(boolean showMetaAi) {
        this.showMetaAi = showMetaAi;
    }

    public boolean isShowScripts() {
        return showScripts;
    }

    public void setShowScripts(boolean showScripts) {
        this.showScripts = showScripts;
    }

    public boolean isShowCoordinates() {
        return showCoordinates;
    }

    public void setShowCoordinates(boolean showCoordinates) {
        this.showCoordinates = showCoordinates;
    }

    public boolean isShowIllumination() {
        return showIllumination;
    }

    public void setShowIllumination(boolean showIllumination) {
        this.showIllumination = showIllumination;
    }

    public boolean isShowSpace() {
        return showSpace;
    }

    public void setShowSpace(boolean showSpace) {
        this.showSpace = showSpace;
    }

    public boolean isUseColors() {
        return useColors;
    }

    public void setUseColors(boolean useColors) {
        this.useColors = useColors;
    }
}
