package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.content.UNIT_INFO_PARAMS.ActionToolTipSections;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static main.content.UNIT_INFO_PARAMS.ActionToolTipSections.*;
import static main.libgdx.texture.TextureCache.getOrCreate;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ActionToolTip extends ToolTip<Supplier<Map<ActionToolTipSections, List>>> {
    private TablePanel baseTable;
    private TablePanel rangeTable;

    private List<TablePanel> textTables = new ArrayList<>();

    @Override
    public void updateAct() {
        final Map<ActionToolTipSections, List> paramsListMap = getUserObject().get();

        List list = paramsListMap.get(HEAD);
        final MultiValueContainer valueContainer = (MultiValueContainer) list.get(0);

        final List<Container<Label>> values = valueContainer.getValues();
        final TextureRegion leftImage = getOrCreateR(values.get(0).getActor().getText().toString());
        final TextureRegion rightImage = getOrCreateR(values.get(1).getActor().getText().toString());

        TablePanel headerTable = new TablePanel() {{
            rowDirection = TOP_LEFT;
        }};
        headerTable.addElement(new Container<>(new ValueContainer(valueContainer.getName(), "")));
        headerTable.addElement(new Container<>(new ValueContainer(leftImage)));
        headerTable.addElement(new Container<>(new ValueContainer(rightImage)));

        baseTable = new TablePanel();
        baseTable.addElement(headerTable);

        list = paramsListMap.get(BASE);

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            baseTable.addElement(container);
        }

        rangeTable = new TablePanel();

        list = paramsListMap.get(RANGE);

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            rangeTable.addElement(container);
        }

        list = paramsListMap.get(TEXT);

        for (Object o : list) {
            List<ValueContainer> valueContainers = (List<ValueContainer>) o;
            TablePanel panel = new TablePanel();
            panel.fill().left().bottom();
            textTables.add(panel);
            for (ValueContainer container : valueContainers) {
                panel.addElement(container);
            }
        }

        inner.addElement(baseTable.fill().left().bottom());
        inner.addElement(rangeTable.fill().left().bottom());
        textTables.forEach(inner::addElement);
    }

    @Override
    protected void postUpdateAct() {
        inner.pad(20);

        NinePatchDrawable ninePatchDrawable =
                new NinePatchDrawable(new NinePatch(getOrCreate("UI/components/tooltip_background.9.png")));
        baseTable.setBackground(ninePatchDrawable);

        ninePatchDrawable =
                new NinePatchDrawable(new NinePatch(getOrCreate("UI/components/tooltip_background.9.png")));
        rangeTable.setBackground(ninePatchDrawable);

        textTables.forEach(tablePanel -> {
            NinePatchDrawable npd =
                    new NinePatchDrawable(new NinePatch(getOrCreate("UI/components/tooltip_background.9.png")));
            rangeTable.setBackground(npd);
        });
    }
}
