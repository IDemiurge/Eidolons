package main.level_editor.gui.top;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import main.level_editor.LevelEditor;
import main.level_editor.backend.menu.LE_MenuHandler;

public class MenuPanel extends MenuBar {

    public MenuPanel() {
        for (LE_MenuHandler.FUNCTION_BUTTONS value : LE_MenuHandler.FUNCTION_BUTTONS.values()) {
            if (value.subFuncs.length==0) {
                continue;
            }
            Menu menu= new Menu(value.name());
            addMenu(menu);
            for (LE_MenuHandler.FUNCTION_BUTTONS subFunc : value.subFuncs) {
               MenuItem item =createMenuItem(subFunc) ;
                item.addListener(createItemListener(item, subFunc));
                menu.addItem(item);
//               addSubMenu(menu, subFunc, item);
            }
        }
    }

    private EventListener createItemListener(MenuItem item, LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        return new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    switch (((InputEvent) event).getType()) {
                        case touchDown:
                            clicked(subFunc);
                            return true;
                    }
                }
                return false;
            }
        };
    }

    private void clicked(LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        LevelEditor.getCurrent().getManager().getMenuHandler().clicked(subFunc);
    }

    private MenuItem createMenuItem(LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        MenuItem item=new MenuItem(subFunc.name());
        return item;
    }

}
