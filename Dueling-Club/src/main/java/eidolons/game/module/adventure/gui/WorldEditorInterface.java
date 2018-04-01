package eidolons.game.module.adventure.gui;

import eidolons.game.module.adventure.gui.map.MapComp;
import main.entity.obj.Obj;

public interface WorldEditorInterface {

    void setInfoObj(Obj obj);

    void refresh();

    MapComp getMapComp();

}
