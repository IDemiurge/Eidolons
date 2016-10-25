package main.swing.components.panels.secondary;

import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.swing.components.panels.DC_CellInfoPanel;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DeadUnitPanel extends G_PagedListPanel<DC_HeroObj> implements MouseListener {
    public static final int PAGE_SIZE = 6;
    /*
     * not all corpses are available/visible - stacking rule should apply here too!
     *
     * could be displayed as dialog!
     */
    private Obj cell;

    public DeadUnitPanel(DC_CellInfoPanel cellInfoPanel) {
        super(PAGE_SIZE, false, 3);
        setPageMouseListener(this);
    }

    @Override
    public int getWrap() {
        return 3;
    }

    @Override
    protected int getArrowOffsetX2() {
        int x = getPanelWidth() + 2 * getArrowWidth();
        return x;
    }

    @Override
    protected int getArrowOffsetY() {
        return super.getArrowOffsetY() + getArrowHeight() / 2;
    }

    @Override
    protected int getArrowOffsetY2() {
        return super.getArrowOffsetY2() + getArrowHeight() / 2;
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        return new DeadUnitPage(new LinkedList<DC_HeroObj>());
    }

    @Override
    protected G_Component createPageComponent(List<DC_HeroObj> list) {
        return new DeadUnitPage(list);
    }

    @Override
    protected List<List<DC_HeroObj>> getPageData() {
        List<Obj> corpses = getCell().getGame().getGraveyardManager().getDeadUnits(
                cell.getCoordinates());

        Collection<DC_HeroObj> units = new LinkedList<>();
        if (corpses != null)
            for (Obj obj : corpses) {
                units.add((DC_HeroObj) obj);
            }
        return splitList(units);
    }

    @Override
    protected int getItemSize() {
        return 96;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        DC_HeroObj unit = getList().getSelectedValue();
        if (unit == null)
            return;
        if (unit.getGame().getManager().isSelecting() || SwingUtilities.isRightMouseButton(e))
            unit.getGame().getManager().objClicked(unit);
        // or if (isDialog())

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

    public Obj getCell() {
        return cell;
    }

    public void setCell(Obj cell) {
        this.cell = cell;
    }

}
