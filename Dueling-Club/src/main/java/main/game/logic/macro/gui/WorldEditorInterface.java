package main.game.logic.macro.gui;

import main.entity.obj.Obj;
import main.game.logic.macro.gui.map.MapComp;

public interface WorldEditorInterface {

    public void setInfoObj(Obj obj);

    public void refresh();

    public MapComp getMapComp();

}
