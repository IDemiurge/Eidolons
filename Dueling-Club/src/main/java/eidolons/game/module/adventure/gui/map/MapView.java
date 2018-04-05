package eidolons.game.module.adventure.gui.map;

import eidolons.client.cc.gui.neo.top.HC_ControlButton;
import eidolons.game.module.adventure.MacroManager;
import eidolons.game.module.adventure.global.TimeMaster;
import eidolons.game.module.adventure.gui.MacroInfoPanel;
import eidolons.game.module.adventure.gui.party.PartyTabPanel;
import eidolons.game.module.adventure.travel.RestMasterOld;
import eidolons.swing.components.buttons.CustomButton;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.components.Builder;
import main.swing.generic.components.G_Component;
import main.swing.listeners.ButtonHandler;

import javax.swing.*;
import java.awt.*;

public class MapView extends Builder {

    MapComp mapComp;
    MacroControlPanel macroControlPanel;
    MacroInfoPanel macroInfoPanel;
    MacroAP_Holder macroActionPanel;
    PartyTabPanel PartyTabPanel;
    private CustomButton endTurnButton;
    private TimeComp timeComp;

    public MapView() {

    }

    public void refresh() {
        super.refresh();
    }

    @Override
    public JComponent build() {
        JComponent component = super.build();
        component.setOpaque(true);
        component.setBackground(Color.black);
        refresh();
        return component;
    }

    @Override
    public void init() {
        mapComp = new MapComp();
        macroInfoPanel = new MacroInfoPanel(MacroManager.getActiveParty()
         .getCurrentLocation());
        macroActionPanel = new MacroAP_Holder();
        PartyTabPanel = new PartyTabPanel();
        macroControlPanel = new MacroControlPanel();
        endTurnButton = new HC_ControlButton("End Turn", new ButtonHandler() {
            @Override
            public void handleClick(String command) {

            }

            public void handleClick(String command, boolean alt) {
                new Thread(new Runnable() {
                    public void run() {
                        if (MacroManager.getActiveParty().getStatus() == null) {
                            if (DialogMaster
                             .confirm("No orders given; should the party rest?")) {
                                if (!RestMasterOld.rest(MacroManager
                                 .getActiveParty())) {
                                    return;
                                }
                            }
                        }
                        MacroManager.endTurn();
                    }
                }).start();
            }


        });
        timeComp = new TimeComp(TimeMaster.getDate());
        compArray = new G_Component[]{PartyTabPanel, macroActionPanel,
         mapComp.getComp(), macroInfoPanel, endTurnButton,
         macroControlPanel, timeComp};
        cInfoArray = new String[]{
         "id ptp, pos 0 0",

         "id MacroActionPanel, pos mapComp.x mapComp.y2 mapComp.x2 visual.y2,  ",
         "id mapComp, pos ptp.x2 mcp.y2", "id mip, pos mapComp.x2 0",
         "id endTurn, pos mip.x mip.y2",
         "id LogPanel, pos mip.x endTurn.y2", "id mcp, pos ptp.x2 0",
         "id timeComp, @pos 1680-240 MacroActionPanel.y2-50",};
    }

    public int getMapOffsetX() {
        return PartyTabPanel.getWidth();
    }

    public int getMapOffsetY() {
        return macroControlPanel.getHeight();
    }

    public void refreshMap() {
        mapComp.refresh();
    }

    public MapComp getMapComp() {
        return mapComp;
    }


    public MacroControlPanel getMacroControlPanel() {
        return macroControlPanel;
    }

    public MacroInfoPanel getMacroInfoPanel() {
        return macroInfoPanel;
    }

    public MacroAP_Holder getMacroActionPanel() {
        return macroActionPanel;
    }

    public PartyTabPanel getPartyTabPanel() {
        return PartyTabPanel;
    }

}
