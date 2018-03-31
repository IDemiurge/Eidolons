package eidolons.game.module.adventure.gui;

import main.entity.obj.Obj;
import eidolons.game.module.adventure.gui.map.MapComp;

public interface WorldEditorInterface {

    void setInfoObj(Obj obj);

    void refresh();

    MapComp getMapComp();

}
