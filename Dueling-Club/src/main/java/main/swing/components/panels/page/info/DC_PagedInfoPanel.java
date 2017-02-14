package main.swing.components.panels.page.info;

import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.content.PROPS;
import main.content.VALUE;
import main.content.ValuePageManager;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_PagedInfoPanel extends G_PagePanel<VALUE> {
    /**
     * Controls Page list for every OBJ_TYPE! How is the data to be managed?
     */
    private static final int VERSION = 3;
    private static final String ATTRIBUTES = StringMaster.getWellFormattedString("ATTRIBUTES");
    private static final String MASTERIES = StringMaster.getWellFormattedString("MASTERIES");
    private static final String RESISTANCES = StringMaster.getWellFormattedString("RESISTANCES");
    private static final String PARAMETERS = StringMaster.getWellFormattedString("PARAMETERS");
    protected Entity entity;
    private Map<Entity, SpellUpgradePage> supCache;

    public DC_PagedInfoPanel(Entity entity) {
        super(0, false, VERSION);
        this.setEntity(entity);
    }

    public DC_PagedInfoPanel() {
        super(0, false, VERSION);
    }

    protected void addComponents() {

        String pos = "pos 0 0";
        add(getCurrentComponent(), pos);
        addControls();
        int i = 0;
        setComponentZOrder(forwardButton, i);
        i++;
        setComponentZOrder(backButton, i);
        i++;
        setComponentZOrder(getCurrentComponent(), i);

        if (isAddNameLabel()) {
        }
    }

    private boolean isAddNameLabel() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected List<List<VALUE>> getPageData() {
        if (getEntity() == null) {
            return null;
        }

        List<List<VALUE>> values = null;
        if (getEntity().getGame().isSimulation()) {
            values = ValuePageManager.getValuesForHCInfoPages(getEntity().getOBJ_TYPE_ENUM());
        }

        if (values == null) {
            values = ValuePageManager.getValuesForDCInfoPages(getEntity().getOBJ_TYPE_ENUM());
        }
        if (values == null) {
            values = ValuePageManager.getGenericValuesForInfoPages();
        }
        if (!getEntity().getGame().isSimulation()) {
            if (getUnit() != null) {
                if (!getUnit().getOwner().isMe()) {
                    if (getUnit().getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT) {
                        values.set(0, ValuePageManager.getOutlineValues());
                    }
                }
            }
        }

        return values;
    }

    @Override
    protected G_Component createPageComponent(List<VALUE> list) {
        // TODO perhaps not all values should be made known?
        if (list.get(0) == PROPS.SPELL_UPGRADES) {
            SpellUpgradePage sup = getSupCache().get(entity);
            if (sup == null) {
                sup = new SpellUpgradePage(entity);
                supCache.put(entity, sup);
            }
            return sup;
        }
        if (list.get(0) == PROPS.WEAPON_ATTACKS) {
            // TODO
        }
        boolean property = list.get(0) instanceof PROPERTY;
        String header = null;
        try {
            header = getPageHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (property) {
            return new PropertyPage(header, list, entity);
        } else {
            // if (getParamPageHeader(list.getOrCreate(0))!=null )
            // header = getParamPageHeader(list.getOrCreate(0));
            return new ParameterPage(header, list, entity);
        }
    }

    private String getPageHeader() {
        String[] array = ValuePages.PAGE_NAMES;
        if (!getEntity().getGame().isSimulation()) {
            array = ValuePages.ALT_PAGE_NAMES;
        } else if (Launcher.getView() == VIEWS.HC) {
            array = ValuePages.ALT_PAGE_NAMES;
        }
        if (array.length <= entity.getOBJ_TYPE_ENUM().getCode()) {
            return null;
        }
        String[] pages = array[entity.getOBJ_TYPE_ENUM().getCode()].split(";");
        if (pages.length <= createPageIndex) {
            return null;
        }
        return StringMaster.getWellFormattedString(pages[createPageIndex]);
    }

    public Map<Entity, SpellUpgradePage> getSupCache() {
        if (supCache == null) {
            supCache = new HashMap<>();
        }
        return supCache;
    }

    private String getParamPageHeader(VALUE value) {
        if (value instanceof PARAMETER) {
            if (((PARAMETER) value).isAttribute()) {
                return ATTRIBUTES;
            }
            if (((PARAMETER) value).isMastery()) {
                return MASTERIES;
            }
            if (value.toString().contains("Resistance")) {
                return RESISTANCES;
            }
            if (value.toString().equalsIgnoreCase("Armor")) {
                return "Armor Values";
            }
            return
                    // entity.getOBJ_TYPE_ENUM().getFullName()+ " " +
                    PARAMETERS;
        }
        return null;
    }

    @Override
    protected G_Component createEmptyPageComponent() {
        return null;
    }

    @Override
    public void refresh() {
        super.refresh();
        // TODO if TYPE changed, reset pages!
        if (getCurrentComponent() != null) {
            ((InfoPage) getCurrentComponent()).setEntity(getEntity());
            getCurrentComponent().refresh();
        }
        // }
        // if not null
        // addBackButton();

    }

    protected boolean isReinitOnRefresh() {
        return true;
    }

    protected boolean isRevalidateOnRefresh() {
        return true;
    }

    @Override
    public int getPanelWidth() {

        return VISUALS.INFO_PANEL.getImage().getWidth(null);
    }

    @Override
    public int getPanelHeight() {
        return VISUALS.INFO_PANEL.getImage().getHeight(null);
    }

    public DC_HeroObj getUnit() {
        if (getEntity() instanceof DC_HeroObj) {
            return (DC_HeroObj) getEntity();
        }
        return null;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        if (this.entity != null && entity != null) {
            if (entity.getOBJ_TYPE_ENUM() != this.entity.getOBJ_TYPE_ENUM()) {
                setDirty(true);
            }
        }
        this.entity = entity;
    }

    public void select(Entity entity) {
        setEntity(entity);
        refresh();
    }

    protected int getArrowOffsetY2() {
        return 0;
    }

    protected int getArrowOffsetY() {
        return 0;
    }

    protected int getArrowOffsetX2() {
        return -2 * forwardButton.getImageWidth();
    }

    protected int getArrowOffsetX() {
        return 0;
        // forwardButton.getImageWidth();
    }

}
