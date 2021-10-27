package libgdx.gui.dungeon.menu.selection.scenario;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.PROPS;
import libgdx.GDX;
import libgdx.StyleHolder;
import libgdx.gui.LabelX;
import libgdx.gui.dungeon.menu.selection.ItemInfoPanel;
import libgdx.gui.dungeon.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.dungeon.panels.TablePanel;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/30/2017.
 */
public class ScenarioInfoPanel extends ItemInfoPanel {
    protected LabelX mainInfo;
    protected LabelX missionsInfo;
    protected LabelX partyInfo;

    public ScenarioInfoPanel(SelectableItemData item) {
        super(item);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        String text = "Missions: " +
         item.getEntity().getProperty(PROPS.SCENARIO_MISSIONS).replace(";", ", ");
        if (text.endsWith(";"))
            text = text.substring(0, text.length() - 1);
        missionsInfo.setText(text);

        text = item.getEntity().getProperty(PROPS.DIFFICULTY);
        if (text.isEmpty())
            text = "Unknown";
        mainInfo.setText("Difficulty: " +
         text);

        mainInfo.setText("A touch of Fate...");


        text = item.getEntity().getProperty(PROPS.SCENARIO_PARTY);
        if (text.isEmpty())
            text = "Unknown";
        partyInfo.setText("Party: " +
         text);
    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {
        super.initHeader(header);

//        header.setBackground(NinePatchFactory.getLightDecorPanelDrawable());
        header.row();
        TablePanel<Actor> infoTable = new TablePanel<>();
        header.addNoGrow(infoTable);
        infoTable.padLeft(GDX.size(  50)).padTop(GDX.size(  50));
        missionsInfo = new LabelX("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 20));
        missionsInfo.setMaxWidth(GDX.size(ItemInfoPanel.WIDTH-50)-500);

        missionsInfo.setText("Missions: N/A");
        infoTable.addNoGrow(missionsInfo);
        infoTable.row();

        partyInfo = new LabelX("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 20));
        partyInfo.setText("Party: N/A");
        infoTable.addNoGrow(partyInfo);
        infoTable.row();
        partyInfo.setMaxWidth(GDX.size(ItemInfoPanel.WIDTH-50)-500);


        mainInfo = new LabelX("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 20));
        mainInfo.setText("Difficulty: Unknown");
        infoTable.addNoGrow(mainInfo);
        mainInfo.setMaxWidth(GDX.size(ItemInfoPanel.WIDTH-50)-500);

    }
}
