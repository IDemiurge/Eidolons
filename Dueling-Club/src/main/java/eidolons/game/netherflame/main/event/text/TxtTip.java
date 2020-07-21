package eidolons.game.netherflame.main.event.text;

import eidolons.system.text.Texts;

public interface TxtTip extends TextEvent {

    default String getMessage(){
        return Texts.getTextMap(getMapId()).get(toString());
    }

    String getMapId();
}

