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
    boolean showDecorText;
    boolean showVfx;
    boolean showShadowMap;
    boolean showShards;

    private boolean gameView;
    private boolean showGamma;

    /*
    can we just have shadowmap/vfx here same way?
     */

    public void toggleAll(){
        showCoordinates = !showCoordinates;
        showScripts = !showScripts;
        showMetaAi = !showMetaAi;
        showIllumination = !showIllumination;
        showSpace = !showSpace;
        showAllColors = !showAllColors;
        showDecorText = !showDecorText;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }
    public void onAll(){
        showCoordinates = true;
        showScripts = true;
        showScripts = true ;
        showIllumination = true;
        showSpace = true;
        showAllColors = true;
        showDecorText = true;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }
    public void offAll(){
        showAllColors = false;
        showCoordinates = false;
        showScripts = false;
        showScripts = false;
        showIllumination = false;
        showSpace = false;
        showDecorText = false;
        GuiEventManager.trigger(GuiEventType.LE_DISPLAY_MODE_UPDATE );
    }

    public boolean isShowDecorText() {
        if (isGameView()) {
            return false;
        }
        return showDecorText;
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
    public void toggleDecorText( ) {
        this.showDecorText = !showDecorText;
    }
    public void toggleScripts( ) {
        this.showScripts = !showScripts;
    }

    public void setGameView(boolean gameView) {
        this.gameView = gameView;
    }

    public void setShowDecorText(boolean showDecorText) {
        this.showDecorText = showDecorText;
    }

    public boolean isShowVfx() {
        if (gameView) {
            return true;
        }
        return showVfx;
    }

    public void setShowVfx(boolean showVfx) {
        this.showVfx = showVfx;
    }

    public boolean isShowShadowMap() {
        if (gameView) {
            return true;
        }
        return showShadowMap;
    }

    public void setShowShadowMap(boolean showShadowMap) {
        this.showShadowMap = showShadowMap;
    }

    public boolean isShowShards() {
        if (gameView) {
            return true;
        }
        return showShards;
    }

    public void setShowShards(boolean showShards) {
        this.showShards = showShards;
    }

    public void toggleShadows() {
        setShowShadowMap(!showShadowMap);
    }
    public void toggleVfx() {
        setShowVfx(!showVfx);
    }

    public boolean isGameView() {
        return gameView;
    }

    public void toggleGamma() {
        setShowGamma(!showGamma);
    }
    public boolean isShowGamma() {
        if (gameView) {
            return false;
        }
        return showGamma;
    }

    public void setShowGamma(boolean showGamma) {
        this.showGamma = showGamma;
    }
}
