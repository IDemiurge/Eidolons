package libgdx.gui.dungeon.panels.headquarters.hero;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.gui.dungeon.panels.ScrollPanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.dc.unitinfo.datasource.UnitDataSource;
import libgdx.gui.dungeon.panels.headquarters.HqElement;
import main.content.VALUE;

/**
 * Created by JustMe on 6/13/2018.
 */
public class HqScrolledValuePanel extends HqElement {

    private final ScrollPanel scroll;

    public HqScrolledValuePanel() {
        this(500, 230);
    }

    public HqScrolledValuePanel(float w, float h) {
        setSize(w, h);
        HqVerticalValueTable valueTable = new StatsListPanel(getValuesOne());
        HqVerticalValueTable valueTable2 = new StatsListPanel(getValuesTwo());
        valueTable.setSize(w / 2, h);
        valueTable2.setSize(w / 2, h);
        TablePanelX<Actor> table = new TablePanelX<>();
        table.add(valueTable);
        table.add(valueTable2);
        add(scroll = new ScrollPanel() {
            @Override
            public int getDefaultOffsetY() {
                return 0;
            }

            @Override
            public Integer getScrollAmount() {
                return super.getScrollAmount();
            }

            @Override
            protected float getUpperLimit() {
                return -1;
            }

            @Override
            protected float getLowerLimit() {
                return -1;
            }

        });
        scroll.addElement(table);
        scroll.pad(1, 10, 1, 10);
        scroll.fill();
    }

    private VALUE[] getValuesOne() {
        return UnitDataSource.getStatsValuesSplit(2, 1);
    }

    private VALUE[] getValuesTwo() {
        return UnitDataSource.getStatsValuesSplit(2, 2);
    }

    @Override
    protected void update(float delta) {
    }

    @Override
    protected void setUserObjectForChildren(Object userObject) {
        super.setUserObjectForChildren(userObject);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        scroll.setSize(getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
