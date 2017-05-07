package main.game.module.adventure.gui.map.obj;

import main.game.module.adventure.entity.MacroObj;
import main.system.images.ImageManager.BORDER;

public class RouteComp extends MapObjComp {

    public static final int DEFAULT_SIZE = 52;

    public RouteComp(MacroObj p) {
        super(p);

    }

    protected BORDER getBorder() {
        return super.getBorder();
    }

    public int getDefaultSize() {
        return DEFAULT_SIZE;
    }
}
