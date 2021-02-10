package libgdx.screens.map.town;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.macro.entity.town.TownPlace;
import libgdx.gui.tooltips.Tooltip;

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
            case BROTHEL:
            case TEMPLE:
            case FACTION_QUARTER:
            case SLAVE_MARKET:
                break;
        }
    }
}
