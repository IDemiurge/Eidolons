package main.libgdx.gui.panels.dc.actionpanel.datasource;

import com.badlogic.gdx.scenes.scene2d.InputListener;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.dialog.ValueTooltip;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource.getObjValueContainerMapper;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ActionPanelDataSource implements
        QuickSlotsDataSource, ActionModDataSource, SpellDataSource,
        EffectsAndAbilitiesSource {
    private Unit unit;

    public ActionPanelDataSource(Unit unit) {
        this.unit = unit;
    }

    @Override
    public List<ActionValueContainer> getQuickSlotActions() {
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();

        List<ActionValueContainer> list = items.stream()
                .map((DC_QuickItemObj key) -> getActionValueContainer(key))
                .collect(Collectors.toList());

        for (int i = 0; i < unit.getRemainingQuickSlots(); i++) {
            list.add(null);
        }

        return list;
    }

    private ActionValueContainer getActionValueContainer(DC_QuickItemObj key) {
        final ActionValueContainer valueContainer = new ActionValueContainer(
                getOrCreateR(key.getImagePath()),
                () -> {
                    key.activate();
                }
        );
        valueContainer.addListener(getToolTipController(key.getName()));
        return valueContainer;
    }

    @Override
    public List<ActionValueContainer> getActionMods() {
        return unit.getActionModeMap().keySet().stream()
                .map(this::getActionValueContainer)
                .collect(Collectors.toList());
    }

    private ActionValueContainer getActionValueContainer(DC_ActiveObj key) {
        final ActionValueContainer valueContainer = new ActionValueContainer(
                getOrCreateR(key.getImagePath()),
                () -> {
                    key.activate();
                }
        );
        valueContainer.addListener(getToolTipController(key.getName()));
        return valueContainer;
    }

    @Override
    public List<ActionValueContainer> getSpells() {

        return unit.getSpells().stream()
                .map(el -> {
                    final ActionValueContainer container = new ActionValueContainer(
                            getOrCreateR(el.getImagePath()),
                            () -> {
                                el.activate();
                            }
                    );

                    container.addListener(getToolTipController(el.getName()));

                    return container;
                }).collect(Collectors.toList());
    }

    private InputListener getToolTipController(String name) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(name, ""));
        return tooltip.getController();
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
}
