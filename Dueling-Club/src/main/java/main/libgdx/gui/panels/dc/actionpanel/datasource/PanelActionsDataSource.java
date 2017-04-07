package main.libgdx.gui.panels.dc.actionpanel.datasource;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource.getActionCostList;
import static main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource.getObjValueContainerMapper;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class PanelActionsDataSource implements
        ActiveQuickSlotsDataSource, UnitActionsDataSource, SpellDataSource,
        EffectsAndAbilitiesSource, ResourceSource {
    private Unit unit;

    public PanelActionsDataSource(Unit unit) {
        this.unit = unit;
    }

    private static InputListener getToolTipController(String name) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(name, "")));
        return tooltip.getController();
    }

    @Override
    public List<ActionValueContainer> getQuickSlotActions() {
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();

        List<ActionValueContainer> list = items.stream()
                .map((DC_QuickItemObj key) -> {
                    final ActionValueContainer valueContainer = new ActionValueContainer(
                            getOrCreateR(key.getImagePath()),
                            () -> {
                                key.invokeClicked();
                            }
                    );
                    valueContainer.addListener(getToolTipController(key.getName()));
                    return valueContainer;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < unit.getRemainingQuickSlots(); i++) {
            list.add(null);
        }

        return list;
    }

    @Override
    public List<ActionValueContainer> getDisplayedActions() {
        List<ActionValueContainer> list = new LinkedList<>();
        list.addAll(getModeActions());
        list.addAll(getSpecialActions());
        return list;
    }

    private List<ActionValueContainer> getSpecialActions() {
        return getActions(ACTION_TYPE.SPECIAL_ACTION);
    }

    public List<ActionValueContainer> getModeActions() {
        return getActions(ACTION_TYPE.MODE);
    }

    public List<ActionValueContainer> getActions(ACTION_TYPE type) {
        return unit.getActionMap().get(type).stream()
                .map(key -> {
                    final ActionValueContainer valueContainer = new ActionValueContainer(
                            getOrCreateR(key.getImagePath()),
                            key::invokeClicked
                    );
                    ActionCostTooltip tooltip = new ActionCostTooltip();
                    tooltip.setUserObject(new ActionCostSource() {

                        @Override
                        public ValueContainer getName() {
                            return new ValueContainer(key.getName(), "");
                        }

                        @Override
                        public List<ValueContainer> getCostsList() {
                            return getActionCostList(key);
                        }
                    });
                    valueContainer.addListener(tooltip.getController());
                    return valueContainer;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionValueContainer> getSpells() {

        return unit.getSpells().stream()
                .map(el -> {
                    final ActionValueContainer container = new ActionValueContainer(
                            getOrCreateR(el.getImagePath()),
                            () -> {
                                el.invokeClicked();
                            }
                    );
                    container.addListener(getToolTipController(el.getName()));

                    return container;
                }).collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getEffects() {
        return unit.getBuffs().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(getObjValueContainerMapper())
                .collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getAbilities() {
        return unit.getPassives().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(getObjValueContainerMapper())
                .collect(Collectors.toList());
    }

    @Override
    public String getToughness() {
        int c = unit.getIntParam(PARAMS.C_TOUGHNESS);
        int m = unit.getIntParam(PARAMS.TOUGHNESS);
        return c + "/" + m;
    }

    @Override
    public String getEndurance() {
        int c = unit.getIntParam(PARAMS.C_ENDURANCE);
        int m = unit.getIntParam(PARAMS.ENDURANCE);
        return c + "/" + m;
    }

    @Override
    public String getStamina() {
        int c = unit.getIntParam(PARAMS.C_STAMINA);
        int m = unit.getIntParam(PARAMS.STAMINA);
        return c + "/" + m;
    }

    @Override
    public String getMorale() {
        int c = unit.getIntParam(PARAMS.C_MORALE);
        int m = unit.getIntParam(PARAMS.MORALE);
        return c + "/" + m;
    }

    @Override
    public String getEssence() {
        int c = unit.getIntParam(PARAMS.C_ESSENCE);
        int m = unit.getIntParam(PARAMS.ESSENCE);
        return c + "/" + m;
    }

    @Override
    public String getFocus() {
        int c = unit.getIntParam(PARAMS.C_FOCUS);
        int m = unit.getIntParam(PARAMS.FOCUS);
        return c + "/" + m;
    }
}
