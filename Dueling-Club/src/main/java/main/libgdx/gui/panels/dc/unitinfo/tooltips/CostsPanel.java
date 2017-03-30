package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class CostsPanel extends TablePanel {

    private Cell costsPanel;

    public CostsPanel() {
        addElement(new ValueContainer(getOrCreateR("UI\\components\\infopanel\\action_cost_header.png")))
                .fill(1, 0).expand(1, 0).top();
        row();
        costsPanel = addElement(null)
                .fill(1, 0).expand(1, 0).top().pad(0, 10, 0, 10);

        addElement(null);//plug element

    }

    @Override
    public void clear() {

    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        final List<ValueContainer> values = (List<ValueContainer>) getUserObject();

        TablePanel costsTable = new TablePanel();

        for (int i = 0; i < values.size(); i++) {
            final ValueContainer valueContainer = values.get(i);
            if (i % 2 == 0) {
                costsTable.row();
            }

            costsTable.add(valueContainer).expand(1, 0).fill(1, 0);

        }

        costsPanel.setActor(costsTable);
    }

    @Override
    public void afterUpdateAct(float delta) {
        if (costsPanel.getActor() != null) {
            ((TablePanel) costsPanel.getActor()).setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
        }
    }
}
