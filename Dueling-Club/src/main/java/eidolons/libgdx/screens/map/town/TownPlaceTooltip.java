package eidolons.libgdx.screens.map.town;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.game.module.adventure.town.TownPlace;

/**
 * Created by JustMe on 3/14/2018.
 */
public class TownPlaceTooltip extends Tooltip {
    TownPlace place;

    public TownPlaceTooltip(TownPlace place) {
        this.place = place;
    }

    @Override
    protected void onTouchDown(InputEvent event, float x, float y) {
        super.onTouchDown(event, x, y);

        triggerEnterEvent();
    }

    private void triggerEnterEvent() {
        switch (place.getTownPlaceType()) {

            case GUILD:
                break;
            case SLAVE_MARKET:
                break;
            case FACTION_QUARTER:
                break;
            case TEMPLE:
                break;
            case BROTHEL:
                break;
        }
    }
}
