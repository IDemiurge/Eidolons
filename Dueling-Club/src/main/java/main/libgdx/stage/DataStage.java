package main.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import main.libgdx.screens.ScreenData;

public class DataStage extends Stage {
    protected ScreenData data;

    public void setData(ScreenData data) {
        this.data = data;
    }
}
