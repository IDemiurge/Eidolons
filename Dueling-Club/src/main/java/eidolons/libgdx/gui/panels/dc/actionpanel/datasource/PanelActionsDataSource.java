package eidolons.libgdx.gui.panels.dc.actionpanel.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActionManager.STD_SPEC_ACTIONS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.*;
import eidolons.libgdx.texture.TextureCache;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

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

    public static ActionValueContainer getActionValueContainer(DC_ActiveObj el, int size) {
        boolean valid = el.canBeManuallyActivated();
        final ActionValueContainer container = new ActionValueContainer(
         size,
         valid,
         TextureCache.getOrCreateSizedRegion(UiMaster.getIconSize(), getImage(el))
         ,
         el::invokeClicked
        );
        ActionCostTooltip tooltip = new ActionCostTooltip(el);
        tooltip.setUserObject(new ActionCostSourceImpl(el));

        tooltip.addTo(container);
        return container;
    }


    private static String getImage(DC_ActiveObj el) {
        String image = el.getImagePath();
//        if (el.can)
        return image;
    }

    @Override
    public String getParam(PARAMS param) {
        switch (param) {
            case STAMINA:
                return getStamina();
            case FOCUS:
                return getFocus();
            case TOUGHNESS:
                return getToughness();
            case ENDURANCE:
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
        if (items == null)
            return (List<ActionValueContainer>)
             ListMaster.fillWithNullElements(new ArrayList<ActionValueContainer>(), unit.getRemainingQuickSlots());
        List<ActionValueContainer> list = items.stream()
         .map((DC_QuickItemObj key) -> {
             boolean valid = key.getActive().canBeManuallyActivated();
             final ActionValueContainer valueContainer = new ActionValueContainer(
              UiMaster.getBottomQuickItemIconSize(),
              valid,
              getOrCreateR(key.getImagePath()),
              key::invokeClicked
             );
             ActionCostTooltip tooltip = new ActionCostTooltip(key.getActive());
             tooltip.setUserObject(new ActionCostSourceImpl(key.getActive()));
             valueContainer.addListener(tooltip.getController());
             return valueContainer;
         })
         .collect(Collectors.toList());
        ObjType type = DataManager.getType(StringMaster.getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.name()), DC_TYPE.ACTIONS);
        TextureRegion invTexture = TextureCache.getOrCreateR(type.getImagePath());

        DC_UnitAction action = unit.getAction(StringMaster.getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.name()));
        if (action == null)
            return list;
        boolean valid = action.canBeManuallyActivated();
        ActionValueContainer invButton = new ActionValueContainer(valid, invTexture, () -> {
            action.clicked();
        });
        list.add(invButton);
        for (int i = 0; i < unit.getRemainingQuickSlots() - 1; i++) {
            list.add(null);
        }

        return list;
    }

    @Override
    public List<ActionValueContainer> getDisplayedActions() {
        List<ActionValueContainer> list = new ArrayList<>();
        list.addAll(getActions(ACTION_TYPE.MODE));
        list.addAll(getActions(ACTION_TYPE.SPECIAL_ACTION));
        return list;
    }

    public List<ActionValueContainer> getActions(ACTION_TYPE type) {
        if (unit.getActionMap().get(type) == null) {
            return new ArrayList<>();
        }
        return unit.getActionMap().get(type).stream()
         .map(getActiveObjValueContainerFunction(UiMaster.getIconSize()))
         .collect(Collectors.toList());
    }

    @Override
    public List<ActionValueContainer> getSpells() {
        return unit.getSpells().stream()
         .map(getActiveObjValueContainerFunction(UiMaster.getBottomSpellIconSize()))
         .collect(Collectors.toList());
    }

    private Function<DC_ActiveObj, ActionValueContainer> getActiveObjValueContainerFunction(
     int size) {
        return el -> {
            return getActionValueContainer(el, size);
        };
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
