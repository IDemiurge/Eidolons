package main.game.logic.macro.gui.map;

import main.game.logic.macro.gui.MacroGuiManager;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.GuiManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MacroAP_Holder extends G_Panel {
    private static final int GAP = 32;
    private Map<MACRO_ACTION_GROUPS, MacroActionPagedPanel> panelMap = new HashMap<>();

    public MacroAP_Holder() {
        initComps();
        addComps();
    }

    @Override
    public Dimension getPanelSize() {
        return new Dimension(MacroGuiManager.getMapWidth(),
                (int) (GuiManager.getScreenHeight()
                        - MacroGuiManager.getMapHeight() - VISUALS.BUTTON
                        .getHeight()));
    }

    @Override
    public void refresh() {
        for (MacroActionPagedPanel uap : panelMap.values()) {
            uap.refresh();
        }
        super.refresh();
    }

    private void initComps() {
        for (MACRO_ACTION_GROUPS group : MACRO_ACTION_GROUPS.values()) {
            MacroActionPagedPanel uap = new MacroActionPagedPanel(group);
            panelMap.put(group, uap);
        }
    }

    private void addComps() {
        // TODO ACTUALLY, THE MODES SHOULD BE BELOW AND HUGE...
        for (MACRO_ACTION_GROUPS group : MACRO_ACTION_GROUPS.values()) {
            MacroActionPagedPanel comp = panelMap.get(group);
            String LI = "@id uap";
            String x = "0";
            String y = "0";
            if (group == MACRO_ACTION_GROUPS.MODE) {
                y = "" + GuiManager.getSmallObjSize();
                x = ((MacroGuiManager.getMapWidth() - comp.getPanelWidth() - GAP) / 2)
                        + "";
            } else {
                if (group == MACRO_ACTION_GROUPS.PARTY) {
                    x = ((MacroGuiManager.getMapWidth() - comp.getPanelWidth()) - GAP)
                            + "";
                } else {
                    x = GAP + "";
                }
            }
            LI += ", y " + y + "," + " x " + x;
            add(comp, LI);
        }

    }

    public Map<MACRO_ACTION_GROUPS, MacroActionPagedPanel> getPanelMap() {
        return panelMap;
    }

    public enum MACRO_ACTION_GROUPS {
        CHARACTER, PARTY, MODE,
        // SPELL
    }

}
