package main.swing.components.panels.page.info.element;

import main.ability.AbilityObj;
import main.content.CONTENT_CONSTS.ABILITY_GROUP;
import main.content.CONTENT_CONSTS.CLASSIFICATIONS;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.game.DC_Game;
import main.swing.components.panels.page.small.IconListPanel;
import main.swing.components.panels.page.small.SmallItem;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.text.ToolTipMaster.TOOLTIP_TYPE;
import main.system.text.TooltipMouseListener;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ContainerIconElement extends G_PagePanel<SmallItem> implements EntityValueComponent {
    private static final int VERSION = 4;
    private static final String DISPLAYED_PASSIVES = StringMaster
            .getWellFormattedString(ABILITY_GROUP.STD_PASSIVE.toString());
    private static final int PAGE_SIZE_SMALL = 6;
    private int iconSize;
    private Entity entity;
    private PROPERTY property;

    public ContainerIconElement(PROPERTY p) {
        super(0, false, VERSION);
        this.property = p;
        setPageMouseListener(new TooltipMouseListener(TOOLTIP_TYPE.DC_INFO_PAGE_PASSIVE,
                DC_Game.game.getToolTipMaster()));
    }

    @Override
    public boolean isButtonsOnBothEnds() {
        return true;
    }

    protected boolean isComponentAfterControls() {
        return true;
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        List<SmallItem> emptyList = new LinkedList<>();
        ListMaster.fillWithNullElements(emptyList, pageSize);
        IconListPanel<SmallItem> iconListPanel = new IconListPanel<SmallItem>(
                // ListMaster.getEmptyList(pageSize)
                emptyList, iconSize, pageSize);
        iconListPanel.getList().setCellRenderer(iconListPanel);
        return iconListPanel;
    }

    @Override
    protected G_Component createPageComponent(List<SmallItem> list) {
        IconListPanel<SmallItem> iconListPanel = new IconListPanel<SmallItem>(list, iconSize,
                pageSize);
        iconListPanel.getList().setCellRenderer(iconListPanel);
        return iconListPanel;
    }

    @Override
    protected boolean isAddControlsAlways() {
        return false;
    }

    @Override
    public int getArrowOffsetX() {
        return arrowWidth / 2;
    }

    @Override
    protected int getArrowOffsetX2() {
        return -arrowWidth * 3 / 2;
    }

    @Override
    protected int getArrowOffsetY2() {
        return getArrowOffsetY();
    }

    @Override
    protected int getArrowOffsetY() {
        return (iconSize - arrowHeight) / 2;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    protected List<List<SmallItem>> getPageData() {
        if (property == G_PROPS.STANDARD_PASSIVES || property == G_PROPS.PASSIVES) {
            iconSize = GuiManager.getSmallObjSize() / 2;
            pageSize = PAGE_SIZE_SMALL;
            Collection<SmallItem> fullList = new LinkedList<>();

            for (String subString : StringMaster.openContainer(entity
                    .getProperty(G_PROPS.CLASSIFICATIONS))) {
                CLASSIFICATIONS classif = new EnumMaster<CLASSIFICATIONS>().retrieveEnumConst(
                        CLASSIFICATIONS.class, subString);
                if (classif != null) {
                    if (classif.isDisplayed()) {
                        fullList.add(new SmallItem(classif, classif.getImagePath(), classif
                                .getName(), classif.getToolTip()));
                    }
                }
            }

            for (String subString : StringMaster.openContainer(entity
                    .getProperty(G_PROPS.STANDARD_PASSIVES))) {
                STANDARD_PASSIVES std_pas = new EnumMaster<STANDARD_PASSIVES>().retrieveEnumConst(
                        STANDARD_PASSIVES.class, subString);
                if (std_pas != null) {
                    fullList.add(new SmallItem(std_pas, std_pas.getImagePath(), std_pas.getName(),
                            std_pas.getToolTip()));
                }
            }

            for (AbilityObj passive : entity.getPassives()) {
                if (passive == null) {
                    continue;
                }
                if (passive.getProperty(G_PROPS.ABILITY_GROUP, true).equalsIgnoreCase(
                        DISPLAYED_PASSIVES)) {
                    fullList.add(new SmallItem(passive));
                }
            }

            return splitList(fullList);

        }

        // any other cases? graveyard for cells!

        return null;
    }

    @Override
    public int getPanelWidth() {
        return arrowWidth + GuiManager.getSmallObjSize() / 2 * pageSize;
    }

    @Override
    public int getPanelHeight() {
        return GuiManager.getSmallObjSize() / 2 + 2;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
        super.refresh();

    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    public synchronized void setEntity(Entity entity) {
        this.entity = entity;
    }

}
