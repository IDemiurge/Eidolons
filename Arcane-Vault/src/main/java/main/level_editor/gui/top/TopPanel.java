package main.level_editor.gui.top;

import eidolons.libgdx.gui.panels.TablePanelX;

public class TopPanel extends TablePanelX {
    FunctionsPanel functionsPanel;
    MenuPanel menuPanel;
    ModesPanel modesPanel;
    EditPanel editPanel;
    InfoPanel infoPanel;

    public void init(){
        functionsPanel = new FunctionsPanel();
        modesPanel = new ModesPanel();
        editPanel = new EditPanel();
        infoPanel = new InfoPanel();
        menuPanel = new MenuPanel();

//        add(menuPanel, infoPanel, editPanel, modesPanel, functionsPanel);
    }
}
