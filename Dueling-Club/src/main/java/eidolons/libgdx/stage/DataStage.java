package eidolons.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.libgdx.screens.ScreenData;

public class DataStage extends Stage {
    protected ScreenData data;

    public void setData(ScreenData data) {
        this.data = data;
    }
}
