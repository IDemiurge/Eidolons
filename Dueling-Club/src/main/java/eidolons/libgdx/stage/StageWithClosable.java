package eidolons.libgdx.stage;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.macro.MacroGame;
import eidolons.system.audio.DC_SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * Created by JustMe on 11/25/2017.
 */
public interface StageWithClosable {

    Closable getDisplayedClosable();

    void setDisplayedClosable(Closable closable);

    default void closeClosable(Closable closable) {
        if (closable instanceof GroupX) {
            ((GroupX) closable).fadeOut();
        } else ((Actor) closable).setVisible(false);
        if (ExplorationMaster.isExplorationOn() || AI_Manager.isRunning())
            if (closable instanceof Blocking) {
                if (((Blocking) closable).isPausing())
                if (DC_Game.game != null)
                    if (DC_Game.game .isStarted())
                        DC_Game.game.getLoop().setPaused(false, false);

            }
        setDisplayedClosable(null);
    }

    default void openClosable(Closable closable) {
        if (!(closable instanceof ConfirmationPanel))
            closeDisplayed(closable);

        if (this instanceof Stage) {
            ((Stage) this).setScrollFocus((Actor) closable);
        }
        setDisplayedClosable(closable);

        if (closable instanceof GroupX) {
            ((GroupX) closable).fadeIn();
        } else ((Actor) closable).setVisible(true);
        if (ExplorationMaster.isExplorationOn())// wtf || AI_Manager.isRunning())
            if (closable instanceof Blocking) {
                if (((Blocking) closable).isPausing())
                    if (DC_Game.game != null)
                        if (DC_Game.game .isStarted())
                    if (DC_Game.game.getLoop() != null) {
                        DC_Game.game.getLoop().setPaused(true, false);
                        return;
                    }
                if (MacroGame.game != null)
                    if (MacroGame.game.getLoop() != null) {
                        MacroGame.game.getLoop().setPaused(true);
                    }
            }

        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__OPEN_MENU);
    }

    default boolean closeDisplayed(Closable newClosable) {
        if (getDisplayedClosable() == newClosable)
            return false;
        return closeDisplayed();
    }

    default boolean closeDisplayed() {
        if (getDisplayedClosable() == null)
            return false;
        getDisplayedClosable().close();
        setDisplayedClosable(null);
        return true;
    }
}
