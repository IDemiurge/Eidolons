package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.ToolTipMaster;
import main.content.enums.rules.VisionEnums.INFO_LEVEL;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.entity.Ref.KEYS;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.entity.CounterMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class UnitViewTooltipFactory extends TooltipFactory<BattleFieldObject, BaseView> {
    private UnitViewTooltipFactory() {

    }

    public static Supplier<List<ValueContainer>> create(BattleFieldObject hero) {
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
    protected Supplier<List<ValueContainer>> supplier(BattleFieldObject hero) {
        return () -> {
            List<ValueContainer> values = new ArrayList<>();
            if (hero.isDead()) {
//                addKeyAndValue();
                values.add(new ValueContainer("Corpse of ", hero.getName()));
                if (hero.getRef().getValue(KEYS.KILLER) != null)
                    if (hero.getRef().getId(KEYS.KILLER) != hero.getId()) {
                        values.add(new ValueContainer("Slain by ",
                         hero.getGame().getObjectById(hero.getRef().
                          getId(KEYS.KILLER)).getNameIfKnown()
                        ));
                    }

                return values;
            }
            if (hero.checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) hero.getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(hero, action);
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

            if (!hero.isMine())
                if (!hero.getGame().isDebugMode())
                    if (hero.getVisibilityLevelForPlayer() !=
                     VISIBILITY_LEVEL.CLEAR_SIGHT) {
                        final ValueContainer nameContainer = new ValueContainer(hero.getToolTip(), "");
                        nameContainer.setNameAlignment(Align.left);
                        values.add(nameContainer);
                        if (hero.getGame().isStarted())
                            if (hero.getUnitVisionStatus() != null) {
                                final ValueContainer valueContainer =
                                 new ValueContainer(StringMaster.getWellFormattedString(hero.getUnitVisionStatus().name()), "");
                                valueContainer.setNameAlignment(Align.left);
                                values.add(valueContainer);
                            }
                        String text = hero.getGame().getVisionMaster().getHintMaster().getHintsString(hero);
                        TextureRegion texture = TextureCache.getOrCreateR(VISUALS.QUESTION.getImgPath());
                        final ValueContainer hintsContainer = new ValueContainer(texture, text);
                        hintsContainer.setNameAlignment(Align.left);
                        hintsContainer.setValueAlignment(Align.right);
                        values.add(hintsContainer);


                        return values;
                    }
            final ValueContainer nameContainer = new ValueContainer(hero.getToolTip(), "");
            nameContainer.setNameAlignment(Align.left);
            values.add(nameContainer);


            INFO_LEVEL info_level =
             new EnumMaster<INFO_LEVEL>().
              retrieveEnumConst(INFO_LEVEL.class,
               OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.INFO_DETAIL_LEVEL));

            values.add(getValueContainer(hero, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
            values.add(getValueContainer(hero, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));
            if (info_level != null)
                switch (info_level) {
                    case VERBOSE:
                        values.add(getValueContainer(hero, PARAMS.C_STAMINA, PARAMS.STAMINA));
                        values.add(getValueContainer(hero, PARAMS.C_FOCUS, PARAMS.FOCUS));
                        values.add(getValueContainer(hero, PARAMS.C_MORALE, PARAMS.MORALE));
                        values.add(getValueContainer(hero, PARAMS.C_ESSENCE, PARAMS.ESSENCE));
                    case NORMAL:
                        addParamStringToValues(hero, values, PARAMS.ARMOR);
                        addParamStringToValues(hero, values, PARAMS.RESISTANCE);
                    case BASIC:
                        addParamStringToValues(hero, values, PARAMS.DAMAGE);
                        addParamStringToValues(hero, values, PARAMS.ATTACK);
                        addParamStringToValues(hero, values, PARAMS.DEFENSE);
                }


            if (hero.getIntParam(PARAMS.N_OF_ACTIONS) > 0) {
                addParamStringToValues(hero, values, PARAMS.N_OF_ACTIONS);
            } else {
                //immobile
            }
            if (hero.getIntParam(PARAMS.N_OF_COUNTERS) > 0) {
                values.add(getValueContainer(hero, PARAMS.C_N_OF_COUNTERS, PARAMS.N_OF_COUNTERS));
            }
            if (hero.getGame().isDebugMode()) {
                ValueContainer valueContainer =
                 new ValueContainer("coord:", hero.getCoordinates().toString());
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
                if (hero.getFacing() != null || hero.getDirection() != null) {
                    final String name = "direction: " + (hero.getFacing() != null ?
                     hero.getFacing().getDirection() :
                     hero.getDirection());
                    valueContainer = new ValueContainer(name, hero.getCoordinates().toString());
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                }
            }
            if (hero instanceof Unit) {
                addPropStringToValues(hero, values, G_PROPS.MODE);
                addPropStringToValues(hero, values, G_PROPS.STATUS);
            }

            if (hero.getCustomParamMap() != null) {
                hero.getCustomParamMap().keySet().forEach(counter -> {
                    final String name = StringMaster.getWellFormattedString(counter);
                    String img = CounterMaster.getImagePath(counter);
                    if (img != null) {

                        TextureRegion texture = TextureCache.getOrCreateR(
                         img);

                        final ValueContainer valueContainer = (texture == null)
                         ? new ValueContainer(name, hero.getCustomParamMap().get(counter))
                         : new ValueContainer(texture, name, hero.getCustomParamMap().get(counter));
                        valueContainer.setNameAlignment(Align.left);
                        valueContainer.setValueAlignment(Align.right);
                        values.add(valueContainer);
                    }
                });
            }

            //            if (VisionManager.isVisibilityOn()){
            if (RuleKeeper.isRuleOn(RULE.VISIBILITY) || Eidolons.game.isDebugMode()) {
                addParamStringToValues(hero, values, PARAMS.LIGHT_EMISSION);
                addParamStringToValues(hero, values, PARAMS.ILLUMINATION);
                addParamStringToValues(hero, values, PARAMS.CONCEALMENT);
                //                    addKeyAndValue("Gamma", ""+hero.getGame().getVisionMaster().
                //                     getGammaMaster().
                //                     getGamma(false, hero.getGame().getManager().getActiveObj(), hero), values);
            }
            if (hero.getGame().isDebugMode()) {

                final ValueContainer outlineContainer =
                 new ValueContainer(StringMaster.getWellFormattedString
                  (hero.getOutlineTypeForPlayer() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);

                final ValueContainer outlineContainer2 =
                 new ValueContainer(StringMaster.getWellFormattedString
                  (hero.getVisibilityLevel() + ""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);
            }
            return values;
        };
    }

}
