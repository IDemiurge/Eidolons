package eidolons.libgdx.stage;

/**
 * Created by JustMe on 11/29/2017.
 */
public class UiStage extends StageX implements StageWithClosable{
    Closable displayedClosable;
    private boolean active;

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
