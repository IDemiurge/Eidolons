package eidolons.system.libgdx;

import eidolons.system.libgdx.wrapper.VectorGdx;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import main.game.bf.Coordinates;

public interface GdxEventAdapter {
    void veil(Coordinates c, boolean black, boolean enter);
    void cannotActivate(DC_ActiveObj e, String reason);

    void tooltip(String description);

    String comment(String img, String text, Coordinates c);

    void comment_(Unit unit, String key, VectorGdx at);
}
