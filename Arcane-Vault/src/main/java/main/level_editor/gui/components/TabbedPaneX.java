package main.level_editor.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import eidolons.libgdx.gui.panels.TablePanelX;

public class TabbedPaneX extends TabbedPane {

    protected void addTab(String title, Actor actor) {
        super.addTab(createTab(title, actor), getTabs().size-1);
    }

    private Tab createTab(String title, Actor actor) {
        return new Tab() {
            @Override
            public String getTabTitle() {
                return title;
            }

            @Override
            public Table getContentTable() {
                TablePanelX<Actor> t = new TablePanelX<>();
                t.add(actor);
                return t;
            }
        };
    }

    @Override
    protected void addTab(Tab tab, int index) {
        super.addTab(tab, index);
    }
}
