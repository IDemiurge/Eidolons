package main.swing.generic.services.dialog;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.graphics.MigMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoicePanel<T> extends DialogPanel implements MouseListener {
    protected T lastSelected;
    protected String tooltip;
    protected int row = 0;
    List<T> data;
    private Map<Rectangle, T> mouseMap = new HashMap<>();

    public ChoicePanel(List<T> itemData, Unit target) {
        super(target);
        this.data = itemData;
        initSize();
        addMouseListener(this);
        int n = 0;
        for (T t : data) {
            addItemComp(t, n);
            n++;
            if (n > getWrap()) {
                n = 0;
                row++;
            }
        }
    }

    public T chooseEntity() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
        T waitForInput = (T) WaitMaster.waitForInput(getWaitOperation());
        close();
        return waitForInput;
    }

    protected WAIT_OPERATIONS getWaitOperation() {
        return WAIT_OPERATIONS.DIALOGUE_DONE;
    }

    @Override
    protected void ok() {
        if (lastSelected == null) {
            WaitMaster.interrupt(getWaitOperation());
        } else {
            WaitMaster.receiveInput(getWaitOperation(), lastSelected);
        }
        super.ok();
    }

    protected int getWrap() {
        return 4;
    }

    public void initSize() {
        setPanelSize(new Dimension(getObjWidth() * data.size(), getObjHeight()));

    }

    protected boolean isSelected(T t) {
        return lastSelected == t;
    }

    private void addItemComp(T t, int n) {
        Image img = getImage(t);
        GraphicComponent comp = new GraphicComponent(img);
        comp.setDataObject(t);
        int x = getX(n);
        int y = getY(n);

        compMap.put(comp, new Point(x, y));
        mouseMap.put(new Rectangle(getLocation().x + x, getLocation().y + y, getObjWidth(),
         getObjHeight()), t);
    }

    protected int getY(int n) {
        return MigMaster.getCenteredPosition(getPanelHeight(), getObjHeight());
    }

    protected int getX(int n) {
        return getObjWidth() * n;
    }

    public int getObjHeight() {
        return 0;
    }

    public int getObjWidth() {
        return 0;
    }

    public void clicked(T t) {

    }

    public Image getImage(T t) {
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        T t = getItem(e);
        if (t == null) {
            super.mouseClicked(e);
            return;
        }

        lastSelected = t;
        clicked(t);
        refresh();

    }

    @Override
    public void refresh() {
        for (Component c : compMap.keySet()) {
            if (c instanceof GraphicComponent) {
                GraphicComponent graphicComponent = (GraphicComponent) c;
                graphicComponent.setImg(getImage((T) graphicComponent.getDataObject()));
            }
        }
        if (lastSelected != null) {
            initTooltip(lastSelected);
        }
        super.refresh();
    }

    private T getItem(MouseEvent e) {

        for (Rectangle r : mouseMap.keySet()) {
            if (r.contains(e.getLocationOnScreen())) {
                return mouseMap.get(r);

            }
        }
        return null;
    }

    protected String getTooltip(DC_ActiveObj t) {
        return tooltip;
    }

    protected void initTooltip(T t) {

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

    public T getSelectedValue() {
        return lastSelected;
    }

}
