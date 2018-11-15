package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ArmorDataSource;

public class ArmorPanel extends TablePanel {
    public ArmorPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();
        final ArmorDataSource source = (ArmorDataSource) getUserObject();

        addElement(source.getArmorObj()).right().fill(false);
//        row();
        TablePanelX<Actor> table = new TablePanelX<>(200, 64);
        addElement(table).left().fill(false) ;
        for (ValueContainer valueContainer : source.getParamValues()) {
            valueContainer.setBackground(
//             getOrCreateR(
//             "ui/components/ninepatch/std/background_3px_border.png")
             NinePatchFactory.getLightPanelFilledDrawable()
            );
            table. addElement(valueContainer).fill(false) ;
            table.row();
        }

    }
}
