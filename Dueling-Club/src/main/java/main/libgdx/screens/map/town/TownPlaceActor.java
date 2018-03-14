package main.libgdx.screens.map.town;

import main.game.module.adventure.town.TownPlace;
import main.libgdx.screens.map.obj.PlaceActor;
import main.libgdx.screens.map.obj.PlaceActorFactory.PlaceActorParameters;

/**
 * Created by JustMe on 3/14/2018.
 * <p>
 * Shop
 * Tavern
 * Library
 * Smithy
 * Quest-giver (Townhall,
 */
public class TownPlaceActor extends PlaceActor {
    public TownPlaceActor(TownPlace place) {
        super(createParameters(place));
        addListener(new TownPlaceTooltip(place).getController());
    }

    private static TownPlaceParameters createParameters(TownPlace place) {
        return new TownPlaceParameters(place);
    }

    static class TownPlaceParameters extends PlaceActorParameters {

        private final TownPlace townPlace;

        public TownPlaceParameters(TownPlace place) {
            super();
            this.townPlace = place;
        }

        public TownPlace getTownPlace() {
            return townPlace;
        }
    }

}
