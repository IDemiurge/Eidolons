package eidolons.system.text;

import eidolons.system.text.ToolTipMaster.TOOLTIP_TYPE;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TooltipMouseListener implements MouseListener {

    TOOLTIP_TYPE type;
    Object arg;

    public TooltipMouseListener(TOOLTIP_TYPE type, ToolTipMaster master) {
        this.type = type;
    }

    public TooltipMouseListener(TOOLTIP_TYPE t, ToolTipMaster toolTipMaster, Object arg) {
        this(t, toolTipMaster);
        this.arg = arg;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
