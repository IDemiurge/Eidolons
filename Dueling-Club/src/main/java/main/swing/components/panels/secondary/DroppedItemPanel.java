package main.swing.components.panels.secondary;

import main.entity.item.DC_HeroItemObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagedListPanel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DroppedItemPanel extends G_PagedListPanel<DC_HeroItemObj> {

    public static final int PAGE_SIZE = 8;
    private DC_Obj cell;

    public DroppedItemPanel() {
        super(PAGE_SIZE, false, 3);
    }

    @Override
    public int getWrap() {
        return 4;
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        return new DroppedItemPage(new LinkedList<>());
    }

    @Override
    protected int getArrowOffsetX2() {
        int x = getPanelWidth() + 2 * getArrowWidth();
        return x;
    }

    @Override
    protected boolean isDoubleButtons() {
        return super.isDoubleButtons();
    }

    @Override
    protected int getArrowOffsetY() {
        return super.getArrowOffsetY();
    }

    @Override
    protected G_Component createPageComponent(List<DC_HeroItemObj> list) {
        return new DroppedItemPage(list);
    }

    @Override
    protected List<List<DC_HeroItemObj>> getPageData() {
        Collection<? extends Obj> items = getCell().getGame().getDroppedItemManager()
                .getDroppedItems(cell);
        Collection<DC_HeroItemObj> list = new LinkedList<>();
        for (Obj obj : items) {
            list.add((DC_HeroItemObj) obj);
        }
        return splitList(list);
    }

    @Override
    protected int getItemSize() {
        return 64;
    }

    public DC_Obj getCell() {
        return cell;
    }

    public void setCell(DC_Obj cell) {
        this.cell = cell;
    }

}
