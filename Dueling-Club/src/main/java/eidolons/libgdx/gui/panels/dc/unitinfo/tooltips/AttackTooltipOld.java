package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.old.MultiValueContainer;

import java.util.ArrayList;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class AttackTooltipOld extends ActionTooltip {
    private TablePanel baseTable;
    private TablePanel rangeTable;

    private List<TablePanel> textTables = new ArrayList<>();

    public AttackTooltipOld(DC_ActiveObj el) {
        super(el);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        TablePanel left = new TablePanel();

        final ActionTooltipSource source = (ActionTooltipSource) getUserObject();

        final MultiValueContainer valueContainer = source.getHead();

        final List<Container<Label>> values = valueContainer.getValues();
        final TextureRegion leftImage = getOrCreateR(values.get(0).getActor().getText().toString());
        final TextureRegion rightImage = getOrCreateR(values.get(1).getActor().getText().toString());


        baseTable = new TablePanel();
        baseTable.addElement(null).expand(0, 0).fill(false);

        baseTable.addElement(new ValueContainer(valueContainer.getName(), ""));
        baseTable.addElement(new ValueContainer(leftImage));
        baseTable.addElement(new ValueContainer(rightImage));
        baseTable.row();


        ValueContainer precalcRow = source.getPrecalcRow();
        if (precalcRow != null) {
            Label label = new Label("Est.", StyleHolder.getDefaultLabelStyle());
            baseTable.addElement(label);
            baseTable.addElement(precalcRow);
            baseTable.row();
        }

        List<MultiValueContainer> list = source.getBase();

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            final List<ValueContainer> separated = container.separate();
            separated.forEach(el -> {
                baseTable.addElement(el);
            });
            baseTable.row();
        }

        rangeTable = new TablePanel();

        list = source.getRange();

        for (Object o : list) {
            MultiValueContainer container = (MultiValueContainer) o;
            final List<ValueContainer> separated = container.separate();
            separated.forEach(el -> {
                rangeTable.addElement(el);
            });
            rangeTable.row();
        }

        List<List<ValueContainer>> listText = source.getText();
        for (List<ValueContainer> valueContainers : listText) {
            TablePanel panel = new TablePanel();
            textTables.add(panel);
            for (ValueContainer container : valueContainers) {
//                container.wrapNames();
                panel.
                 addElement(container);
                panel.
                 row();
            }
        }

        left.addElement(baseTable).width(282).pad(0, 0, 3, 0);
        left.row();
        left.addElement(rangeTable).width(282).pad(0, 0, 3, 0);
        left.row();
        textTables.forEach(el -> {
            left.addElement(el).width(282).pad(0, 0, 3, 0);
            left.row();
        });

        addElement(left);

        CostsPanel costsPanel = new CostsPanel();
        costsPanel.setUserObject(source.getCostsSource());

        addElement(costsPanel);

        for (Actor allChild : GdxMaster.getAllChildren(this)) {
            if (allChild instanceof ValueContainer) {
//                ((ValueContainer) allChild).setFixedMinSize(true);
//                ((ValueContainer) allChild). setFixedSize(true);
                ((ValueContainer) allChild). removeBackground();
                allChild.setWidth(allChild.getWidth()*1.3f);
                allChild.setHeight(allChild.getHeight()*1.1f);
                ((ValueContainer) allChild).setBackground(
                        new NinePatchDrawable(NinePatchFactory.getLightPanelFilledSmall()));
            }
        }
    }

    @Override
    public void afterUpdateAct(float delta) {
//        super.afterUpdateAct(delta);
        baseTable.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

        rangeTable.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));

        textTables.forEach(tablePanel -> {
            tablePanel.setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
        });
    }


}
