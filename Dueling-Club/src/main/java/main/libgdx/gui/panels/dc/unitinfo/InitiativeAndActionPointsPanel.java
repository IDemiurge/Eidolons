package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.InitiativeAndActionPointsSource;
import main.libgdx.texture.TextureCache;

public class InitiativeAndActionPointsPanel extends TablePanel {
    private final VerticalValueContainer initiativeContainer;
    private final VerticalValueContainer actionContainer;

    public InitiativeAndActionPointsPanel() {
        rowDirection = TOP_RIGHT;
        initiativeContainer = new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/n_of_counters_s.png"), "n/a");
        actionContainer = new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/n_of_actions_s.png"), "n/a");

        initiativeContainer.fill().left().bottom();
        actionContainer.fill().right().bottom();

        Table innerTable = new Table();
        innerTable.add(initiativeContainer).fill().left().bottom();
        innerTable.add(actionContainer).fill().right().bottom();

        addElement(new Container(innerTable).center().bottom());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            InitiativeAndActionPointsSource source = (InitiativeAndActionPointsSource) getUserObject();

            initiativeContainer.updateValue(source.getInitiative());
            actionContainer.updateValue(source.getActionPoints());

            updatePanel = false;
        }
    }
}
