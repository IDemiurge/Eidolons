package eidolons.libgdx.stage;

import eidolons.system.audio.DC_SoundMaster;
import main.system.sound.AudioEnums;

/**
 * Created by JustMe on 11/25/2017.
 */
public interface Closable {

    default void close() {
        getStageWithClosable().closeClosable(this);

    }

    StageWithClosable getStageWithClosable();

    default void open() {
        getStageWithClosable().openClosable(this);
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__OPEN_MENU);
    }

}
