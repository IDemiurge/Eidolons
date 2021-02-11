package eidolons.puzzle;

import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

public class PuzzleEvents extends GdxEventHandler{
//split global event handlers into groups - ?
    public void init(){
        GuiEventManager.bind(GuiEventType.PUZZLE_VEIL , p-> {
            List list = (List) p.get();
            veil(list.get(0), list.get(1), list.get(2));
        });
    }
        public void veil(){
        GuiEventManager.trigger(GuiEventType.ADD_GRID_OBJ, veil);
    }
}
