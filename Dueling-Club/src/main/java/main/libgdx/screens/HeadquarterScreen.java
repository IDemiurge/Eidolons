package main.libgdx.screens;

import main.libgdx.gui.panels.headquarters.MapPanel;
import main.libgdx.gui.panels.headquarters.ShopPanel;
import main.libgdx.gui.panels.headquarters.TavernPanel;
import main.libgdx.stage.HeadQuarterStage;

public class HeadquarterScreen extends ScreenWithLoader {
    private HeadQuarterStage headQuarterStage;

    private MapPanel mapPanel;
    private ShopPanel shopPanel;
    private TavernPanel tavernPanel;

    public HeadquarterScreen() {
        mapPanel = new MapPanel();
        shopPanel = new ShopPanel();
        tavernPanel = new TavernPanel();
    }

    @Override
    protected void afterLoad() {

    }

    @Override
    protected void preLoad() {
        super.preLoad();


    }
}
