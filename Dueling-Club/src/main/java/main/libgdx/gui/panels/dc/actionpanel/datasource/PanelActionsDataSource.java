package main.libgdx.gui.panels.dc.actionpanel.datasource;

import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.libgdx.gui.panels.dc.unitinfo.datasource.*;
import main.system.datatypes.DequeImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class PanelActionsDataSource implements
        ActiveQuickSlotsDataSource, UnitActionsDataSource, SpellDataSource,
        EffectsAndAbilitiesSource, ResourceSource,
        MainWeaponDataSource<ActionValueContainer>, OffWeaponDataSource {
    private Unit unit;

    private UnitDataSource unitDataSource;

    public PanelActionsDataSource(Unit unit) {
        this.unit = unit;
        unitDataSource = new UnitDataSource(unit);
    }
    @Override
    public String getParam(PARAMS param) {
        switch (param){
            case STAMINA:
                return getStamina();
            case FOCUS:
                return getFocus();
            case TOUGHNESS:
                return getToughness();
            case  ENDURANCE :
                return getEndurance();
            case ESSENCE:
                return getEssence();
            case MORALE:
                return getMorale();
        }
        return null;
    }
    @Override
    public List<ActionValueContainer> getQuickSlotActions() {
        final DequeImpl<DC_QuickItemObj> items = unit.getQuickItems();

        List<ActionValueContainer> list = items.stream()
                .map((DC_QuickItemObj key) -> {
                    final ActionValueContainer valueContainer = new ActionValueContainer(
                            getOrCreateR(key.getImagePath()),
                            key::invokeClicked
                    );
                    ActionCostTooltip tooltip = new ActionCostTooltip(key.getActive());
                    tooltip.setUserObject(new ActionCostSourceImpl(key.getActive()) );
                    valueContainer.addListener(tooltip.getController());
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
        list.addAll(getActions(ACTION_TYPE.MODE));
        list.addAll(getActions(ACTION_TYPE.SPECIAL_ACTION));
        return list;
    }

    public List<ActionValueContainer> getActions(ACTION_TYPE type) {
        return unit.getActionMap().get(type).stream()
                .map(getActiveObjValueContainerFunction())
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionValueContainer> getSpells() {
        return unit.getSpells().stream()
                .map(getActiveObjValueContainerFunction())
                .collect(Collectors.toList());
    }

    private Function<DC_ActiveObj, ActionValueContainer> getActiveObjValueContainerFunction() {
        return el -> {
           return  getActionValueContainer(el);
        };
    }

    public static ActionValueContainer getActionValueContainer(DC_ActiveObj el) {
        final ActionValueContainer container = new ActionValueContainer(
         getOrCreateR(getImage(el)),
         el::invokeClicked
        );
        ActionCostTooltip tooltip = new ActionCostTooltip(el);
        tooltip.setUserObject(new ActionCostSourceImpl(el) );


        container.addListener(tooltip.getController());
        return container;
    }

    private static String getImage(DC_ActiveObj el) {
        String image = el.getImagePath();
//        if (el.can)
        return image;
    }

    @Override
    public List<ValueContainer> getBuffs() {
        return unitDataSource.getBuffs();
    }

    @Override
    public List<ValueContainer> getAbilities() {
        return unitDataSource.getAbilities();
    }

    @Override
    public String getToughness() {
        return unitDataSource.getToughness();
    }

    @Override
    public String getEndurance() {
        return unitDataSource.getEndurance();
    }

    @Override
    public String getStamina() {
        return unitDataSource.getStamina();
    }

    @Override
    public String getMorale() {
        return unitDataSource.getMorale();
    }

    @Override
    public String getEssence() {
        return unitDataSource.getEssence();
    }

    @Override
    public String getFocus() {
        return unitDataSource.getFocus();
    }

    @Override
    public ValueContainer getOffWeapon() {
        return unitDataSource.getOffWeapon();
    }

    @Override
    public List<ValueContainer> getOffWeaponDetailInfo() {
        return unitDataSource.getOffWeaponDetailInfo();
    }

    @Override
    public ValueContainer getNaturalOffWeapon() {
        return unitDataSource.getNaturalOffWeapon();
    }

    @Override
    public List<ValueContainer> getNaturalOffWeaponDetailInfo() {
        return unitDataSource.getNaturalOffWeaponDetailInfo();
    }

    @Override
    public ActionValueContainer getMainWeapon() {
        return null;
    }

    @Override
    public List<ActionValueContainer> getMainWeaponDetailInfo() {
        return null;
    }

    @Override
    public ActionValueContainer getNaturalMainWeapon() {
        return null;
    }

    @Override
    public List<ActionValueContainer> getNaturalMainWeaponDetailInfo() {
        return null;
    }
}
