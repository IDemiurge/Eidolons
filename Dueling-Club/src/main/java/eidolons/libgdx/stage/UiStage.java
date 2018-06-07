package eidolons.libgdx.stage;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by JustMe on 11/29/2017.
 */
public class UiStage extends StageX implements StageWithClosable{
    Closable displayedClosable;
    private boolean active;

    public UiStage() {
       super(new ScreenViewport(new OrthographicCamera()));
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Closable getDisplayedClosable() {
        return displayedClosable;
    }

    @Override
    public void setDisplayedClosable(Closable displayedClosable) {
        this.displayedClosable = displayedClosable;
    }
}
