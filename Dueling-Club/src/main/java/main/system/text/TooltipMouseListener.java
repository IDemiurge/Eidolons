package main.system.text;

import main.system.text.ToolTipMaster.TOOLTIP_TYPE;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TooltipMouseListener implements MouseListener {

    TOOLTIP_TYPE type;
    Object arg;
    private ToolTipMaster master;

    public TooltipMouseListener(TOOLTIP_TYPE type, ToolTipMaster master) {
        this.type = type;
        this.master = master;
    }

    public TooltipMouseListener(TOOLTIP_TYPE t, ToolTipMaster toolTipMaster, Object arg) {
        this(t, toolTipMaster);
        this.arg = arg;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e))
            master.initTooltip(type, e, arg);

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
