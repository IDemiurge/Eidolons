package main.gui.components.controls;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.v2_0.AV2;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AV_TableButtons extends G_Panel {
    boolean secondTable;

    public AV_TableButtons(boolean secondTable) {
        this.secondTable = secondTable;
    }

    public void update(OBJ_TYPE selectedType) {
        removeAll();
            int i=0;
        for (AV_TABLE_BUTTON button : AV_TABLE_BUTTON.values()) {
            if (!isBtnForType(button, selectedType))
                continue;
            if (i++ >= 6){
                add(createButton(button), "wrap, sg btn");
                i=0;
            } else
                add(createButton(button), "sg btn");
        }
    }

    private boolean isBtnForType(AV_TABLE_BUTTON button, OBJ_TYPE selectedType) {
        if (button.forTypes.length>0) {
            return ArrayMaster.contains_( button.forTypes, selectedType);
        }
        return true;
    }

    private Component createButton(AV_TABLE_BUTTON button) {
        JButton btn = new JButton(button.toString());
        btn.addActionListener(new ActionListener() {
                                  @Override
                                  public void actionPerformed(ActionEvent e) {
                                      boolean alt = ActionEvent.ALT_MASK == (e.getModifiers() & ActionEvent.ALT_MASK);
                                      AV2.getButtonHandler().handle(secondTable, alt, e.getActionCommand());
                                  }
                              }
                );
        return btn;
    }

    public enum AV_TABLE_BUTTON{
        NEW, CLONE, UPGRADE, OPEN,
        LEVEL_UP(DC_TYPE.CHARS, DC_TYPE.UNITS),
        CLEAR, ROLLBACK, COMPARE,
        REMOVE, RENAME, COPY, PASTE, SET;

        OBJ_TYPE[] forTypes;

        AV_TABLE_BUTTON(OBJ_TYPE... forTypes) {
            this.forTypes = forTypes;
        }

        @Override
        public String toString() {
            return StringMaster.format(name());
        }
    }

}
