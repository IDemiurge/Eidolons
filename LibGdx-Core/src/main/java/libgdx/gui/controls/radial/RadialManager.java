package libgdx.gui.controls.radial;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.active.*;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj;
import libgdx.GdxImageMaster;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import libgdx.gui.panels.dc.menus.outcome.OutcomePanel;
import libgdx.gui.panels.dc.unitinfo.neo.UnitInfoPanelNew;
import libgdx.gui.panels.dc.unitinfo.tooltips.AttackTooltipFactory;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.texture.TextureCache;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.game.core.game.Game;
import main.game.logic.action.context.Context;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.Flags;

import java.util.*;
import java.util.function.Supplier;

import static libgdx.texture.TextureCache.getOrCreateR;
import static libgdx.texture.TextureCache.getOrCreateRoundedRegion;
import static main.system.GuiEventType.GAME_FINISHED;

public class RadialManager {
    protected static final ActiveObj SHORTCUT = new DummyAction();
    protected static ObjectMap<DC_Obj, List<RadialContainer>> cache = new ObjectMap<>();
    protected static boolean processingShortcuts;

    public static TextureRegion getTextureForActive(DC_ActiveObj obj) {
        return getTextureForActive(obj, null);
    }

    public static TextureRegion getTextureForActive(DC_ActiveObj obj, DC_Obj target) {
        if (obj.isAttackAny()) {
            DC_WeaponObj weapon = obj.getActiveWeapon();
            String path = GdxImageMaster.getAttackActionPath(obj, weapon);
            TextureRegion texture = TextureCache.getOrCreateR(path);
            if (texture.getRegionWidth() > 64) {
                for (DC_UnitAction attackAction : weapon.getAttackActions()) {
                    TextureRegion t = TextureCache.getOrCreateR(GdxImageMaster.getAttackActionPath(attackAction, weapon));
                    if (texture.getRegionWidth() > 64)
                        continue;
                    texture = t;
                    break;
                }
            }
            return texture;
        }

        return TextureCache.getOrCreateRoundedRegion(obj.getImagePath());
    }

    protected static boolean isActionShown(ActiveObj el, DC_Obj target) {
        if (el == SHORTCUT)
            return true;
        if (!(el instanceof DC_ActiveObj)) {
            return false;
        }
        if (el.getName().equalsIgnoreCase("Use Inventory")) {
            return false;
        }
        if (((DC_ActiveObj) el).getGame().isDebugMode())
            return true;
        DC_ActiveObj action = ((DC_ActiveObj) el);

        if (action.getActionGroup() == ACTION_TYPE_GROUPS.DUNGEON)
            return true; //TODO [quick fix]

        if (target != action.getOwnerUnit()) {
            if (action.getActionType() == ACTION_TYPE.MODE) {
                return false;
            }
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.TURN) {
                return processingShortcuts;
            }
            if (action.getActionGroup() == ACTION_TYPE_GROUPS.MOVE) {
                if (target.getX() - action.getOwnerUnit().getX() > 2
                        || target.getY() - action.getOwnerUnit().getY() > 2
                ) {
                    return false;
                }
                if (action !=
                        DefaultActionHandler.getMoveToCellAction(action.getOwnerUnit(),
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
            if (target == action.getOwnerUnit())
                if (action.getTargeting() instanceof SelectiveTargeting)
                    return true;
        }
        if (action.getTargeting() == null) {
            return false;
        }
        if (target == null)
            return true;
        return action.canBeTargeted(target.getId(), false);
    }

    public static void addCostTooltip(DC_ActiveObj el, ValueContainer valueContainer) {
        ActionCostTooltip tooltip = new ActionCostTooltip(el);
        tooltip.setRadial(true);
        valueContainer.addListener(tooltip.getController());
    }

    public static List<RadialContainer> getOrCreateRadialMenu(DC_Obj target) {
        List<RadialContainer> nodes = cache.get(target);
        if (!ListMaster.isNotEmpty(nodes))
            nodes = createNodes(target);

        cache.put(target, nodes);
        return nodes;
    }

    public static List<RadialContainer> createNodes(DC_Obj target) {
        if (Flags.isIDE())
            if (OutcomePanel.TEST_MODE)
                try {
                    GuiEventManager.trigger(GAME_FINISHED, DC_Game.game);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }

        Unit sourceUnit = (Unit) Game.game.getManager().getActiveObj();
        if (sourceUnit == null) {
            return new ArrayList<>();
        }
        if (!sourceUnit.isMine()) {
            if (!sourceUnit.getGame().isDebugMode()) {
                return new ArrayList<>();
            }
        }
        List<? extends ActiveObj> actions = getActions(sourceUnit, target);
        return createNodes(sourceUnit, target, actions, true);
    }

    public static List<RadialContainer> createNodes(
            Unit sourceUnit, DC_Obj target, List<? extends ActiveObj> actives,
            boolean groupedAttacks
    ) {

        List<RadialContainer> list = new ArrayList<>();

        if (checkExamineNode(target))
            list.add(getExamineNode(target));


        List<RadialContainer> moves = new ArrayList<>();
        List<RadialContainer> turns = new ArrayList<>();
        List<RadialContainer> attacks = new ArrayList<>();
        List<RadialContainer> offhandAttacks = new ArrayList<>();
        List<RadialContainer> specialActions = new ArrayList<>();
        List<RadialContainer> modes = new ArrayList<>();
        List<RadialContainer> orders = new ArrayList<>();
        List<RadialContainer> quickItems = new ArrayList<>();
        List<RadialContainer> dualAttacks = new ArrayList<>();
        List<DC_ActiveObj> topActions = new ArrayList<>();
        List<DC_ActiveObj> shortcuts = new ArrayList<>();
//top actions = last? recommended?
        processingShortcuts = false;
        for (ActiveObj action : actives) {
            if (!isActionShown(action, target))
                continue;

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
                final RadialContainer valueContainer = configureMoveNode(target, el);
                addCostTooltip(el, valueContainer);
                moves.add(valueContainer);
            } else if (el.isTurn()) {
                final RadialContainer valueContainer = configureActionNode(target, el);
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
                final RadialContainer valueContainer = configureActionNode(target, el);
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
                    // if (!el.isAttackAny())  //TODO special attacks separately?
                    {
                        if (el.getActionType() != ACTION_TYPE.HIDDEN) {
                            specialActions.add(valueContainer);
                        }
                    }
                }

            }
        }


        if (!attacks.isEmpty()) {
            if (groupedAttacks)
                list.add(configureAttackParentNode(attacks,
                        RADIAL_PARENT_NODE.MAIN_HAND_ATTACKS, target, sourceUnit.getAttackAction(false)));
            else {
                list.addAll(attacks);
            }
        }
        list.add(getParentNode(RADIAL_PARENT_NODE.TURN_ACTIONS, turns));
        list.add(getParentNode(RADIAL_PARENT_NODE.MOVES, moves));

        if (!offhandAttacks.isEmpty()) {
            if (groupedAttacks)
                list.add(configureAttackParentNode(offhandAttacks,
                        RADIAL_PARENT_NODE.OFFHAND_ATTACKS, target, sourceUnit.getAttackAction(true)));
            else {
                list.addAll(offhandAttacks);
            }
        }

        list.add(getParentNode(RADIAL_PARENT_NODE.QUICK_ITEMS, quickItems));
        list.add(getParentNode(RADIAL_PARENT_NODE.MODES, modes));
        list.add(getParentNode(RADIAL_PARENT_NODE.ORDERS, orders));
        list.add(getParentNode(RADIAL_PARENT_NODE.DUAL_ATTACKS, dualAttacks));

        list.add(getParentNode(RADIAL_PARENT_NODE.SPECIAL, specialActions));

        if (target != null) //TODO REFACTOR
            if (!sourceUnit.getSpells().isEmpty()) {
                final List<RadialContainer> spellNodes =
                        SpellRadialManager.getSpellNodes(sourceUnit, target);
                list.add(getParentNode(RADIAL_PARENT_NODE.SPELLS, spellNodes));
            }

        topActions.forEach(activeObj -> {
            list.add(configureActionNode(target, activeObj));
        });
        shortcuts.forEach(activeObj -> {
            list.add(configureShortcutActionNode(target, activeObj));
        });


        list.removeIf(Objects::isNull); // REMOVE IF NO NODES IN PARENT!
        return list;
    }


    protected static boolean checkExamineNode(DC_Obj target) {
        if (!UnitInfoPanelNew.EXAMINE_READY && !Flags.isIDE()) {
            return false;
        }
        return target instanceof Unit && (
                target.getOutlineTypeForPlayer() == null
                        && VisionHelper.checkKnown(target));
    }

    protected static RadialContainer configureShortcutActionNode(DC_Obj target, DC_ActiveObj activeObj) {
        if (activeObj.isMove()) {
            if (target instanceof BattleFieldObject) {
                target = target.getGame().getCellByCoordinate(target.getCoordinates());
            }
        } else if (activeObj.isTurn()) {
            target = activeObj.getOwnerUnit();
        }
        RadialContainer node = configureActionNode(target, activeObj);
        if (activeObj.isAttackAny()) {
            addAttackTooltip(node, activeObj, target);
        }
        return node;
    }

    protected static List<? extends ActiveObj> getActions(Unit sourceUnit, DC_Obj target) {

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

    protected static Collection<? extends ActiveObj> getShortcuts(Unit sourceUnit,
                                                                  DC_Obj target) {

        List<ActiveObj> list = new ArrayList<>();
        for (RADIAL_ACTION_SHORTCUT sub : RADIAL_ACTION_SHORTCUT.values()) {
            if (!checkShortcut(sub, sourceUnit, target)) {
                continue;
            }
            DC_ActiveObj action = getShortcut(sub, sourceUnit, target);
            list.add(action);
        }
        list.removeIf(Objects::isNull);
        return list;
    }

    protected static boolean checkShortcut(RADIAL_ACTION_SHORTCUT sub, Unit sourceUnit, DC_Obj target) {
        switch (sub) {
            case ATTACK:
                return target instanceof BattleFieldObject;
        }
        return true;
    }

    protected static DC_ActiveObj getShortcut(RADIAL_ACTION_SHORTCUT sub,
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

    protected static boolean isQuickItemShown(DC_QuickItemObj item, DC_Obj target) {
        if (target != item.getOwnerObj()) {
            if (!(item.getActive().getTargeting() instanceof SelectiveTargeting)) {
                return false;
            }
        }
        return !item.isAmmo();
    }

    protected static RadialContainer getParentNode(RADIAL_PARENT_NODE type,
                                                   List<RadialContainer> containers) {
        if (containers.isEmpty()) {
            return null;
        }
        RadialContainer valueContainer = new RadialContainer(TextureCache.getOrCreateR(type.getIconPath()), null);
//        getOrCreateGrayscaleR(
        valueContainer.setChildNodes(containers);
        addSimpleTooltip(valueContainer, type.getName());
        return (valueContainer);
    }

    protected static RadialContainer getAttackParentNode(RADIAL_PARENT_NODE type,
                                                         RadialContainer valueContainer) {
        addSimpleTooltip(valueContainer, type.getName());
        return valueContainer;
    }

    protected static RadialContainer getExamineNode(DC_Obj target) {

        Runnable runnable = () -> {
            GuiEventManager.trigger(
                    GuiEventType.SHOW_UNIT_INFO_PANEL,
                    target);

        };
        final RadialContainer valueContainer = new RadialContainer(TextureCache.getOrCreateR("ui/components/dc/radial/examine.png"), runnable);
        addSimpleTooltip(valueContainer, "Examine");
        return valueContainer;
    }

    public static void addSimpleTooltip(RadialContainer el, String name) {
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Collections.singletonList(new ValueContainer(name, "")));
        for (com.badlogic.gdx.scenes.scene2d.EventListener sub : el.getListeners()) {
            if (!(sub instanceof ClickListener))
                el.removeListener(sub);
        }
        el.addListener(tooltip.getController());
    }

    protected static void addAttackTooltip(RadialContainer valueContainer,
                                           DC_ActiveObj activeObj,
                                           DC_Obj target) {
        valueContainer.setTooltipSupplier(() -> AttackTooltipFactory.createAttackTooltip((DC_UnitAction)
                activeObj, target));


    }

    protected static RadialContainer configureActionNode(DC_Obj target, DC_ActiveObj el) {
        if (el.getTargeting() instanceof SelectiveTargeting) {
            return configureSelectiveTargetedNode(el, target);
        }
        RadialContainer valueContainer;
        if (el instanceof Spell) {
            valueContainer = new SpellRadialContainer(

                    new TextureRegion(getTextureForActive(el, target))

                    , getRunnable(target, el),
                    checkValid(el, target), el, target);
        } else {
            valueContainer = new RadialContainer(
                    new TextureRegion(getTextureForActive(el, target)), getRunnable(target, el),
                    checkValid(el, target), el, target);
        }
        addSimpleTooltip(valueContainer, el.getName());
        return valueContainer;
    }

    protected static boolean checkValid(DC_ActiveObj activeObj, DC_Obj target) {
        if (target == null) {
            return false;
        }
        Ref ref = activeObj.getOwnerUnit().getRef().getTargetingRef(target);
        return activeObj.canBeActivated(ref);
    }

    protected static RadialContainer
    configureSelectiveTargetedNode(DC_ActiveObj active) {
        return configureSelectiveTargetedNode(active, null);
    }

    protected static RadialContainer configureSelectiveTargetedNode(
            DC_ActiveObj active, DC_Obj target) {
        boolean wasValid;
        wasValid = active.canBeManuallyActivated();
        final boolean valid = wasValid;

        TextureRegion textureRegion = getTextureForActive(active);

        Runnable runnable = () -> {
            if (valid) {
                Context context = new Context(active.getOwnerUnit().getRef());
                if (target != null)
                    if (!target.equals(active.getOwnerUnit())) {
                        context.setTarget(target.getId());
                    }
                Eidolons.getGame().getLoop().actionInputManual(
                        new ActionInput(active, context));
            } else {
                FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.CANNOT_ACTIVATE, "", active);
            }

        };
        if (active instanceof Spell) {
            return new SpellRadialContainer(textureRegion, runnable, valid, active, target);
        }
        return new RadialContainer(textureRegion, runnable, valid, active, target);
    }

    protected static RadialContainer configureAttackParentNode(

            List<RadialContainer> list, RADIAL_PARENT_NODE parentNode, DC_Obj target, DC_ActiveObj parent) {
        if (parent.getActiveWeapon().isRanged()) {
            if (parent.getRef().getObj(Ref.KEYS.AMMO) == null) {
                for (DC_QuickItemObj ammo : parent.getOwnerUnit().getQuickItems()) {
                    final RadialContainer valueContainer = new RadialContainer(TextureCache.getOrCreateR(ammo.getImagePath()), getRunnable(target, ammo.getActive()));
                    addSimpleTooltip(valueContainer, ammo.getName());
                    list.add(valueContainer);
                }
            }
        }

        RadialContainer valueContainer =
                new RadialContainer(new TextureRegion(getTextureForActive(parent, target)), null,
                        checkValid(parent, target), parent, target);
        addSimpleTooltip(valueContainer, parentNode.getName());
        valueContainer.setChildNodes(list);

        return valueContainer;
    }

    protected static RadialContainer getAttackActionNode(DC_ActiveObj activeObj, DC_Obj target) {
//        if (activeObj.getOwnerUnit() == target) {
        final RadialContainer valueContainer =
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

    protected static RadialContainer configureMoveNode(DC_Obj target,
                                                       DC_ActiveObj activeObj) {
        RadialContainer result;

        if (target == activeObj.getOwnerUnit()) {
            result = configureSelectiveTargetedNode(activeObj);
        } else {
            if (activeObj.getTargeting() instanceof SelectiveTargeting) {
                result = new RadialContainer(TextureCache.getOrCreateR(activeObj.getImagePath()), getRunnable(target, activeObj));
            } else {
                result = new RadialContainer(new TextureRegion(
                        getTextureForActive(activeObj, target)), getRunnable(target, activeObj), checkValid(activeObj, target), activeObj, target);
            }
        }
        addSimpleTooltip(result, activeObj.getName());

        return result;
    }

    protected static Runnable getRunnable(DC_Obj target, DC_ActiveObj activeObj) {
//        Runnable runnable=        runnableCaches.getVar(target).getVar(activeObj);

        if (activeObj instanceof DC_ActiveObj) {
            if (activeObj.getTargeting() instanceof SelectiveTargeting) {
                return () -> {
                    if (activeObj.isMove()) {
                        DC_Cell cell = target.getGame().getCellByCoordinate(target.getCoordinates());
                        if (!activeObj.getTargeter().canBeTargeted(cell.getId()))
                            return;
                    }
                    activeObj.activateOn(target);


                };
            }
        }
        if (target == activeObj.getOwnerUnit())
            return activeObj::invokeClicked;
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
                int chance = 0;
                try {
                    chance = activeObj.getCalculator().getCritOrDodgeChance(target);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                if (chance == 0)
                    return null;
                int finalChance = chance;
                return () -> {
                    String string;
                    if (finalChance > 0) {
                        string = finalChance + "% to crit";
                    } else
                        string = finalChance + "% to miss";
                    return string;
                };
            }
        if (activeObj.isSpell()) {

        }
        return null;
    }

    public Supplier<List<RadialContainer>> get(RADIAL_PARENT_NODE type,
                                               DC_ActiveObj activeObj,
                                               DC_Obj target) {
        return () -> {
            return getChildNodes(type, activeObj, target);
        };
    }

    protected List<RadialContainer> getChildNodes(RADIAL_PARENT_NODE type, DC_ActiveObj activeObj, DC_Obj target) {
        return new ArrayList<>();
    }

    public enum RADIAL_ACTION_SHORTCUT {
        TURN_TO,
        MOVE_TO,
        ATTACK,

    }

    public enum RADIAL_PARENT_NODE {
        OFFHAND_ATTACKS,
        TURN_ACTIONS("/UI/components/dc/radial/turns.png"),
        SPELLS("/UI/components/dc/radial/spells.png"),
        MOVES("ui/components/dc/radial/moves.png"),
        MAIN_HAND_ATTACKS,
        SPECIAL("ui/components/dc/radial/special actions.png"),
        QUICK_ITEMS("ui/components/dc/radial/quick items.png"),
        MODES("ui/components/dc/radial/additional actions.png"),
        ORDERS("ui/components/dc/radial/orders.png"),
        DUAL_ATTACKS("ui/components/dc/radial/DUAL_ATTACKS.png");

        protected String iconPath;

        RADIAL_PARENT_NODE() {
        }

        RADIAL_PARENT_NODE(String iconPath) {
            this.iconPath = iconPath;
        }

        public String getIconPath() {
            return iconPath;
        }

        public String getName() {
            return StringMaster.format(name());
        }
    }
}
