package eidolons.libgdx.stage;

import eidolons.libgdx.gui.panels.headquarters.HeadQuartersPanel;
import eidolons.libgdx.screens.ScreenData;

public class HeadQuarterStage extends DataStage {
    private HeadQuartersPanel headQuartersPanel;

    public HeadQuarterStage() {
        headQuartersPanel = new HeadQuartersPanel();
        addActor(headQuartersPanel);

        headQuartersPanel.setCreateHeroButtonCallback(() -> System.out.println("create hero"));
        headQuartersPanel.setMapButtonCallback(() -> System.out.println("open map"));
        headQuartersPanel.setMenuButtonCallback(() -> System.out.println("open game menu"));
        headQuartersPanel.setShopButtonCallback(() -> System.out.println("open shop"));
        headQuartersPanel.setTavernButtonCallback(() -> System.out.println("open tavern"));
    }

    @Override
    public void setData(ScreenData data) {
        super.setData(data);
        headQuartersPanel.setUserObject(data.getParams());
    }
}
