package libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DefaultActionHandler;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.ai.tools.priority.ThreatAnalyzer;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.objects.ContainerObj;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.StyleHolder;
import libgdx.bf.generic.ImageContainer;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.bf.grid.cell.UnitView;
import libgdx.gui.generic.ValueContainer;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
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
import main.system.entity.CounterMaster;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class UnitViewTooltipFactory extends TooltipFactory<BattleFieldObject, BaseView> {
    private UnitViewTooltipFactory() {

    }

    public static UnitViewTooltip create(UnitView view, BattleFieldObject object) {
        final UnitViewTooltip tooltip = new UnitViewTooltip(view);
        tooltip.setUserObject(getSupplier(object, view));
        view.setToolTip(tooltip);
        return tooltip;
    }

    public static Supplier<List<Actor>> getSupplier(BattleFieldObject hero, BaseView view) {
        try {
            return new UnitViewTooltipFactory().supplier(hero, view);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return () ->
                new ArrayList<>(Collections.singletonList(new ValueContainer("Error", "")));
    }

    @Override
    protected Tooltip createTooltip(BaseView actor) {
        return new UnitViewTooltip(actor);
    }

    @Override
    protected Supplier<List<Actor>> supplier(BattleFieldObject object, BaseView view) {
        return () -> {
            if (Eidolons.getGame().getStateManager().isResetting()) {
                return null;
            }
            maxWidth = getMaxWidth();
            this.values = new ArrayList<>();
            if (object.isBeingReset()) {
                values.add(new ValueContainer("Calculating..."));
                return values;
            }
            if (object.isDead()) {
                //                addKeyAndValue();
                values.add(new ValueContainer("Corpse of ", object.getName()));
                if (object.getRef().getValue(KEYS.KILLER) != null)
                    if (object.getRef().getId(KEYS.KILLER) != object.getId()) {
                        add(new ValueContainer("Slain by ",
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
                    add(activationTooltip);
                }
            }

            if (!object.isMine())
                if (!object.getGame().isDebugMode())
                        if (object.getVisibilityLevelForPlayer() !=
                                VISIBILITY_LEVEL.CLEAR_SIGHT) {
                            //ENEMY OUTLINE
                            final ValueContainer nameContainer = new ValueContainer(object.getToolTip(), "");
                            nameContainer.setNameAlignment(Align.center);
                            add(nameContainer);
                            if (object.getGame().isStarted())
                                if (object.getUnitVisionStatus() != null) {
                                    final ValueContainer valueContainer =
                                            new ValueContainer(StringMaster.format(object.getUnitVisionStatus().name()), "");
                                    valueContainer.setNameAlignment(Align.center);
                                    add(valueContainer);
                                }
                            String text = object.getGame().getVisionMaster().getHintMaster().getHintsString(object);
                            TextureRegion texture = TextureCache.getOrCreateR(VISUALS.QUESTION.getImgPath());
                            final ValueContainer hintsContainer = new ValueContainer(texture, text);
                            hintsContainer.setValueAlignment(Align.right);
                            add(hintsContainer);

                            return values;
                        }
            if (view instanceof UnitGridView) {
                if (((UnitGridView) view).getInitiativeQueueUnitView() != null) {

                    super.addTitleContainer(object.getName(), Images.CIRCLE_BORDER,
                            ((UnitGridView) view).getInitiativeQueueUnitView().processPortraitTexture(
                                    object.getImagePath()
                            ));
                }
            }
            super.addNameContainer(object.getToolTip());

            // final ValueContainer nameContainer = new ValueContainer(object.getToolTip(), "");
            // nameContainer.setNameAlignment(Align.center);
            //add(nameContainer);
            //TODO emblem?
            if (object instanceof Unit) {
                if (object.getOwner().isEnemy()) {
                    VisualEnums.THREAT_LEVEL threat = ThreatAnalyzer.getThreatLevel(object);
                    Color color = threat.color;
                    Label.LabelStyle style = StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.AVQ, 24, color);
                    super.addStyledContainer(style, "             ", StringMaster.format(threat.toString()));
                } else {
                    Label.LabelStyle style = StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.AVQ, 24,
                            object.getOwner().isNeutral() ? GdxColorMaster.BRONZE : GdxColorMaster.CYAN);
                    super.addStyledContainer(style, "             ",
                            object.getOwner().isNeutral() ? "Neutral" : "Ally");

                }
                add(new ImageContainer(Images.SEPARATOR_NARROW));
            }


            INFO_LEVEL info_level =
                    new EnumMaster<INFO_LEVEL>().
                            retrieveEnumConst(INFO_LEVEL.class,
                                    OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.INFO_DETAIL_LEVEL));


            addPropStringToValues(object, G_PROPS.STANDARD_PASSIVES, false);
            addPropStringToValues(object, G_PROPS.CLASSIFICATIONS, false);

            super.startContainer();
            container.defaults().uniform();
            if (object.checkBool(GenericEnums.STD_BOOLS.FAUX)) {
                super.endContainer();
                add(new ValueContainer("Fragile"));
                super.startContainer();
            } else if (object.isIndestructible()) {
                super.endContainer();
                add(new ValueContainer("Indestructible"));
                super.startContainer();
            } else {
                // add(getValueContainer(object, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
                // if (!object.isPlayerCharacter() || EidolonsGame.getVar("endurance")) {
                //     add(getValueContainer(object, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));
                // }
            }
            if (!object.isBfObj())
                if (info_level != null)
                    switch (info_level) {
                        case VERBOSE:
                            //if not full?
                        case NORMAL:
                            wrap = false;
                            add(getValueContainer(object, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
                            add(getValueContainer(object, PARAMS.C_FOCUS, PARAMS.FOCUS));
                            container.row();
                            add(getValueContainer(object, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));
                            add(getValueContainer(object, PARAMS.C_ESSENCE, PARAMS.ESSENCE));
                            container.row();
                            wrap = true;
                            super.endContainer();
                        case BASIC:
                            super.endContainer();
                            startContainer();
                            container.removeBackground();
                            container.defaults().growX().padRight(10).padLeft(10);

                            wrap=false;
                            addParamStringToValues(object, PARAMS.ATTACK);
                            addParamStringToValues(object, PARAMS.DEFENSE);
                            container.row();
                            addParamStringToValues(object, PARAMS.INITIATIVE);
                            addParamStringToValues(object, PARAMS.DAMAGE);
                            container.row();
                            endContainer();
                            startContainer();
                            if (object.getIntParam(PARAMS.EXTRA_ATTACKS) > 0) {
                                add(getValueContainer(object, PARAMS.C_EXTRA_ATTACKS, PARAMS.EXTRA_ATTACKS));
                            }
                            if (object.getIntParam(PARAMS.EXTRA_MOVES) > 0) {
                                add(getValueContainer(object, PARAMS.C_EXTRA_MOVES, PARAMS.EXTRA_MOVES));
                            }
                            endContainer();
                    }
            if (!object.isIndestructible())
                if (info_level != INFO_LEVEL.BASIC) {
                    addParamStringToValues(object, PARAMS.ARMOR);
                    addParamStringToValues(object, PARAMS.RESISTANCE);
                }

            if (object.getGame().isDebugMode()) {
                ValueContainer valueContainer =
                        new ValueContainer("coord:", object.getCoordinates().toString());
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                add(valueContainer);
                if (object.getFacing() != null || object.getDirection() != null) {
                    final String name = "direction: " + (object.getFacing() != null ?
                            object.getFacing().getDirection() :
                            object.getDirection());
                    valueContainer = new ValueContainer(name, object.getCoordinates().toString());
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    add(valueContainer);
                }
            }
            if (object instanceof Unit) {
                addPropStringToValues(object, G_PROPS.MODE);
                addPropStringToValues(object, G_PROPS.STATUS);
            }
            if (CoreEngine.TEST_LAUNCH) {
                add(new ValueContainer("ID", object.getId() + ""));
            }
            if (object.getCustomParamMap() != null) {
                for (String counter : object.getCustomParamMap().keys()) {
                    final String name = StringMaster.format(counter);
                    String img = CounterMaster.getImagePath(counter);
                    if (img != null) {

                        TextureRegion texture = TextureCache.getOrCreateR(
                                img);

                        final ValueContainer valueContainer = (texture == null)
                                ? new ValueContainer(name, object.getCustomParamMap().get(counter))
                                : new ValueContainer(texture, name, object.getCustomParamMap().get(counter));
                        valueContainer.setNameAlignment(Align.left);
                        valueContainer.setValueAlignment(Align.right);
                        add(valueContainer);
                    }
                }
            }

            //            if (VisionManager.isVisibilityOn()){
            if (RuleKeeper.isRuleOn(RuleEnums.RULE.VISIBILITY) || Eidolons.game.isDebugMode()) {
                addParamStringToValues(object, PARAMS.LIGHT_EMISSION);
                addParamStringToValues(object, PARAMS.ILLUMINATION);
                addParamStringToValues(object, PARAMS.CONCEALMENT);
                //                    addKeyAndValue("Gamma", ""+hero.getGame().getVisionMaster().
                //                     getGammaMaster().
                //                     getGamma(false, hero.getGame().getManager().getActiveObj(), hero), values);
            }
            if (object.getGame().isDebugMode()) {

                final ValueContainer outlineContainer =
                        new ValueContainer(StringMaster.format
                                (object.getOutlineTypeForPlayer() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                add(outlineContainer);

                final ValueContainer outlineContainer2 =
                        new ValueContainer(StringMaster.format
                                (object.getVisibilityLevel() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                add(outlineContainer);
            }
            startContainer();
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
                add(container);
            }
            endContainer();
            return values;
        };
    }

    private float getMaxWidth() {
        return 500;
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
            if (OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.ALT_MODE_ON)) {
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
