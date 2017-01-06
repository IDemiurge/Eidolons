package main.swing.components.panels;

import main.entity.obj.DC_UnitAction;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_GameState;
import main.swing.components.panels.DC_UnitActionPanel.ACTION_DISPLAY_GROUP;
import main.swing.components.panels.page.DC_PagedUnitActionPanel;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.GuiManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_UAP_Holder extends G_Panel {

    public static final int UAP_PER_ROW = 3;
    public static final int GAP_WIDTH = 22;
    private DC_GameState state;
    private Map<ACTION_DISPLAY_GROUP, DC_PagedUnitActionPanel> uapMap = new HashMap<>();

    public DC_UAP_Holder(DC_GameState state) {
        this.state = state;
        init();
    }

    public static String getX(int i, int row) {
        i = (i - 1) - row * UAP_PER_ROW;
        if (i == -1)
            return getLeftmostX(row);

        return "uap" + i + "" + row + ".x2+" + GAP_WIDTH;
    }

    public static String getLeftmostX(int row) {
        return ((row * 2 + 1) * GuiManager.getSmallObjSize() + ((row * 5) * GAP_WIDTH / 2) - GuiManager
                .getSmallObjSize() / 3)
                + "";
    }

    private void init() {
        initComps();
        addComps();

    }

    @Override
    public void refresh() {

        Obj activeObj = state.getManager().getActiveObj();
        if (activeObj != null) {
            if (activeObj.getOwner().isAi() || !activeObj.getOwner().isMe()) {
                for (DC_PagedUnitActionPanel uap : uapMap.values()) {
                    uap.refresh();
                }
                return;
            }

        }
        for (DC_PagedUnitActionPanel uap : uapMap.values()) {
            uap.setObj(activeObj);
            uap.refresh();
        }
        super.refresh();
    }

    private void addComps() {
        int i = 0;
        for (ACTION_DISPLAY_GROUP group : ACTION_DISPLAY_GROUP.values()) {
            String LI = "id uap";
            int row = 0;
            switch (group) {
                case SPEC_ACTIONS:
                    row = 1;
                    LI += i + "" + row + ", y 64," + " x " + 0;
                    break;
                case SPEC_ATTACKS:
                    row = 1;
                    LI += i + "" + row + ", y 64," + " x " + getX(i - UAP_PER_ROW, 0)
                    // + ("+" + (GuiManager.getSmallObjSize() - GAP_WIDTH))
                    ;
                    break;

                case SPEC_MOVES:
                    row = 1;
                    LI += i + "" + row + ", y 64," + " x " + getX(i - UAP_PER_ROW, 0)
                            + ("+" + (GuiManager.getSmallObjSize() - GAP_WIDTH));
                    break;
                case STD_MODES:
                case STD_ACTIONS:
                case ADDITIONAL_MOVES:

                    // case SPEC_ACTIONS:
                    // case SPEC_ATTACKS:
                    // case SPEC_MOVES:

                    String x = getX(i, 0);

                    if (i > 0)
                        x += "+" + (((i / 2) * 2) * 2) * GAP_WIDTH;
                    if (i == 2)
                        x += "-" + 4 * GAP_WIDTH;

                    LI += (i - row * UAP_PER_ROW) + "" + row + ", y " + 0 + "," + " x " + x;
                    break;

            }
            add(uapMap.get(group), LI);

            i++;
        }

    }

    private void initComps() {
        for (ACTION_DISPLAY_GROUP group : ACTION_DISPLAY_GROUP.values()) {
            DC_PagedUnitActionPanel uap = new DC_PagedUnitActionPanel(group);
            uapMap.put(group, uap);
        }
    }

    public Map<ACTION_DISPLAY_GROUP, DC_PagedUnitActionPanel> getUapMap() {
        return uapMap;
    }

    public DC_PagedUnitActionPanel getPanelForAction(DC_ActiveObj action) {
        for (ACTION_DISPLAY_GROUP group : getUapMap().keySet()) {
            DC_PagedUnitActionPanel actionPanel = getUapMap().get(group);
            DC_UnitActionPanel panel = (DC_UnitActionPanel) actionPanel.getCurrentComponent();
            List<DC_UnitAction> data = panel.getData();
            if (data != null)
                if (data.contains(action)) {
                    return actionPanel;
                }
        }
        return null;
    }
    // case STD_MODES:
    // LI += i + "" + row + ", y 0," + " x " + 0;
    // break;
    // case STD_ACTIONS:
    // LI += i
    // + ""
    // + row
    // + ", y 0,"
    // + " x "
    // + getX(i, row)
    // + ("+" + (GuiManager.getSmallObjSize() - GAP_WIDTH));
    // break;
    // case ADDITIONAL_MOVES:
    // LI += i
    // + ""
    // + row
    // + ", y 0,"
    // + " x "
    // + getX(i, row)
    // + ("+" + (GuiManager.getSmallObjSize() - GAP_WIDTH))
    // // + ("+" + (GuiManager.getCellSize() - GAP_WIDTH))
    // ;
    // break;
    // case SPEC_ACTIONS:
    // case SPEC_ATTACKS:
    // case SPEC_MOVES:
    // row = 1;
    // String x = getX(i - UAP_PER_ROW, 0);
    // // add per 'i'
    //
    // if (i > UAP_PER_ROW)
    // x += "+" + (((i / 2) * 2 - UAP_PER_ROW) * 2)
    // * GAP_WIDTH;
    // if (i == 5)
    // x += "-" + 2 * GAP_WIDTH;
    //
    // LI += (i - row * UAP_PER_ROW) + "" + row + ", y "
    // + GuiManager.getSmallObjSize() + "," + " x " + x;
    // break;
    //
    // }
    // add(uapMap.getOrCreate(group), LI);
    //
    // i++;

}
