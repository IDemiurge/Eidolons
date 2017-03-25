package main.libgdx.gui.panels.dc.newlayout;

import com.badlogic.gdx.graphics.g2d.Batch;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class StatsPanel extends BasePanel {

    private List<NewTable> tables = new ArrayList<>();

    @Override
    public void updateAct(float delta) {
        List<List<ValueContainer>> valueContainers = ((Supplier<List<List<ValueContainer>>>) getUserObject()).get();

        int fullSize = valueContainers.size();

        for (List<ValueContainer> valueContainer : valueContainers) {
            fullSize += valueContainer.size();
        }

        int halfSize = fullSize / 2;
        if (fullSize % 2 != 0) {
            halfSize++;
        }


        for (int i = 0, valueContainersSize = valueContainers.size(); i < valueContainersSize; i++) {
            List<ValueContainer> valueContainer = valueContainers.get(i);
            NewTable table = new NewTable(new float[]{50, 50}, fullSize, NewTable.AlignW.LEFT, NewTable.AlignH.BOTTOM);
            NewTable leftTable = new NewTable(new float[]{80, 20}, halfSize, NewTable.AlignW.LEFT, NewTable.AlignH.BOTTOM);
            NewTable rightTable = new NewTable(new float[]{80, 20}, halfSize, NewTable.AlignW.LEFT, NewTable.AlignH.BOTTOM);
            table.addAt(0, 0, leftTable);
            table.addAt(1, 0, rightTable);
            final Iterator<ValueContainer> iterator = valueContainer.iterator();
            while (iterator.hasNext()) {
/*                leftTable.addValueContainer(iterator.next());
                if (iterator.hasNext()) {
                    rightTable.addValueContainer(iterator.next());
                }*/
            }

            tables.add(table);
            addActor(table);
        }

        for (int i = 0, tablesSize = tables.size(); i < tablesSize; i++) {
            NewTable table = tables.get(i);
            final float parentH = getHeight();
            final float y = (parentH - table.getHeight()) * (i + 1);
            final float offset = 20 * i;
            table.setPosition(0, y + offset);
        }
        setWidth(382);
        setHeight(400);
        // todo add border functional to base table
        // .setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
