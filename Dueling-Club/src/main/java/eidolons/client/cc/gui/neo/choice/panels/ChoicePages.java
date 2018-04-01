package eidolons.client.cc.gui.neo.choice.panels;

import eidolons.client.cc.gui.neo.choice.ChoiceView;
import eidolons.client.cc.gui.neo.choice.PagedSelectionPanel;

public class ChoicePages<E> extends PagedSelectionPanel<E> {

    public ChoicePages(ChoiceView<E> panel, int pageSize, int itemSize,
                       int columnsCount) {
        super(panel, pageSize, itemSize, columnsCount);
        this.visuals = VISUALS.V_CHOICE_LIST_8_12;
    }

}
