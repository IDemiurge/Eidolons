package main.swing.generic.services.dialog;

import main.entity.Entity;
import main.swing.generic.components.G_Component;

public class EntityChoice<T extends Entity> extends CustomDialog<T> {

    @Override
    protected G_Component createPanel() {
        return null;
//        return new AttackChoicePanel(this, data);
    }


}
