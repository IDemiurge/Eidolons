package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.MainAttributesSource;

public class MainAttributesPanel extends TablePanel {
    public MainAttributesPanel() {
        super();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        MainAttributesSource source = (MainAttributesSource) getUserObject();

        addElement(source.getResistance());

        addElement(source.getDefense());

        addElement(source.getArmor());

        addElement(source.getFortitude());

        addElement(source.getSpirit());
    }
}
