package libgdx.map.town;

import eidolons.macro.entity.town.TownPlace;
import libgdx.map.obj.PlaceActor;
import libgdx.map.obj.PlaceActorFactory.PlaceActorParameters;

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
