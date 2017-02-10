package main.game.logic.dungeon.editor.gui;

import main.game.logic.dungeon.editor.gui.LE_PlanPanel.*;
import main.system.auxiliary.StringMaster;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LE_KeyMaster implements KeyListener {

    private LE_MainPanel panel;

    public LE_KeyMaster(LE_MainPanel panel) {
        this.panel = panel;
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        // focus on ControlPanel or Side/PlanPanel
        new Thread(new Runnable() {
            public void run() {
                handleKey(e);
            }
        }, " thread").start();
    }

    private void handleKey(KeyEvent e) {
        boolean alt = e.isShiftDown();
        switch (e.getKeyChar()) {
            case 'c':
            case 'C':
                panel.getPlanPanel().handlePlanControl(PLAN_CONTROLS.COPY, alt);
                return;
            case 'f':
            case 'F':
                panel.getPlanPanel().handlePlanControl(PLAN_CONTROLS.FILL, alt);
                return;
            case 'e':
            case 'E':
                panel.getPlanPanel().handlePlanControl(PLAN_CONTROLS.CLEAR, alt);
                return;
            case 'v':
            case 'V':
                panel.getPlanPanel().handlePlanControl(PLAN_CONTROLS.MOVE, alt);
                return;
            case 'r':
            case 'R':
                panel.getPlanPanel().handlePlanControl(PLAN_CONTROLS.MIRROR, alt);
                return;

        }
        Integer i = StringMaster.getInteger(e.getKeyChar());
        if (i == null) {
            return;
        }
        i--;
        switch (panel.getPlanPanel().getControlGroup()) {
            case BLOCK:
                panel.getPlanPanel().handleBlockControl(BLOCK_CONTROLS.values()[i]);
                break;
            case LEVEL:
                panel.getPlanPanel().handleLevelControl(LEVEL_CONTROLS.values()[i], alt);
                break;
            case MISSION:
                panel.getPlanPanel().handleMissionControl(MISSION_CONTROLS.values()[i]);
                break;
            case OBJ:
                panel.getPlanPanel().handleObjControl(OBJ_CONTROLS.values()[i]);
                break;
            default:
                break;

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
