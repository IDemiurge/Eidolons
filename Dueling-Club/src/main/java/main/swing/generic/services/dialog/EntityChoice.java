package main.swing.generic.services.dialog;

import main.entity.Entity;
import main.swing.generic.components.G_Component;

import java.awt.event.MouseEvent;

public class EntityChoice<T extends Entity> extends CustomDialog<T> {

    @Override
    protected G_Component createPanel() {
        return  null;
//        return new AttackChoicePanel(this, data);
    }

    @Override
    public AttackChoicePanel getPanel() {
        return (AttackChoicePanel) super.getPanel();
    }

    public void click(MouseEvent e) {
        getPanel().mouseClicked(e);
    }

}
