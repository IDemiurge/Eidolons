package main.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by JustMe on 11/29/2017.
 */
public class UiStage extends Stage {

    private boolean active;

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
