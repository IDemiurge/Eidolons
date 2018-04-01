package eidolons.swing.generic.services.dialog;

import main.entity.Entity;
import eidolons.swing.components.buttons.CustomButton;
import main.swing.generic.components.ComponentVisuals;
import main.swing.generic.components.G_Dialog;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.graphics.ColorManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class EntityChoiceDialog<T extends Entity> extends G_Dialog implements MouseListener {

    protected List<T> data;
    protected ChoicePanel<T> choicePanel;

    public EntityChoiceDialog(List<T> data) {
        this.data = data;
        init();
        dialog.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
    }

    public T chooseEntity() {
        SwingUtilities.invokeLater(this::show);
        T waitForInput = (T) WaitMaster.waitForInput(getWaitOperation());
        close();
        return waitForInput;
    }

    protected ComponentVisuals getButtonVisualsHighlighted() {
        return null;
    }

    protected ComponentVisuals getButtonVisuals() {
        return null;
    }

    @Override
    protected boolean isAlwaysOnTop() {
        return false;
    }

    @Override
    public Component createComponent() {
        G_Panel comp = new G_Panel();
        choicePanel = getChoicePanel();

        CustomButton okButton = new CustomButton(VISUALS.OK) {
            @Override
            public void handleClick() {
                ok();
            }
        };
        CustomButton closeButton = new CustomButton(VISUALS.CANCEL) {
            @Override
            public void handleClick() {
                WaitMaster.interrupt(getWaitOperation());
                close();
            }
        };
        comp.setOpaque(true);
        comp.setBackground(ColorManager.BACKGROUND);
        int x = choicePanel.getPanelWidth();
        // n * getObjSize();
        comp.add(choicePanel);
        comp.add(okButton, "id ok, pos " + x + " 0");
        comp.add(closeButton, "pos ok.x @max_bottom, id close");
        // comp.add(list, "id list, pos 0 0");
        comp.setPanelSize(getSize());
        return comp;
    }

    protected ChoicePanel<T> getChoicePanel() {
        return null;
    }

    protected int getObjSize() {
        return 64;
    }

    protected int getWrap() {
        return 2;
    }

    @Override
    public void close() {

        super.close();
    }

    protected WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATIONS.DIALOGUE_DONE;
    }

    @Override
    protected void ok() {
        if (choicePanel.getSelectedValue() == null) {
            WaitMaster.interrupt(getWaitOperation());
        } else {
            WaitMaster.receiveInput(getWaitOperation(), choicePanel.getSelectedValue());
        }
        super.ok();
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    @Override
    public boolean isCentered() {
        return true; // TODO
    }

    @Override
    public Dimension getSize() {
        // if (list==null )return null;
        return new Dimension(choicePanel.getPanelWidth() + 40, choicePanel.getPanelHeight());
    }

    @Override
    public String getTitle() {
        return null;
    }

    protected void selected(T entity, boolean highlighted) {
        if (highlighted) {
            ok();
            return;
        }

        if (isInfoSelectionOn()) {
            entity.getGame().getManager().infoSelect(entity);
        }
        initTooltip(entity);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            ok();
            return;
        }
        T entity = choicePanel.getSelectedValue();
        if (isInfoSelectionOn()) {
            entity.getGame().getManager().infoSelect(entity);
        }
        initTooltip(entity);
    }

    protected void initTooltip(T entity) {

    }

    public boolean isInfoSelectionOn() {
        return true;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
