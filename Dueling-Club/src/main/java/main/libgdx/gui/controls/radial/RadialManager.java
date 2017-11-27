package main.libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.active.*;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.core.ActionInput;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.logic.action.context.Context;
import main.game.module.dungeoncrawl.objects.DungeonObj;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSourceImpl;
import main.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.libgdx.gui.panels.dc.logpanel.text.TextPanel;
import main.libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.AttackTooltipFactory;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.*;
import java.util.function.Supplier;

import static main.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.GAME_FINISHED;

public class RadialManager {
    private static final ActiveObj SHORTCUT = new DummyAction();
    private static Map<DC_Obj, List<RadialValueContainer>> cache = new HashMap<>();
    private static boolean processingShortcuts;

    public static TextureRegion getTextureForActive(DC_ActiveObj obj, DC_Obj target) {
//        Ref ref = obj.getOwnerObj().getRef().getTargetingRef(target);
//        return !obj.canBeActivated(ref) ?
//         getOrCreateGrayscaleR(obj.getImagePath()):
        return getOrCreateR(obj.getImagePath());
    }

    private static boolean isActionShown(ActiveObj el, DC_Obj target) {
        if (el == SHORTCUT)
            return true;
        if (!(el instanceof DC_ActiveObj)) {
            return false;
        }
        if (((DC_ActiveObj) el).getGame().isDebugMode())
            return true;
        DC_ActiveObj action = ((DC_ActiveObj) el);

        if (action.getActionGroup() == ACTION_TYPE_GROUPS.DUNGEON)
            return true; //TODO [quick fix]

        if (target != action.getOwnerObj()) {
            if (action.getActionType() == ACTION_TYPE.MODE) {
                return false;
            }
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.TURN) {
                return processingShortcuts;
            }
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
                if (target.getX() - action.getOwnerObj().getX() > 2
                 || target.getY() - action.getOwnerObj().getY() > 2
                 ) {
                    return false;
                }
                if (action !=
                 DefaultActionHandler.getMoveToCellAction(action.getOwnerObj(),
                  target.getCoordinates()))
                    return false;
//
                if (target instanceof BattleFieldObject) {
                    target = target.getGame().getCellByCoordinate(target.getCoordinates());
                }
            }
        } else {
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.ATTACK) {
                return true;
            }
            if (target == action.getOwnerObj())
                if (action.getTargeting() instanceof SelectiveTargeting)
                    return true;
        }
        if (action.getTargeting() == null) {
            return false;
        }

        if (!action.canBeTargeted(target.getId(), false)) {
            return false;
        }
        return true;
    }

    public static void addCostTooltip(DC_ActiveObj el, ValueContainer valueContainer) {
        ActionCostTooltip tooltip = new ActionCostTooltip(el);
        tooltip.setUserObject(new ActionCostSourceImpl(el));
        valueContainer.addListener(tooltip.getController());
    }

    public static List<RadialValueContainer> getOrCreateRadialMenu(DC_Obj target) {
        List<RadialValueContainer> nodes = cache.get(target);
        if (!ListMaster.isNotEmpty(nodes))
            nodes = createNew(target);

        cache.put(target, nodes);
        return nodes;
    }

    public static List<RadialValueContainer> createNew(DC_Obj target) {
        if (OutcomePanel.TEST_MODE)
            try {
                GuiEventManager.trigger(GAME_FINISHED, DC_Game.game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (TextPanel.TEST_MODE)
            try {
//                GuiEventManager.trigger(SHOW_TEXT_CENTERED, "Hey\nyo\nman!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        List<RadialValueContainer> list = new ArrayList<>();
        if (target instanceof Unit) {
            list.add(getExamineNode(target));
        }

        Unit sourceUnit = (Unit) Game.game.getManager().getActiveObj();
        if (sourceUnit == null) {
            return list;
        }
        if (!sourceUnit.isMine()) {
            if (!sourceUnit.getGame().isDebugMode()) {
                return list;
            }
        }


        List<RadialValueContainer> moves = new ArrayList<>();
        List<RadialValueContainer> turns = new ArrayList<>();
        List<RadialValueContainer> attacks = new ArrayList<>();
        List<RadialValueContainer> offhandAttacks = new ArrayList<>();
        List<RadialValueContainer> specialActions = new ArrayList<>();
        List<RadialValueContainer> modes = new ArrayList<>();
        List<RadialValueContainer> orders = new ArrayList<>();
        List<RadialValueContainer> quickItems = new ArrayList<>();
        List<RadialValueContainer> dualAttacks = new ArrayList<>();
        List<DC_ActiveObj> topActions = new ArrayList<>();
        List<DC_ActiveObj> shortcuts = new ArrayList<>();
//top actions = last? recommended?
        List<? extends ActiveObj> actives = getActions(sourceUnit, target);
        processingShortcuts = false;
        for (ActiveObj action : actives) {
            if (!isActionShown(action, target))
                continue;

//        actives.parallelStream()
//         .filter(action -> isActionShown(action, target))
//         .distinct()
//         .sequential()
//         .forEach(action -> {
            if (action == SHORTCUT) {
                processingShortcuts = true;
                continue;
            }
            DC_ActiveObj el = (DC_ActiveObj) action;
            if (processingShortcuts)
                shortcuts.add(el);
            else if (el.getActionGroup() == ACTION_TYPE_GROUPS.DUNGEON) {
                topActions.add(el);
            } else if (el.isMove()) {
                final RadialValueContainer valueContainer = configureMoveNode(target, el);
                addCostTooltip(el, valueContainer);
                moves.add(valueContainer);
            } else if (el.isTurn()) {
                final RadialValueContainer valueContainer = configureActionNode(target, el);
                addCostTooltip(el, valueContainer);
                turns.add(valueContainer);
            } else if (el.getChecker().isDualAttack()) {
                dualAttacks.add(getAttackActionNode(el, target));
            } else if (el.isStandardAttack()
             || (
             el.getActionGroup() == ACTION_TYPE_GROUPS.SPECIAL &&
              el.getActionType() == ACTION_TYPE.SPECIAL_ATTACK)) {
                if (el.isOffhand())
                    offhandAttacks.add(getAttackActionNode(el, target));
                else
                    attacks.add(getAttackActionNode(el, target));
            }
//             if (el.getActionType()==ACTION_TYPE.SPECIAL_ATTACK){
//                 attacks.add(getAttackActionNode(el, target));
//                 offhandAttacks.add(getAttackActionNode(el, target));
//             }
            else {
                final RadialValueContainer valueContainer = configureActionNode(target, el);
//                 if (el.isSpell()) { DONE VIA SpellRadialManager
//                     spells.add(valueContainer);
//                 } else
                addCostTooltip(el, valueContainer);
                if (el instanceof DC_QuickItemAction) {
                    quickItems.add(valueContainer);
                } else if (el.getActionGroup() == ACTION_TYPE_GROUPS.ORDER) {
                    orders.add(valueContainer);
                } else if (el.getActionType() == ACTION_TYPE.MODE) {
                    modes.add(valueContainer);
                } else {
                    if (!el.isAttackAny()) {
                        if (el.getActionType() != ACTION_TYPE.HIDDEN) {
                            specialActions.add(valueContainer);
                        }
                    }
                }

            }
        }


        if (!attacks.isEmpty()) {
            list.add(configureAttackParentNode(attacks,
             RADIAL_PARENT_NODE.MAIN_HAND_ATTACKS, target, sourceUnit.getAttackAction(false)));
        }
        list.add(getParentNode(RADIAL_PARENT_NODE.TURN_ACTIONS, turns));
        list.add(getParentNode(RADIAL_PARENT_NODE.MOVES, moves));

        if (!offhandAttacks.isEmpty()) {
            list.add(configureAttackParentNode(offhandAttacks,
             RADIAL_PARENT_NODE.OFFHAND_ATTACKS, target, sourceUnit.getAttackAction(true)));
        }

        list.add(getParentNode(RADIAL_PARENT_NODE.QUICK_ITEMS, quickItems));
        list.add(getParentNode(RADIAL_PARENT_NODE.MODES, modes));
        list.add(getParentNode(RADIAL_PARENT_NODE.ORDERS, orders));
        list.add(getParentNode(RADIAL_PARENT_NODE.DUAL_ATTACKS, dualAttacks));


        list.add(getParentNode(RADIAL_PARENT_NODE.SPECIAL, specialActions));

        if (!sourceUnit.getSpells().isEmpty()) {
            final List<RadialValueContainer> spellNodes =
             SpellRadialManager.getSpellNodes(sourceUnit, target);
            list.add(getParentNode(RADIAL_PARENT_NODE.SPELLS, spellNodes));
        }


        topActions.forEach(activeObj -> {
            list.add(configureActionNode(target, activeObj));
        });
        shortcuts.forEach(activeObj -> {
            list.add(configureShortcutActionNode(target, activeObj));
        });


        list.removeIf(i -> i == null); // REMOVE IF NO NODES IN PARENT!
        return list;
    }

    private static RadialValueContainer configureShortcutActionNode(DC_Obj target, DC_ActiveObj activeObj) {
        if (activeObj.isMove()) {
            if (target instanceof BattleFieldObject) {
                target = target.getGame().getCellByCoordinate(target.getCoordinates());
            }
        } else if (activeObj.isTurn()) {
            target = activeObj.getOwnerObj();
        }
        RadialValueContainer node = configureActionNode(target, activeObj);
        if (activeObj.isAttackAny()) {
            addAttackTooltip(node, activeObj, target);
        }
        return node;
    }

    private static List<? extends ActiveObj> getActions(Unit sourceUnit, DC_Obj target) {

        List<ActiveObj> actives = new ArrayList<>(sourceUnit.getActives());
//        actives.addAll(sourceUnit.getSpells());
        if (sourceUnit.getQuickItems() != null)
            sourceUnit.getQuickItems().forEach(item -> {
                if (isQuickItemShown(item, target)) {
                    actives.add(item.getActive());
                }
            });
        if (target instanceof DungeonObj) {
            (((DungeonObj) target).getDM().
             getActions((DungeonObj) target, sourceUnit)).forEach(a -> {
                actives.add(0, (ActiveObj) a);
            });
//            if (DoorMaster.isDoor((BattleFieldObject) target)) {
//                actives.addAll(DoorMaster.getActions((BattleFieldObject) target, sourceUnit));
//            }
        }
        actives.add(SHORTCUT);
        actives.addAll(getShortcuts(sourceUnit, target));
        return actives;
    }

    private static Collection<? extends ActiveObj> getShortcuts(Unit sourceUnit,
                                                                DC_Obj target) {

        List<ActiveObj> list = new ArrayList<>();
        for (RADIAL_ACTION_SHORTCUT sub : RADIAL_ACTION_SHORTCUT.values()) {
            if (!checkShortcut(sub, sourceUnit, target)) {
                continue;
            }
            DC_ActiveObj action = getShortcut(sub, sourceUnit, target);
            list.add(action);
        }
        list.removeIf(a -> a == null);
        return list;
    }

    private static boolean checkShortcut(RADIAL_ACTION_SHORTCUT sub, Unit sourceUnit, DC_Obj target) {
        switch (sub) {
            case ATTACK:
                return target instanceof BattleFieldObject;
        }
        return true;
    }

    private static DC_ActiveObj getShortcut(RADIAL_ACTION_SHORTCUT sub,
                                            Unit sourceUnit, DC_Obj target) {
        switch (sub) {
            case TURN_TO:
                return DefaultActionHandler.getTurnToAction(sourceUnit, target.getCoordinates());
            case MOVE_TO:
                return DefaultActionHandler.getMoveToCellAction(sourceUnit, target.getCoordinates());
            case ATTACK:
                return DefaultActionHandler.getPreferredAttackAction(
                 sourceUnit, (BattleFieldObject) target);
        }
        return null;
    }

    private static boolean isQuickItemShown(DC_QuickItemObj item, DC_Obj target) {
        if (target != item.getOwnerObj()) {
            if (!(item.getActive().getTargeting() instanceof SelectiveTargeting)) {
                return false;
            }
        }
        if (item.isAmmo()) {
            return false;
        }
        return true;
    }

    private static RadialValueContainer getParentNode(RADIAL_PARENT_NODE type,
                                                      List<RadialValueContainer> containers) {
        if (containers.isEmpty()) {
            return null;
        }
        RadialValueContainer valueContainer = new RadialValueContainer(getOrCreateR(type.getIconPath()), null);
//        getOrCreateGrayscaleR(
        valueContainer.setChildNodes(containers);
        addSimpleTooltip(valueContainer, type.getName());
        return (valueContainer);
    }

    private static RadialValueContainer getAttackParentNode(RADIAL_PARENT_NODE type,
                                                            RadialValueContainer valueContainer) {
        addSimpleTooltip(valueContainer, type.getName());
        return valueContainer;
    }

    private static RadialValueContainer getExamineNode(DC_Obj target) {

        Runnable runnable = () -> {
            GuiEventManager.trigger(
             GuiEventType.SHOW_UNIT_INFO_PANEL,
             new UnitDataSource(((Unit) target)));

        };
        final RadialValueContainer valueContainer = new RadialValueContainer(getOrCreateR("UI/components\\2017\\radial/examine.png"), runnable);
        addSimpleTooltip(valueContainer, "Examine");
        return valueContainer;
    }

    public static void addSimpleTooltip(RadialValueContainer el, String name) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(name, "")));
        el.clearListeners();
        el.addListener(tooltip.getController());
    }

    private static void addAttackTooltip(RadialValueContainer valueContainer,
                                         DC_ActiveObj activeObj,
                                         DC_Obj target) {
        valueContainer.setTooltipSupplier(() -> AttackTooltipFactory.createAttackTooltip((DC_UnitAction)
         activeObj, target));


    }

    protected static RadialValueContainer configureActionNode(DC_Obj target, DC_ActiveObj el) {
        if (el.getTargeting() instanceof SelectiveTargeting) {
            return configureSelectiveTargetedNode(el, target);
        }
        RadialValueContainer valueContainer = new RadialValueContainer(
         new TextureRegion(getTextureForActive(el, target)), getRunnable(target, el), checkValid(el, target), el, target);
        addSimpleTooltip(valueContainer, el.getName());
        return valueContainer;
    }

    private static boolean checkValid(DC_ActiveObj activeObj, DC_Obj target) {
        Ref ref = activeObj.getOwnerObj().getRef().getTargetingRef(target);
        return activeObj.canBeActivated(ref);
    }

    private static RadialValueContainer
    configureSelectiveTargetedNode(DC_ActiveObj active) {
        return configureSelectiveTargetedNode(active, null);
    }

    private static RadialValueContainer configureSelectiveTargetedNode(
     DC_ActiveObj active, DC_Obj target) {
        boolean wasValid;
//        if (target == null|| target.equals(active.getOwnerObj())){
//            Set<Obj> objSet = CoreEngine.isActionTargetingFiltersOff() ?
//             DC_Game.game.getUnits().parallelStream().distinct().collect(Collectors.toSet())
//             : getFilter(active).getObjects();
//            wasValid = objSet.size() > 0 &&
//             active.canBeManuallyActivated();
//        } else
        wasValid = active.canBeManuallyActivated();
        final boolean valid = wasValid;

        TextureRegion textureRegion =
         getOrCreateR(active.getImagePath());
//         valid ? now via Shader!
//         getOrCreateR(active.getImagePath()) :
//         getOrCreateGrayscaleR(active.getImagePath());
        Runnable runnable = () -> {
            if (valid) {
                Context context = new Context(active.getOwnerObj().getRef());
                if (target != null)
                    if (!target.equals(active.getOwnerObj())) {
                        context.setTarget(target.getId());
                    }
                Eidolons.getGame().getGameLoop().actionInput(
                 new ActionInput(active, context));
            } else {
                FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.CANNOT_ACTIVATE, "", active);
            }

        };
        return new RadialValueContainer(textureRegion, runnable, valid, active, target);
    }

    private static RadialValueContainer configureAttackParentNode(

     List<RadialValueContainer> list, RADIAL_PARENT_NODE parentNode, DC_Obj target, DC_ActiveObj parent) {
        if (parent.getActiveWeapon().isRanged()) {
            if (parent.getRef().getObj(Ref.KEYS.AMMO) == null) {
                for (DC_QuickItemObj ammo : parent.getOwnerObj().getQuickItems()) {
                    final RadialValueContainer valueContainer = new RadialValueContainer(getOrCreateR(ammo.getImagePath()), getRunnable(target, ammo.getActive()));
                    addSimpleTooltip(valueContainer, ammo.getName());
                    list.add(valueContainer);
                }
            }
        }

        RadialValueContainer valueContainer =
         new RadialValueContainer(new TextureRegion(getTextureForActive(parent, target)), null,
          checkValid(parent, target), parent, target);
        addSimpleTooltip(valueContainer, parentNode.getName());
        valueContainer.setChildNodes(list);

        return valueContainer;
    }

    private static RadialValueContainer getAttackActionNode(DC_ActiveObj activeObj, DC_Obj target) {
//        if (activeObj.getOwnerObj() == target) {
        final RadialValueContainer valueContainer =
         configureSelectiveTargetedNode(activeObj, target);
//            addAttackTooltip(valueContainer, activeObj, target);
//            return (valueContainer);
//        } else if (activeObj.getTargeting() instanceof SelectiveTargeting) {
//            final RadialValueContainer valueContainer =
//             new RadialValueContainer(getOrCreateR(activeObj.getImagePath()),
//              getRunnable(target, activeObj));
        addAttackTooltip(valueContainer, activeObj, target);
        return (valueContainer);
//        }
//        return null;
    }

    private static RadialValueContainer configureMoveNode(DC_Obj target,
                                                          DC_ActiveObj activeObj) {
        RadialValueContainer result;

        if (target == activeObj.getOwnerObj()) {
            result = configureSelectiveTargetedNode(activeObj);
        } else {
            if (activeObj.getTargeting() instanceof SelectiveTargeting) {
                result = new RadialValueContainer(getOrCreateR(activeObj.getImagePath()), getRunnable(target, activeObj));
            } else {
                result = new RadialValueContainer(new TextureRegion(
                 getTextureForActive(activeObj, target)), getRunnable(target, activeObj), checkValid(activeObj, target), activeObj, target);
            }
        }
        addSimpleTooltip(result, activeObj.getName());

        return result;
    }

    protected static Runnable getRunnable(DC_Obj target, DC_ActiveObj activeObj) {
//        Runnable runnable=        runnableCaches.get(target).get(activeObj);

        if (activeObj instanceof DC_ActiveObj) {
            DC_ActiveObj active = (DC_ActiveObj) activeObj;
            if (active.getTargeting() instanceof SelectiveTargeting) {
                return () -> {
                    if (active.isMove()) {
                        DC_Cell cell = target.getGame().getCellByCoordinate(target.getCoordinates());
                        if (!active.getTargeter().canBeTargeted(cell.getId()))
                            return;
                    }
                    active.activateOn(target);


                };
            }
        }
        if (target == activeObj.getOwnerObj())
            return () -> {
                activeObj.invokeClicked();
            };
        return () -> {
            activeObj.activateOn(target);
        };


    }

    public static void clearCache() {
        cache.clear();
    }

    public static Supplier<String> getInfoTextSupplier(boolean valid, DC_ActiveObj activeObj, DC_Obj target) {
        if (!valid) {
            return () -> activeObj.getCosts().getReasonsString();
        }
        if (!activeObj.isAttackGeneric())
            if (activeObj.isAttackAny()) {
                int chance = activeObj.getCalculator().getCritOrDodgeChance(target);
                if (chance == 0)
                    return null;
                return () -> {
                    String string = null;
                    if (chance > 0) {
                        string = chance + "% to crit";
                    } else
                        string = chance + "% to miss";
                    return string;
                };
            }
        if (activeObj.isSpell()) {

        }
        return null;
    }

    public Supplier<List<RadialValueContainer>> get(RADIAL_PARENT_NODE type,
                                                    DC_ActiveObj activeObj,
                                                    DC_Obj target) {
        return () -> {
            return getChildNodes(type, activeObj, target);
        };
    }

    private List<RadialValueContainer> getChildNodes(RADIAL_PARENT_NODE type, DC_ActiveObj activeObj, DC_Obj target) {
        List<RadialValueContainer> list = new ArrayList<>();
        return list;
    }

    public enum RADIAL_ACTION_SHORTCUT {
        TURN_TO,
        MOVE_TO,
        ATTACK,

    }

    public enum RADIAL_PARENT_NODE {
        OFFHAND_ATTACKS,
        TURN_ACTIONS("/UI/components\\2017\\radial\\turns.png"),
        SPELLS("/UI/components\\2017\\radial\\spells.png"),
        MOVES("UI\\components\\2017\\radial\\moves.png"),
        MAIN_HAND_ATTACKS,
        SPECIAL("UI\\components\\2017\\radial\\special actions.png"),
        QUICK_ITEMS("UI\\components\\2017\\radial\\restoration modes.png"), MODES("UI\\components\\2017\\radial\\additional actions.png"),
        ORDERS("UI\\components\\2017\\radial\\orders.png"),
        DUAL_ATTACKS("UI\\components\\2017\\radial\\DUAL_ATTACKS.png");

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
