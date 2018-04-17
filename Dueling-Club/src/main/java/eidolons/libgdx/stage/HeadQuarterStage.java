package eidolons.libgdx.stage;

import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.screens.ScreenData;

public class HeadQuarterStage extends DataStage {
    private HqPanel headQuartersPanel;

    public HeadQuarterStage() {

    }

    @Override
    public void setData(ScreenData data) {
        super.setData(data);
        headQuartersPanel.setUserObject(data.getParams());
    }
}
