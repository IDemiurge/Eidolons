package eidolons.system.text.tips;

import eidolons.system.text.DescriptionTooltips;

public interface TxtTip extends TextEvent {

    default String getMessage(){
        return DescriptionTooltips.getMap(getMapId()).get(toString().toLowerCase());
    }

    String getMapId();
}

