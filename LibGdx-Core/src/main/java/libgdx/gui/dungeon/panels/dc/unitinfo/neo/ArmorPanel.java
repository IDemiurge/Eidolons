package libgdx.gui.dungeon.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.dc.unitinfo.datasource.ArmorDataSource;

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
