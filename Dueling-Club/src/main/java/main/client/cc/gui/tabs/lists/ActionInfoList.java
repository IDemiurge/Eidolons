package main.client.cc.gui.tabs.lists;

import main.client.cc.CharacterCreator;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.logic.generic.DC_ActionManager;
import main.swing.SwingMaster;
import main.swing.components.panels.secondary.ActionModePanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class ActionInfoList extends ActionModePanel {
    private Unit hero;
    private List<DC_ActiveObj> data;

    public ActionInfoList(Unit hero) {
        this.hero = hero;

    }

    @Override
    public DC_ActiveObj getAction() {
        if (isOffhandWeaponSelected()) {
            action = hero.getAction(DC_ActionManager.OFFHAND_ATTACK);
        } else {
            action = hero.getAction("Attack");
        }
        return action;
    }

    @Override
    protected boolean toggleAttack(MouseEvent e, final DC_ActiveObj subAction) {
        CharacterCreator.typeSelected(subAction);
        boolean toggleAttack = super.toggleAttack(e, subAction);
        SwingMaster.invokeLater(new Runnable() {
            @Override
            public void run() {

                CharacterCreator.getHeroPanel().typeSelected(subAction);
            }
        });

        return toggleAttack;
    }

    @Override
    protected boolean isDrawAuto() {
        return false;
    }

    @Override
    protected boolean isDrawBackground() {
        return false;
    }

    @Override
    protected boolean isOn(DC_ActiveObj subAction) {
        if (CharacterCreator.getInfoSelected() != null) {
            if (CharacterCreator.getInfoSelected().equals(subAction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void toggleAuto() {
        super.toggleAuto();
    }

    @Override
    public void refresh() {
        if (getAction() != null) {
            initSubActions();
        }
        super.refresh();
    }

    private boolean isOffhandWeaponSelected() {
        if (CharacterCreator.getInfoSelected() != null) {
            if (CharacterCreator.getInfoSelected().equals(hero.getActiveWeapon(true).getType())) {
                return true;
            }
        }
        if (hero.getAction(DC_ActionManager.OFFHAND_ATTACK) != null) {
            if (hero.getAction(DC_ActionManager.OFFHAND_ATTACK).getSubActions().contains(
                    CharacterCreator.getInfoSelected())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

}
