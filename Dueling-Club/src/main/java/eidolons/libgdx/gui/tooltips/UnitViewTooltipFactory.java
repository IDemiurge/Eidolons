package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.UnitView;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.ToolTipMaster;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums.INFO_LEVEL;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.entity.CounterMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UnitViewTooltipFactory extends TooltipFactory<BattleFieldObject, BaseView> {
    private UnitViewTooltipFactory() {

    }

    public static UnitViewTooltip create(UnitView view, BattleFieldObject object) {
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(getSupplier(object));
        view.setToolTip(tooltip);
        return tooltip;
    }

    public static Supplier<List<ValueContainer>> getSupplier(BattleFieldObject hero) {
        try {
            return new UnitViewTooltipFactory().supplier(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return () ->
                new ArrayList<>(Arrays.asList(new ValueContainer("Error", "")));
    }

    @Override
    protected Tooltip createTooltip(BaseView actor) {
        return new UnitViewTooltip(actor);
    }

    @Override
    protected Supplier<List<ValueContainer>> supplier(BattleFieldObject object) {
        return () -> {
            if (Eidolons.getGame().getStateManager().isResetting()) {
                return null;
            }
            List<ValueContainer> values = new ArrayList<>();
            if (object.isBeingReset()) {
                values.add(new ValueContainer("Calculating..."));
                return values;
            }
            if (object.isDead()) {
//                addKeyAndValue();
                values.add(new ValueContainer("Corpse of ", object.getName()));
                if (object.getRef().getValue(KEYS.KILLER) != null)
                    if (object.getRef().getId(KEYS.KILLER) != object.getId()) {
                        values.add(new ValueContainer("Slain by ",
                                object.getGame().getObjectById(object.getRef().
                                        getId(KEYS.KILLER)).getNameIfKnown()
                        ));
                    }

                return values;
            }
            if (object.checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) object.getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(object, action);
                } catch (Exception e) {
                    if (!action.isBroken()) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    } else {
                        action.setBroken(true);
                    }
                }
                if (!StringMaster.isEmpty(actionTargetingTooltip)) {
                    final ValueContainer activationTooltip =
                            new ValueContainer(actionTargetingTooltip, "");
                    activationTooltip.setNameAlignment(Align.left);
                    values.add(activationTooltip);
                }
            }

            if (!object.isMine())
                if (!object.getGame().isDebugMode())
                    if (!ShadowMaster.isShadowAlive())
                        if (object.getVisibilityLevelForPlayer() !=
                                VISIBILITY_LEVEL.CLEAR_SIGHT) {
                            final ValueContainer nameContainer = new ValueContainer(object.getToolTip(), "");
                            nameContainer.setNameAlignment(Align.center);
                            values.add(nameContainer);
                            if (object.getGame().isStarted())
                                if (object.getUnitVisionStatus() != null) {
                                    final ValueContainer valueContainer =
                                            new ValueContainer(StringMaster.getWellFormattedString(object.getUnitVisionStatus().name()), "");
                                    valueContainer.setNameAlignment(Align.center);
                                    values.add(valueContainer);
                                }
                            String text = object.getGame().getVisionMaster().getHintMaster().getHintsString(object);
                            TextureRegion texture = TextureCache.getOrCreateR(VISUALS.QUESTION.getImgPath());
                            final ValueContainer hintsContainer = new ValueContainer(texture, text);
                            hintsContainer.setNameAlignment(Align.left);
                            hintsContainer.setValueAlignment(Align.right);
                            values.add(hintsContainer);


                            return values;
                        }
            final ValueContainer nameContainer = new ValueContainer(object.getToolTip(), "");
            nameContainer.setNameAlignment(Align.center);
            values.add(nameContainer);


            INFO_LEVEL info_level =
                    new EnumMaster<INFO_LEVEL>().
                            retrieveEnumConst(INFO_LEVEL.class,
                                    OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.INFO_DETAIL_LEVEL));

            addPropStringToValues(object, values, G_PROPS.STANDARD_PASSIVES, false);
            addPropStringToValues(object, values, G_PROPS.CLASSIFICATIONS, false);

            if (object.checkBool(GenericEnums.STD_BOOLS.FAUX)) {
                values.add(new ValueContainer("Fragile"));
            } else if (object.isIndestructible()) {
                values.add(new ValueContainer("Indestructible"));
            } else {
                values.add(getValueContainer(object, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
                if (!object.isPlayerCharacter() || EidolonsGame.getVar("endurance")) {
                    values.add(getValueContainer(object, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));
                }
            }
            if (!object.isBfObj())
                if (info_level != null)
                    switch (info_level) {
                        case VERBOSE:
                            //if not full?
                        case NORMAL:
                            values.add(getValueContainer(object, PARAMS.C_STAMINA, PARAMS.STAMINA));
                            values.add(getValueContainer(object, PARAMS.C_FOCUS, PARAMS.FOCUS));
                            values.add(getValueContainer(object, PARAMS.C_MORALE, PARAMS.MORALE));
                            values.add(getValueContainer(object, PARAMS.C_ESSENCE, PARAMS.ESSENCE));
                        case BASIC:
                            addParamStringToValues(object, values, PARAMS.N_OF_ACTIONS);
//                        .center()
                            addParamStringToValues(object, values, PARAMS.DAMAGE);
                            addParamStringToValues(object, values, PARAMS.ATTACK);
                            addParamStringToValues(object, values, PARAMS.DEFENSE);
                            if (object.getIntParam(PARAMS.N_OF_COUNTERS) > 0) {
                                values.add(getValueContainer(object, PARAMS.C_N_OF_COUNTERS, PARAMS.N_OF_COUNTERS));
                            }
                    }
            if (!object.isIndestructible())
                if (info_level == INFO_LEVEL.VERBOSE) {
                    addParamStringToValues(object, values, PARAMS.ARMOR);
                    addParamStringToValues(object, values, PARAMS.RESISTANCE);
                }

            if (object.getGame().isDebugMode()) {
                ValueContainer valueContainer =
                        new ValueContainer("coord:", object.getCoordinates().toString());
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
                if (object.getFacing() != null || object.getDirection() != null) {
                    final String name = "direction: " + (object.getFacing() != null ?
                            object.getFacing().getDirection() :
                            object.getDirection());
                    valueContainer = new ValueContainer(name, object.getCoordinates().toString());
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                }
            }
            if (object instanceof Unit) {
                addPropStringToValues(object, values, G_PROPS.MODE);
                addPropStringToValues(object, values, G_PROPS.STATUS);
            }

            if (object.getCustomParamMap() != null) {
                object.getCustomParamMap().keySet().forEach(counter -> {
                    final String name = StringMaster.getWellFormattedString(counter);
                    String img = CounterMaster.getImagePath(counter);
                    if (img != null) {

                        TextureRegion texture = TextureCache.getOrCreateR(
                                img);

                        final ValueContainer valueContainer = (texture == null)
                                ? new ValueContainer(name, object.getCustomParamMap().get(counter))
                                : new ValueContainer(texture, name, object.getCustomParamMap().get(counter));
                        valueContainer.setNameAlignment(Align.left);
                        valueContainer.setValueAlignment(Align.right);
                        values.add(valueContainer);
                    }
                });
            }

            //            if (VisionManager.isVisibilityOn()){
            if (RuleKeeper.isRuleOn(RULE.VISIBILITY) || Eidolons.game.isDebugMode()) {
                addParamStringToValues(object, values, PARAMS.LIGHT_EMISSION);
                addParamStringToValues(object, values, PARAMS.ILLUMINATION);
                addParamStringToValues(object, values, PARAMS.CONCEALMENT);
                //                    addKeyAndValue("Gamma", ""+hero.getGame().getVisionMaster().
                //                     getGammaMaster().
                //                     getGamma(false, hero.getGame().getManager().getActiveObj(), hero), values);
            }
            if (object.getGame().isDebugMode()) {

                final ValueContainer outlineContainer =
                        new ValueContainer(StringMaster.getWellFormattedString
                                (object.getOutlineTypeForPlayer() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);

                final ValueContainer outlineContainer2 =
                        new ValueContainer(StringMaster.getWellFormattedString
                                (object.getVisibilityLevel() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);
            }

            ValueContainer container = null;
            if (object.isOverlaying()) {
                container = getOverlayingTip(object);
            } else if (object instanceof ContainerObj) {
                container = getContainerTip(object);
            } else {
                try {
                    container = getAttackTip(object);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            if (container != null) {
                values.add(container);
            }
            return values;
        };
    }

    private ValueContainer getContainerTip(BattleFieldObject unit) {
        //check clearshot!
        //Left-Click to interact
        boolean out = PositionMaster.getExactDistance(Eidolons.getGame().getManager().getActiveObj(), unit) > 1.5f;
        if (!out)
            out = !new ClearShotCondition().check(Eidolons.getGame().getManager().getActiveObj(), unit);
        String text = "Left-Click to interact";
        if (out) {
            text = "(out of reach) " + text;
        }
        return new ValueContainer(text +
                "\nUnlocked\nNo Traps detected");
    }

    private ValueContainer getOverlayingTip(BattleFieldObject unit) {
        String text = "Alt-Click or Radial to open contents";
        boolean out = PositionMaster.getExactDistance(Eidolons.getGame().getManager().getActiveObj(), unit) > 1.5f;
        if (!out)
            out = !new ClearShotCondition().check(Eidolons.getGame().getManager().getActiveObj(), unit);
        if (out) {
            text = "(out of reach) " + text;
        }
        return new ValueContainer(text +
                "\nUnlocked\nNo Traps detected");
    }

    private ValueContainer getAttackTip(BattleFieldObject unit) {
        DC_ActiveObj attackAction = DefaultActionHandler.getPreferredAttackAction(Eidolons.getMainHero(), unit);
        if (attackAction != null) {
            String control =
                    "Click to attack with ";
            if (!OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.ALT_MODE_ON)) {
                control = "Alt-" + control;
            }
            Ref ref = Eidolons.getMainHero().getRef().getCopy();
            ref.setID(KEYS.ACTIVE, attackAction.getId());
            ref.setTarget(unit.getId());
//                    Attack attack = DC_AttackMaster.getAttackFromAction(attackAction);
            String tip = "Damage: " +
                    FutureBuilder.precalculateDamage(attackAction, unit, true, true)
                    //DamageCalculator.precalculateDamage(attack, true)
                    + "-" + FutureBuilder.precalculateDamage(attackAction, unit, true, false)
                    //DamageCalculator.precalculateDamage(attack, false)
                    ;
//chance to hit
            return new ValueContainer(control +
                    attackAction.getName() + "\n" + tip);

        }
        return null;
    }

}
