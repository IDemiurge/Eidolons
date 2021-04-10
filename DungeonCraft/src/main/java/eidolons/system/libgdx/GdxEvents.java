package eidolons.system.libgdx;

import eidolons.entity.active.DC_ActiveObj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;

public class GdxEvents {


    public static void tooltip(String description) {
        GdxAdapter.getInstance().getEventsAdapter().tooltip(description);
    }
}
