package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.content.UNIT_INFO_PARAMS.ActionToolTipSections;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static main.content.UNIT_INFO_PARAMS.ActionToolTipSections.*;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ActionToolTip extends ToolTip {
    private TablePanel baseTable;
    private TablePanel rangeTable;

    private List<TablePanel> textTables = new ArrayList<>();

    @Override
    public void updateAct(float delta) {
        final Map<ActionToolTipSections, List> paramsListMap = ((Supplier<Map<ActionToolTipSections, List>>) getUserObject()).get();

        List list = paramsListMap.get(HEAD);
        final MultiValueContainer valueContainer = (MultiValueContainer) list.get(0);

        final List<Container<Label>> values = valueContainer.getValues();
        final TextureRegion leftImage = getOrCreateR(values.get(0).getActor().getText().toString());
        final TextureRegion rightImage = getOrCreateR(values.get(1).getActor().getText().toString());

        TablePanel headerTable = new TablePanel();
        headerTable.addElement(new ValueContainer(rightImage));
        headerTable.addElement(new ValueContainer(leftImage));
        headerTable.addElement(new ValueContainer(valueContainer.getName(), ""));


        baseTable = new TablePanel();
        baseTable.addElement(headerTable);

        list = paramsListMap.get(BASE);

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            baseTable.addElement(container);
            baseTable.row();
        }

        rangeTable = new TablePanel();

        list = paramsListMap.get(RANGE);

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            rangeTable.addElement(container);
            rangeTable.row();
        }

        list = paramsListMap.get(TEXT);

        for (Object o : list) {
            List<ValueContainer> valueContainers = (List<ValueContainer>) o;
            TablePanel panel = new TablePanel();
            textTables.add(panel);
            for (ValueContainer container : valueContainers) {
                panel.addElement(container);
                panel.row();
            }
        }

        addElement(baseTable).width(282).pad(0, 0, 5, 0);
        row();
        addElement(rangeTable).width(282).pad(0, 0, 5, 0);
        row();
        textTables.forEach(el -> {
            addElement(el).width(282).pad(0, 0, 5, 0);
            row();
        });
    }

    @Override
    public void afterUpdateAct(float delta) {
        baseTable.setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));

        rangeTable.setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));

        textTables.forEach(tablePanel -> {
            tablePanel.setBackground(new NinePatchDrawable(NinePathFactory.getTooltip()));
        });
    }
}
