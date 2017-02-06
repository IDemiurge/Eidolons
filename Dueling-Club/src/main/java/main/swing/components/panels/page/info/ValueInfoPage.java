package main.swing.components.panels.page.info;

import main.content.VALUE;
import main.content.properties.G_PROPS;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.swing.components.panels.page.info.element.ContainerTextElement;
import main.swing.components.panels.page.info.element.EntityValueComponent;
import main.swing.components.panels.page.info.element.PropertyElement;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.secondary.InfoMaster;

import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class ValueInfoPage extends InfoPage {
    public static final int INNER_HEIGHT = 200;
    public static final int INNER_WIDTH = 240;

    protected Map<VALUE, EntityValueComponent> compMap = new XLinkedMap<>();
    protected List<VALUE> values;
    protected int Z;
    protected int X;
    protected int Y;
    protected int i;
    private EntityValueComponent headerComp;
    private boolean noVisuals;
    private String header;

    /**
     * back button
     *
     * @param list
     * @param entity
     */
    public ValueInfoPage(List<VALUE> list, Entity entity, String header) {
        super(getPanelVisuals());
        // this.setBackgroundVisuals(true);
        this.entity = entity;
        this.values = list;
        this.header = header;
        init();

    }

    public ValueInfoPage(List<VALUE> list, Entity entity, boolean noVisuals) {
        super(null);
        this.entity = entity;
        this.values = list;
        init();
        this.noVisuals = noVisuals;
    }

    protected static VISUALS getPanelVisuals() {
        return VISUALS.INFO_PANEL;
    }

    protected String getHeaderText() {
        if (header != null) {
            return entity.getDisplayedName() + " " +
                    // StringMaster.wrapInParenthesis
                    (header);
        }
        return entity.getDisplayedName() + InfoMaster.getWorkspaceTip(entity);
    }

    protected boolean isAddHeader() {
        return true;
    }

    protected void addHeader() {
        if (FontMaster.getStringWidth(FontMaster.getDefaultFont(PropertyElement.DEFAULT_SIZE),
                getHeaderText()) > PropertyElement.ALT_HEADER_COMPONENT.getWidth() * 8 / 10) {
            headerComp = new ContainerTextElement(getHeaderText());
        } else {
            headerComp = new PropertyElement(getHeaderText());
        }
        // String constraints = ((VISUALS.INFO_PANEL.getImg().getWidth(null) -
        // PropertyElement.ALT_HEADER_COMPONENT
        // .getImg().getWidth(null)) / 2) + " " + getDefaultY();
        addComponent(headerComp, G_PROPS.NAME);
        // add(headerComp, "pos " + constraints);
        // setComponentZOrder(headerComp, compMap.size());
    }

    protected void init() {
        if (!isInitialized()) {
            return;
        }

        X = getDefaultX();
        Y = getDefaultY();
        i = 0;
        Z = getDefaultZ();
        if (isAddHeader()) {
            addHeader();
        }
        for (VALUE p : values) {
            EntityValueComponent comp = getComponent(p);
            addComponent(comp, p);

        }
        initialized = true;
    }

    protected int getColumns() {
        return 1;
    }

    protected void addComponent(EntityValueComponent comp, VALUE p) {
        comp.setEntity(entity);
        comp.refresh();
        add(comp.getComponent(), "pos " + X + " " + Y);
        setComponentZOrder(comp.getComponent(), Z);
        Z++;
        X += getColumnWidth();
        i++;
        if (i >= getColumns()) {
            X = getDefaultX();

            Y += comp.getComponent().getPreferredSize().getHeight();
            i = 0;
        }
        compMap.put(p, comp);

    }

    protected int getDefaultZ() {
        return 0;
    }

    protected int getDefaultY() {
        return GraphicComponent.STD_COMP_IMAGES.ARROW_3_UP.getImg().getHeight(null);
    }

    protected int getDefaultX() {
        return GraphicComponent.STD_COMP_IMAGES.ARROW_3_RIGHT.getImg().getWidth(null) + 5;
    }

    public Dimension getPreferredSize() {
        return getMaximumSize();
    }

    public Dimension getMaximumSize() {
        return new Dimension(getPanelVisuals().getWidth(), getPanelVisuals().getHeight());
    }

    protected abstract EntityValueComponent getComponent(VALUE p);

    protected abstract int getColumnWidth();

    protected abstract int getRowHeight();

    @Override
    public void refresh() {

        removeAll();
        init();
        revalidate();
        super.refresh();
    }

    public Map<VALUE, EntityValueComponent> getCompMap() {
        return compMap;
    }

}
