package eidolons.libgdx.gui.menu.selection.scenario;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.content.PROPS;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import main.system.graphics.FontMaster.FONT;
import main.system.text.TextWrapper;

/**
 * Created by JustMe on 11/30/2017.
 */
public class ScenarioInfoPanel extends ItemInfoPanel {
    private Label mainInfo;
    private Label missionsInfo;
    private Label partyInfo;

    public ScenarioInfoPanel(SelectableItemData item) {
        super(item);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        String text = "Missions: " +
         item.getEntity().getProperty(PROPS.SCENARIO_MISSIONS).replace(";", ", ");
        if (text.endsWith(";"))
            text = text.substring(0, text.length() - 1);
        text = TextWrapper.wrapWithNewLine(text, 40);
        missionsInfo.setText(text);

        text = item.getEntity().getProperty(PROPS.DIFFICULTY);
        if (text.isEmpty())
            text = "Unknown";
        mainInfo.setText("Difficulty: " +
         text);

        text = item.getEntity().getProperty(PROPS.SCENARIO_PARTY);
        if (text.isEmpty())
            text = "Unknown";
        partyInfo.setText("Party: " +
         text);
    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {

        header.addNormalSize(preview).left().padLeft(20).padTop(45);
        header.addElement(title).left();

        header.row();
        TablePanel<Actor> infoTable = new TablePanel<>();
        header.addNoGrow(infoTable);
        infoTable.padLeft(50).padTop(50);
        missionsInfo = new Label("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 18));
        missionsInfo.setText("Missions: N/A");
        infoTable.addNoGrow(missionsInfo);
        infoTable.row();

        partyInfo = new Label("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 18));
        partyInfo.setText("Party: N/A");
        infoTable.addNoGrow(partyInfo);
        infoTable.row();


        mainInfo = new Label("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 18));
        mainInfo.setText("Difficulty: Unknown");
        infoTable.addNoGrow(mainInfo);

    }
}
