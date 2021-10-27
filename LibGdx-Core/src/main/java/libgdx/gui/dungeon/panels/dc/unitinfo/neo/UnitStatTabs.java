package libgdx.gui.dungeon.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import libgdx.GdxMaster;
import libgdx.gui.dungeon.panels.TabbedPanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.headquarters.hero.HqScrolledValuePanel;
import libgdx.gui.dungeon.panels.headquarters.tabs.stats.HqAttributeTable;
import libgdx.gui.dungeon.panels.headquarters.tabs.stats.HqMasteryTable;

/**
 * Created by JustMe on 11/15/2018.
 */
public class UnitStatTabs extends TabbedPanel {
    private final HqScrolledValuePanel scrolledValuePanel;


    public UnitStatTabs(float w, float h) {
        final float height = h * getHeightCoef();
        final float width = w * getWidthCoef();

        scrolledValuePanel = new HqScrolledValuePanel(
                GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));

        HqMasteryTable masteryTable = new HqMasteryTable();
        HqAttributeTable attributeTable = new HqAttributeTable();
        attributeTable.setEditable(false);
        attributeTable.setSize(width, height);
        masteryTable.setSize(width, height);

        addTab(attributeTable, "Attributes");
        addTab(masteryTable, "Mastery Scores");
//        addTab(scrolledValuePanel, "Stats");
        //        main.setSize(width,height);
        contentTable.setSize(GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));
        tabSelected("Attributes");
        setSize(GdxMaster.adjustWidth(width), GdxMaster.adjustHeight(height));
    }

    private float getWidthCoef() {
        return 0.80f;
    }

    private float getHeightCoef() {
        return 0.82f;
    }

    protected TablePanelX createContentsTable() {
        return new TablePanelX<>(
                getWidth() * getWidthCoef(), getHeight() * getHeightCoef());
    }

    @Override
    protected Cell setDisplayedActor(Actor actor) {
        Cell cell = super.setDisplayedActor(actor);
//            if (actor == scrolledValuePanel)
        {
            scrolledValuePanel.setUserObject(getUserObject());
            scrolledValuePanel.updateAct(0);
        }
        return cell;
    }

    @Override
    protected int getDefaultAlignment() {
        return Align.top;
    }

    @Override
    protected int getDefaultTabAlignment() {
        return Align.center;
    }

    @Override
    protected Cell createContentsCell() {
        return super.createContentsCell().padTop(20);
    }

    @Override
    protected Cell addTabTable() {
        return  super.addTabTable().space(20);
    }

    @Override
    protected Cell<TextButton> addTabActor(TextButton b) {
        return super.addTabActor(b);
    }
}

