package eidolons.libgdx.stage;

/**
 * Created by JustMe on 11/25/2017.
 */
public interface StageWithClosable {
    boolean closeDisplayed();

    Closable getDisplayedClosable();

    void setDisplayedClosable(Closable closable);
}
