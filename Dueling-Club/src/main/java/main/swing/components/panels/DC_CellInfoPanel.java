package main.swing.components.panels;

import main.content.PARAMS;
import main.content.VALUE;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.logic.macro.gui.party.Header;
import main.swing.builders.DC_Builder;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.components.panels.page.log.DC_PagedLogPanel;
import main.swing.components.panels.page.small.DC_PagedBuffPanel;
import main.swing.components.panels.secondary.DeadUnitPanel;
import main.swing.components.panels.secondary.DroppedItemPanel;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;

public class DC_CellInfoPanel extends G_Panel {

    private DC_Obj cell;
    private DC_Game game;
    private DeadUnitPanel deadUnitPanel;
    private DroppedItemPanel droppedItemPanel;
    private DC_PagedLogPanel log;
    private DC_PagedInfoPanel info;
    private DC_PagedBuffPanel buffs;
    private GraphicComponent skulls;
    private NameComponent nameComponent;
    private Header header;
    private int arrowWidth;

    // TrapsInfoPanel t;
    /*
	 * CI info
	 * allow to select cell underneath with alt-click? 
	 * 
	 *  last seen
	 *  dungeon level info 
	 * 
	 */
    // visuals separately? skulls and coins, dividers
    public DC_CellInfoPanel(G_Panel ipHolder, DC_Game game) {
        // panel = new G_Panel();
        this.game = game;
        deadUnitPanel = new DeadUnitPanel(this);
        droppedItemPanel = new DroppedItemPanel();
        skulls = new GraphicComponent(VISUALS.GRAVEYARD.getImage());
        // TODO should display traps, tracks, last seen
        buffs = new DC_PagedBuffPanel();
        nameComponent = new NameComponent();
        header = new Header(new ListMaster<VALUE>().getList(PARAMS.SPACE, PARAMS.CONCEALMENT,
                PARAMS.ILLUMINATION, PARAMS.LIGHT_EMISSION, PARAMS.MOVE_STA_PENALTY,
                PARAMS.MOVE_AP_PENALTY

        ), null);
        arrowWidth = deadUnitPanel.getArrowWidth();

        // space, speed modifier, stamina penalty, concealment, illumination -
        // in a special info comp?
        // mini-value-icon panel? 3x2 ...

        int width = GuiManager.getScreenWidthInt() - GuiManager.getBattleFieldWidth()
                - DC_Builder.getBfGridPosX();

        add(droppedItemPanel, "id drop,pos " + (GuiManager.getCellWidth() + arrowWidth)
                + " header.y2");

        add(buffs, "id buffs,pos " + (width + 2 * arrowWidth - buffs.getPanelWidth()) / 2 + " 0");
        add(nameComponent, "id nameComponent ,  pos "
                + (width + 4 * arrowWidth - nameComponent.getPanelWidth()) / 2 + " buffs.y2");

        add(header, "id header ,  pos " + (GuiManager.getCellWidth() + arrowWidth)
                + " nameComponent.y2");

        add(skulls, "id skulls,pos " + (GuiManager.getCellWidth() + arrowWidth) + " drop.y2");
        add(deadUnitPanel, "id dead, pos " +

                GuiManager.getCellWidth()

                + " skulls.y2");
        skulls = new GraphicComponent(VISUALS.GRAVEYARD.getImage());
        add(skulls, "id skulls2, pos skulls.x dead.y2");
        log = new DC_PagedLogPanel(game);

        info = new DC_PagedInfoPanel(); // dungeon-block info?

        addLog();

    }

    private void addInfo() {
        add(info, "id info, pos " + (GuiManager.getCellWidth() + arrowWidth) + " skulls2.y2");
    }

    private void addLog() {
        add(log, "id log, pos " + (GuiManager.getCellWidth() + arrowWidth) + " skulls2.y2");
    }

    public void deselected() {
        remove(info);
        addLog();
    }

    public void selected(Obj obj) {
        info.setEntity(obj);
        info.refresh();
        remove(log);
        addInfo();
        // toggle log panel? some button to get it back...
    }

    @Override
    protected boolean isAutoZOrder() {
        return true;
    }

    public void refresh() {
        // if empty?
        cell = game.getManager().getInfoObj();
        nameComponent.setObj(getCell());
        nameComponent.refresh();
        header.setEntity(getCell());
        header.refresh();
        buffs.setObj(getCell());
        buffs.refresh();

        deadUnitPanel.setCell(cell);
        deadUnitPanel.refresh();
        droppedItemPanel.setCell(cell);
        droppedItemPanel.refresh();
        log.refresh();
        deselected();
    }

    public Obj getCell() {
        return cell;
    }

    public void setCell(DC_Obj cell) {
        this.cell = cell;
    }
}
