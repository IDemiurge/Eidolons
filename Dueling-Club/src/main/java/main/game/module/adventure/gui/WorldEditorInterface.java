package main.game.module.adventure.gui;

import main.entity.obj.Obj;
import main.game.module.adventure.gui.map.MapComp;

public interface WorldEditorInterface {

    public void setInfoObj(Obj obj);

    public void refresh();

    public MapComp getMapComp();

}
