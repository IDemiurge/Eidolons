package main.level_editor.backend.display;

import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.io.Serializable;

public class LE_DisplayMode  implements Serializable {

    boolean showStacks;
    boolean showMetaAi;
    boolean showScripts;

    boolean showCoordinates; //TODO use options?
    boolean showIllumination;
    boolean showSpace;

    boolean showAllColors;

    public void toggleAll(){
        showCoordinates = !showCoordinates;
        showScripts = !showScripts;
        showMetaAi = !showMetaAi;
        showIllumination = !showIllumination;
        showSpace = !showSpace;
        showAllColors = !showAllColors;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }
    public void onAll(){
        showCoordinates = true;
        showScripts = true;
        showScripts = true ;
        showIllumination = true;
        showSpace = true;
        showAllColors = true;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }
    public void offAll(){
        showAllColors = false;
        showCoordinates = false;
        showScripts = false;
        showScripts = false;
        showIllumination = false;
        showSpace = false;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
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


    public boolean isShowAllColors() {
        return showAllColors;
    }

    public void setShowAllColors(boolean showAllColors) {
        this.showAllColors = showAllColors;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }
    public void toggleCoordinates( ) {
        this.showCoordinates = !showCoordinates;
    }
    public void toggleColors( ) {
        this.showAllColors = !showAllColors;
    }
    public void toggleAi( ) {
        this.showMetaAi = !showMetaAi;
    }
    public void toggleScripts( ) {
        this.showScripts = !showScripts;
    }
}
