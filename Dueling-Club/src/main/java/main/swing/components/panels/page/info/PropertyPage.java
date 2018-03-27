package main.swing.components.panels.page.info;

import main.content.PROPS;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.swing.components.panels.page.info.element.*;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;

import java.util.ArrayList;
import java.util.List;

public class PropertyPage extends ValueInfoPage {

    private List<VALUE> displayedValues;

    public PropertyPage(String header, List<VALUE> list, Entity entity) {
        super(list, entity, header);
    }

    @Override
    protected void init() {

        X = super.getDefaultX();
        Y = getDefaultY();
        i = 0;
        Z = getDefaultZ();
        if (isAddHeader()) {
            addHeader();
        }
        resetDisplayedValues();
        for (VALUE p : displayedValues) {
            EntityValueComponent comp = getComponent(p);
            super.addComponent(comp, p);

        }
    }

    private void resetDisplayedValues() {
        displayedValues = new ArrayList<>();
        if (getEntity() == null) {
            return;
        }
        for (VALUE p : values) {
            if (!getEntity().getValue(p).isEmpty()) {
                displayedValues.add(p);
            }
        }
        if (values.remove(PROPS.PARAMETER_BONUSES)) {
            if (getEntity().getDescription().length() < 30) {
                displayedValues.add(PROPS.PARAMETER_BONUSES);
            }
        }
    }

    @Override
    protected EntityValueComponent getComponent(VALUE v) {

        if (v instanceof PARAMETER) {
            ParamElement pElement = new ParamElement(v);
            pElement.setEntity(entity);
            return pElement;
        }
        PROPERTY p = (PROPERTY) v;
        if (isIconContainer(p)) {
            ContainerIconElement containerIconElement = new ContainerIconElement(p);
            X = STD_COMP_IMAGES.ARROW_3_RIGHT.getImg().getWidth(null) / 2
                    + (VISUALS.INFO_PANEL.getImage().getWidth(null) - ValueInfoPage.INNER_WIDTH)
                    / 2;
            containerIconElement.setEntity(entity);
            return containerIconElement;
        }
        if (isWrappedText(p)) {
            X = (VISUALS.INFO_PANEL.getImage().getWidth(null) - ValueInfoPage.INNER_WIDTH) / 2;
            ContainerTextElement containerTextElement = new ContainerTextElement(p);
            containerTextElement.setEntity(entity);
            containerTextElement.refresh();
            return containerTextElement;
        }
        PropertyElement propertyElement = new PropertyElement(p);
        propertyElement.setEntity(entity);
        return propertyElement;
    }

    private boolean isIconContainer(PROPERTY p) {
        if (p == G_PROPS.STANDARD_PASSIVES) {
            return true;
        }
        if (p == G_PROPS.PASSIVES) {
            return true;
        }
        if (p == PROPS.FAVORED_SPELL_GROUPS) {
            return true;
        }
        return p == G_PROPS.CLASSIFICATIONS;
    }

    private boolean isWrappedText(PROPERTY p) {
        if (p == G_PROPS.LORE) {
            return true;
        }
        if (p == G_PROPS.DEITY) {
            return false;
        }
        if (p == G_PROPS.MODE) {
            return true;
        }
        if (p == G_PROPS.DESCRIPTION) {
            return true;
        }
        if (p == G_PROPS.FLAVOR) {
            return true;
        }
        if (p == G_PROPS.CLASSIFICATIONS) {
            return false;
        }
        return p.isContainer();
    }

    protected int getDefaultX() {
        return (VISUALS.INFO_PANEL.getImage().getWidth(null) - PropertyElement.HEADER_COMPONENT
                .getImage().getWidth(null)) / 2;
    }

    protected int getColumnWidth() {
        return PropertyElement.HEADER_COMPONENT.getWidth();
    }

    @Override
    protected int getRowHeight() {
        return PropertyElement.HEADER_COMPONENT.getHeight();
    }
}
