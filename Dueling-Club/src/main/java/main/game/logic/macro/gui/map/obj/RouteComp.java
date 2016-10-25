package main.game.logic.macro.gui.map.obj;

import main.game.logic.macro.entity.MacroObj;
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
