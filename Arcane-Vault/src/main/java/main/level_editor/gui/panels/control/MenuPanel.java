package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import main.level_editor.LevelEditor;
import main.level_editor.backend.brush.BrushShape;
import main.level_editor.backend.brush.LE_BrushType;
import main.level_editor.backend.handlers.LE_MenuHandler;
import main.system.auxiliary.StringMaster;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MenuPanel extends MenuBar {

    public MenuPanel() {
        Menu.MenuStyle style = (StyleHolder.getMenuStyle());
        MenuItem.MenuItemStyle btnStyle = (StyleHolder.getMenuBtnStyle());
       
        for (LE_MenuHandler.FUNCTION_BUTTONS value : LE_MenuHandler.FUNCTION_BUTTONS.values()) {
            if (value.subFuncs.length == 0) {
                continue;
            }
            Menu menu = new Menu(StringMaster.getWellFormattedString(value.name()), style);

            addMenu(menu);
            for (LE_MenuHandler.FUNCTION_BUTTONS subFunc : value.subFuncs) {
                MenuItem item = createMenuItem(subFunc);
                item.addListener(createItemListener(item, subFunc));
                item.setStyle(btnStyle);
                menu.addItem(item);
//               addSubMenu(menu, subFunc, item);
            }
        }

        Menu brushMenu;
        addMenu(brushMenu = new Menu("Brush", style));
        for (LE_BrushType value : LE_BrushType.values()) {
            MenuItem item = new MenuItem(value.name());
            item.addListener(createBrushItemListener(value));
            item.setStyle(btnStyle);
            brushMenu.addItem(item);
        }
        addMenu(brushMenu = new Menu("Shape", style));
        for (BrushShape value :  BrushShape.values()) {
            MenuItem item = new MenuItem(value.name());
            item.addListener(createBrushShapeItemListener(value));
            item.setStyle(btnStyle);
            brushMenu.addItem(item);
        }

    }

    private EventListener createBrushShapeItemListener(BrushShape value) {
        return event -> {
            if (event instanceof InputEvent) {
                switch (((InputEvent) event).getType()) {
                    case touchDown:
                        LevelEditor.getCurrent().getManager().getModelManager()
                                .getModel().setBrushMode(true);
                        LevelEditor.getCurrent().getManager().getModelManager()
                                .getModel().getBrush().setShape(value);
                        return true;
                }
            }
            return false;
        };
    }

    private EventListener createBrushItemListener(LE_BrushType value) {
        return event -> {
            if (event instanceof InputEvent) {
                switch (((InputEvent) event).getType()) {
                    case touchDown:
                        LevelEditor.getCurrent().getManager().getModelManager()
                                .getModel().setBrushMode(true);
                        LevelEditor.getCurrent().getManager().getModelManager()
                                .getModel().getBrush().setBrushType(value);
                        return true;
                }
            }
            return false;
        };
    }

    public void initControlMenus(LE_ControlPanel[] panels) {
        Menu.MenuStyle style = (StyleHolder.getMenuStyle());
        TextButton.TextButtonStyle btnStyle = (StyleHolder.getMenuBtnStyle());
        for (LE_ControlPanel panel :
                panels) {
            Menu menu = new Menu(TabbedControlPanel.getTitleFromClass(panel.getClazz()), style);
            addMenu(menu);
            for (Method method : panel.getClazz().getMethods()) {
                MenuItem item = new MenuItem(StringMaster.getWellFormattedString(method.getName()));
                item.addListener(createItemListener(item, method, panel.getHandler()));
                menu.addItem(item);
                item.setStyle(btnStyle);

            }
        }
    }

    private EventListener createItemListener(MenuItem item, Method method, Object handler) {
        return event -> {
            if (event instanceof InputEvent) {
                switch (((InputEvent) event).getType()) {
                    case touchDown:
                        try {
                            method.invoke(handler);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                }
            }
            return false;
        };
    }

    private EventListener createItemListener(MenuItem item, LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        return event -> {
            if (event instanceof InputEvent) {
                switch (((InputEvent) event).getType()) {
                    case touchDown:
                        clicked(subFunc);
                        return true;
                }
            }
            return false;
        };
    }

    private void clicked(LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        Eidolons.onNonGdxThread(() -> LevelEditor.getCurrent().getManager().getMenuHandler().clicked(subFunc));
    }

    private MenuItem createMenuItem(LE_MenuHandler.FUNCTION_BUTTONS subFunc) {
        MenuItem item = new MenuItem( StringMaster.getWellFormattedString(subFunc.name()));
        return item;
    }

}
