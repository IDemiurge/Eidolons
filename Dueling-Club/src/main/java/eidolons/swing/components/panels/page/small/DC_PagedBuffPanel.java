package eidolons.swing.components.panels.page.small;

import main.content.values.parameters.G_PARAMS;
import main.entity.obj.BuffObj;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.datatypes.DequeImpl;
import main.system.graphics.GuiManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DC_PagedBuffPanel extends G_PagePanel<BuffObj> {
    private static final int PAGE_SIZE = 5;
    private static final int VERSION = 5;

    public DC_PagedBuffPanel() {
        super(PAGE_SIZE, false, VERSION);

    }

    @Override
    protected boolean isAddControlsAlways() {
        return true;
    }

    @Override
    protected G_Component createPageComponent(List<BuffObj> list) {
        List<SmallItem> compList = new ArrayList<>();
        for (BuffObj buff : list) {
            if (buff == null) {
                compList.add(new SmallItem());
            } else {
                compList.add(new SmallItem(buff));
            }
        }
        BuffPage buffPage = new BuffPage(compList, getItemSize(), PAGE_SIZE);
        buffPage.getList().setCellRenderer(buffPage);
        return buffPage;
    }

    @Override
    protected int getItemSize() {
        return GuiManager.getSmallObjSize() / 2;
    }

    @Override
    protected int getArrowOffsetY() {
        return (getItemSize() - arrowHeight) / 2;
    }

    @Override
    protected int getArrowOffsetY2() {
        return getArrowOffsetY();
    }

    @Override
    protected List<List<BuffObj>> getPageData() {
        if (obj == null) {
            return new ArrayList<>();
        }
        DequeImpl<BuffObj> buffs = obj.getBuffs();
        if (buffs == null) {
            return new ArrayList<>();
        }
        List<BuffObj> list = new ArrayList<>();
        for (BuffObj buff : buffs) {
            if (buff.isVisible()) {
                list.add(buff);
            }
        }
        Collections.sort(list, getComparator());
        return splitList(list);
    }

    private Comparator<BuffObj> getComparator() {

        return new Comparator<BuffObj>() {

            @Override
            public int compare(BuffObj o1, BuffObj o2) {
                // if (o1.getProperty(G_PROPS.BUFF_TYPE).equals())
                // if (!o2.getProperty(G_PROPS.BUFF_TYPE).equals())
                // return -1;
                //
                // if (!o1.getProperty(G_PROPS.BUFF_TYPE).equals())
                // if (o2.getProperty(G_PROPS.BUFF_TYPE).equals())
                // return 1;

                // standard -> buff -> debuff -> other

                // preCheck group as well

                if (o1.isPermanent()) {
                    if (!o2.isPermanent()) {
                        return 1;
                    }

                }
                if (o2.isPermanent()) {
                    if (!o1.isPermanent()) {
                        return -1;
                    }
                }

                if (o1.getIntParam(G_PARAMS.C_DURATION) < o2
                 .getIntParam(G_PARAMS.C_DURATION)) {
                    return 1;
                }
                if (o1.getIntParam(G_PARAMS.C_DURATION) > o2
                 .getIntParam(G_PARAMS.C_DURATION)) {
                    return -1;
                }

                if (o1.getIntParam(G_PARAMS.DURATION) < o2
                 .getIntParam(G_PARAMS.DURATION)) {
                    return 1;
                }
                if (o1.getIntParam(G_PARAMS.DURATION) > o2
                 .getIntParam(G_PARAMS.DURATION)) {
                    return -1;
                }

                return 0;
            }
        };
    }

}
