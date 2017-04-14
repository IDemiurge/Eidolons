package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.elements.Filter;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_QuickItemAction;
import main.entity.item.DC_QuickItemObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.ActionInput;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.logic.action.context.Context;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSource;
import main.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.*;
import java.util.stream.Collectors;

import static main.libgdx.texture.TextureCache.getOrCreateGrayscaleR;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class RadialManager {

    public static TextureRegion getTextureForActive(DC_ActiveObj obj, DC_Obj target) {
        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
        return getTextureForActive(obj, ref);
    }

    public static TextureRegion getTextureRForActive(DC_ActiveObj obj, DC_Obj target) {
        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
        return getTextureRForActive(obj, ref);
    }

    public static TextureRegion getTextureRForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
         getOrCreateGrayscaleR(obj.getImagePath())
         : getOrCreateR(obj.getImagePath());
    }

    public static TextureRegion getTextureForActive(DC_ActiveObj obj, Ref ref) {
        return !obj.canBeActivated(ref) ?
                getOrCreateGrayscaleR(obj.getImagePath())
                : getOrCreateR(obj.getImagePath());
    }

    private static boolean isActionShown(ActiveObj el, DC_Obj target) {
        if (!(el instanceof DC_ActiveObj)) return false;
        DC_ActiveObj action = ((DC_ActiveObj) el);
        if (target != action.getOwnerObj()) {
            if (action.getActionType() == ACTION_TYPE.MODE)
                return false;
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.TURN)
                return false;
        }
        if (action.getTargeting() == null) return false;

        if (!action.canBeTargeted(target.getId()))
            return false;
        return true;
    }

    public static List<RadialValueContainer> createNew(DC_Obj target) {
        List<RadialValueContainer> list = new LinkedList<>();
        if (target instanceof Unit)
            list.add(getExamineNode(target));

        Unit sourceUnit = (Unit) Game.game.getManager().getActiveObj();
        if (sourceUnit == null) {
            return list;
        }
        if (!sourceUnit.isMine())
            if (!sourceUnit.getGame().isDebugMode())
                return list;


        List<RadialValueContainer> moves = new ArrayList<>();
        List<RadialValueContainer> turns = new ArrayList<>();
        List<RadialValueContainer> attacks = new ArrayList<>();
        List<RadialValueContainer> specialActions = new ArrayList<>();
        List<RadialValueContainer> modes = new ArrayList<>();
        List<RadialValueContainer> quickItems = new ArrayList<>();
        List<ActiveObj> actives = sourceUnit.getActives();
        sourceUnit.getQuickItems().forEach(item -> {
            if (isQuickItemShown(item, target))
                actives.add(item.getActive());
        });
        actives.parallelStream()
         .filter(el -> isActionShown(el, target))
         .map(el -> (DC_ActiveObj) el)
         .distinct()
         .sequential()
         .forEach(el -> {
             if (el.isMove()) {
                 final RadialValueContainer valueContainer = configureMoveNode(target, el);
                 moves.add(valueContainer);
             } else if (el.isTurn()) {
                 final RadialValueContainer valueContainer = configureActionNode(target, el);
                 turns.add(valueContainer);
             } else if (el.isAttackGeneric()) {
                 attacks.add(configureAttackNode(target, el));
             } else {
                 final RadialValueContainer valueContainer = configureActionNode(target, el);

                 if (el instanceof DC_QuickItemAction)
                     quickItems.add(valueContainer);
                 else if (el.getActionType() == ACTION_TYPE.MODE)
                     modes.add(valueContainer);
                 else
                 {
                     if (!el.isAttackAny())
                         if ( el.getActionType()!=ACTION_TYPE.HIDDEN )
                             specialActions.add(valueContainer);
                 }

             }
         });


        if (!attacks.isEmpty())
        list.add(getAttackParentNode(RADIAL_PARENT_NODE.MAIN_HAND_ATTACKS, attacks.get(0)));
        list.add(getParentNode(RADIAL_PARENT_NODE.TURN_ACTIONS, turns));
        list.add(getParentNode(RADIAL_PARENT_NODE.MOVES, moves));
        if (attacks.size() > 1)
            list.add(getAttackParentNode(RADIAL_PARENT_NODE.OFFHAND_ATTACKS, attacks.get(1)));

        list.add(getParentNode(RADIAL_PARENT_NODE.QUICK_ITEMS, quickItems));
        list.add(getParentNode(RADIAL_PARENT_NODE.MODES, modes));


        list.add(getParentNode(RADIAL_PARENT_NODE.SPECIAL, specialActions));

        if (!sourceUnit.getSpells().isEmpty()) {
            final List<RadialValueContainer> spellNodes =
             SpellRadialManager.getSpellNodes(sourceUnit, target);
            list.add(getParentNode(RADIAL_PARENT_NODE.SPELLS, spellNodes));
        }
        list.removeIf(i -> i == null); // REMOVE IF NO NODES IN PARENT!
        return list;
    }

    private static boolean isQuickItemShown(DC_QuickItemObj item, DC_Obj target) {
        if (target != item.getOwnerObj()) {
            if (!(item.getActive().getTargeting() instanceof SelectiveTargeting))
                return false;
        }
        if (item.isAmmo())
            return false;
        return true;
    }

    private static RadialValueContainer getParentNode(RADIAL_PARENT_NODE type,
                                                      List<RadialValueContainer> containers) {
      if (containers.isEmpty() )
          return null ;
        RadialValueContainer valueContainer = new RadialValueContainer(
         getOrCreateR(type.getIconPath()),
         null);
//        getOrCreateGrayscaleR(
        valueContainer.setChilds(containers);
        addSimpleTooltip(valueContainer, type.getName());
        return (valueContainer);
    }

    private static RadialValueContainer getAttackParentNode(RADIAL_PARENT_NODE type,
                                                            RadialValueContainer valueContainer) {
        addSimpleTooltip(valueContainer, type.getName());
        return valueContainer;
    }

    private static RadialValueContainer getExamineNode(DC_Obj target) {

        final RadialValueContainer valueContainer = new RadialValueContainer(
         getOrCreateR("UI/components\\2017\\radial/examine.png"),
         () -> {
             GuiEventManager.trigger(
              GuiEventType.SHOW_UNIT_INFO_PANEL,
              new EventCallbackParam<>(new UnitDataSource(((Unit) target))));

         }
        );
        addSimpleTooltip(valueContainer, "Examine");
        return valueContainer;
    }


    public static void addTooltip(RADIAL_PARENT_NODE parent, RadialValueContainer el, DC_ActiveObj activeObj) {
        new ActionCostTooltip();

        new ActionCostSource(){

            @Override
            public ValueContainer getName() {
                return null;
            }

            @Override
            public List<ValueContainer> getCostsList() {
                return null;
            }
        };
    }
        public static void addSimpleTooltip(RadialValueContainer el, String name) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(name, "")));
        el.addListener(tooltip.getController());
    }

    private static Filter<Obj> getFilter(DC_ActiveObj active) {
        active.getRef().setMatch(active.getOwnerObj().getId()); // for filter
        Filter<Obj> filter = active.getTargeting().getFilter();
        filter.setRef(active.getRef());

        return filter;
    }

    private static RadialValueContainer configureActionNode(DC_Obj target, DC_ActiveObj el) {
        if (el.getTargeting() instanceof SelectiveTargeting)
            if (target == el.getOwnerObj())
                return configureSelectiveTargetedNode(el);

        RadialValueContainer valueContainer = new RadialValueContainer(
         new TextureRegion(getTextureForActive(el, target)),
         getRunnable(target, el)
        );
        addSimpleTooltip(valueContainer, el.getName());
        return valueContainer;
    }

    private static RadialValueContainer configureSelectiveTargetedNode(DC_ActiveObj active) {

        Set<Obj> objSet = CoreEngine.isActionTargetingFiltersOff() ?
         DC_Game.game.getUnits().parallelStream().distinct().collect(Collectors.toSet())
         : getFilter(active).getObjects();

        final boolean valid = objSet.size() > 0;

        return new RadialValueContainer(
         valid ?
          getOrCreateR(active.getImagePath()) :
          getOrCreateGrayscaleR(active.getImagePath()),
         () -> {
             if (valid) {
                 WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT, new ActionInput(active, new Context(active.getRef())));
             } else {
                 FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.ERROR, "", active);
             }

         });
    }

    private static RadialValueContainer configureAttackNode(DC_Obj target, DC_ActiveObj dcActiveObj) {
        List<RadialValueContainer> list = new ArrayList<>();

        for (DC_ActiveObj dc_activeObj : dcActiveObj.getSubActions()) {
            if (dcActiveObj.getRef().getSourceObj() == target) {
                final RadialValueContainer valueContainer =
                 configureSelectiveTargetedNode(dc_activeObj);
                addSimpleTooltip(valueContainer, dc_activeObj.getName());
                list.add(valueContainer);
            } else if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
                final RadialValueContainer valueContainer = new RadialValueContainer(
                 getOrCreateR(dc_activeObj.getImagePath()),
                 getRunnable(target, dcActiveObj)
//                () -> dc_activeObj.activateOn(target)
                );
                addSimpleTooltip(valueContainer, dc_activeObj.getName());
                list.add(valueContainer);
            }
        }

        DC_WeaponObj activeWeapon = dcActiveObj.getActiveWeapon();
        if (activeWeapon != null && activeWeapon.isRanged()) {
            if (dcActiveObj.getRef().getObj(Ref.KEYS.AMMO) == null) {
                for (DC_QuickItemObj ammo : dcActiveObj.getOwnerObj().getQuickItems()) {
                    final RadialValueContainer valueContainer = new RadialValueContainer(
                     getOrCreateR(ammo.getImagePath()),
                     getRunnable(target, ammo)
                    );
                    addSimpleTooltip(valueContainer, ammo.getName());
                    list.add(valueContainer);
                }
            }
        }

        RadialValueContainer valueContainer =
         new RadialValueContainer(
          new TextureRegion(getTextureForActive(dcActiveObj, target)), null
         );
        addSimpleTooltip(valueContainer, dcActiveObj.getName());
        valueContainer.setChilds(list);

        return valueContainer;
    }

    private static RadialValueContainer configureMoveNode(DC_Obj target,
                                                          DC_ActiveObj dcActiveObj) {
        RadialValueContainer result;

        if (target == dcActiveObj.getOwnerObj()) {
            result = configureSelectiveTargetedNode(dcActiveObj);
        } else {
            if (dcActiveObj.getTargeting() instanceof SelectiveTargeting) {
                result = new RadialValueContainer(
                 getOrCreateR(dcActiveObj.getImagePath()),
                 getRunnable(target, dcActiveObj)

                );
            } else {
                result = new RadialValueContainer(
                 new TextureRegion(getTextureForActive(dcActiveObj, target)),
                 getRunnable(target, dcActiveObj)
                );
            }
        }
        addSimpleTooltip(result, dcActiveObj.getName());

        return result;
    }


    private static Runnable getRunnable(DC_Obj target, Entity activeObj) {

        if (activeObj instanceof DC_ActiveObj) {
            DC_ActiveObj active = (DC_ActiveObj) activeObj;
            if (active.getTargeting() instanceof SelectiveTargeting)
                return () -> {
                    DC_Cell cell = target.getGame().getCellByCoordinate(target.getCoordinates());
                    if (active.getTargeter().canBeTargeted(cell.getId()))
                        active.activateOn(target);


                };
        }

        return () -> {
            activeObj.invokeClicked();


        };
    }

    public enum RADIAL_PARENT_NODE {
        OFFHAND_ATTACKS,
        TURN_ACTIONS("/UI/components\\2017\\radial\\turns.png"),
        SPELLS("/UI/components\\2017\\radial\\spells.png"),
        MOVES("UI\\components\\2017\\radial\\moves.png"),
        MAIN_HAND_ATTACKS,
        SPECIAL("UI\\components\\2017\\radial\\special actions.png"),
        QUICK_ITEMS("UI\\components\\2017\\radial\\restoration modes.png")
        , MODES("UI\\components\\2017\\radial\\additional actions.png"),
        ;

        private String iconPath;

        RADIAL_PARENT_NODE() {
        }

        RADIAL_PARENT_NODE(String iconPath) {
            this.iconPath = iconPath;
        }

        public String getIconPath() {
            return iconPath;
        }

        public String getName() {
            return StringMaster.getWellFormattedString(name());
        }
    }
}
