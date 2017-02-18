package main.game.logic.macro.gui.map;

import main.game.logic.macro.MacroGame;
import main.game.logic.macro.entity.MacroAction;
import main.game.logic.macro.entity.MacroActionManager;
import main.game.logic.macro.gui.map.MacroAP_Holder.MACRO_ACTION_GROUPS;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;
import main.system.graphics.GuiManager;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class MacroActionPagedPanel extends G_PagedListPanel<MacroAction> {
    // shouldn't there also be a Character/Party division?
    // 3 panels could be just right, I suppose...
    private MACRO_ACTION_GROUPS group;

    //
    public MacroActionPagedPanel(MACRO_ACTION_GROUPS group) {
        super(group == MACRO_ACTION_GROUPS.MODE ? 9 : 5, false, 3);
        this.group = group;
    }

    // TODO special rendering for MODE actions - keep them darkened except
    // active Mode

    @Override
    protected List<List<MacroAction>> getPageData() {
        return new ListMaster<MacroAction>().splitList(true, pageSize,
                MacroActionManager.getMacroActions(group, MacroGame.getGame()
                        .getPlayerParty()));
    }

    @Override
    protected G_Component createPageComponent(List<MacroAction> list) {
        MacroActionPanel macroActionPanel = new MacroActionPanel(group, list);
        return macroActionPanel;
    }

    public int getPanelHeight() {
        return GuiManager.getSmallObjSize();
    }

    public int getPanelWidth() {
        return getPageSize() * GuiManager.getSmallObjSize();
    }
    // public MacroActionPanel(int pageSize, boolean vertical, int version) {
    // super(pageSize, vertical, version);
    // // TODO Auto-generated constructor stub
    // }
    //
    // @Override
    // protected G_Component createPageComponent(List<MacroAction> list) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // protected List<List<MacroAction>> getPageData() {
    // // TODO Auto-generated method stub
    // return null;
    // }

    // paged?

}
