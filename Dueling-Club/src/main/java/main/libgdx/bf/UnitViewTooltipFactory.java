package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import main.content.PARAMS;
import main.content.enums.rules.VisionEnums.INFO_LEVEL;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.core.Eidolons;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.entity.CounterMaster;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import main.system.text.ToolTipMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewTooltipFactory {
    private UnitViewTooltipFactory() {

    }

    private static ValueContainer getValueContainer(BattleFieldObject hero, PARAMS cur, PARAMS max) {
        final Integer cv =StringMaster.getInteger(hero.getCachedValue(max));
        final Integer v = hero.getIntParam(cur);
        final String name = max.getName();
        final TextureRegion iconTexture = getOrCreateR("UI\\value icons\\" + name.replaceAll("_", " ") + ".png");
        final ValueContainer valueContainer = new ValueContainer(iconTexture, name, v + "/" + cv);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        return valueContainer;
    }

    public static Supplier<List<ValueContainer>> create(BattleFieldObject hero) {
        try {
            return create_(hero);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return () ->
            new ArrayList<>(Arrays.asList(new ValueContainer("Error", "")));
    }

    public static Supplier<List<ValueContainer>> create_(BattleFieldObject hero) {
        return () -> {
            List<ValueContainer> values = new ArrayList<>();
            if (hero.checkSelectHighlighted()) {
                String actionTargetingTooltip = "";
                DC_ActiveObj action = (DC_ActiveObj) hero.getGame().getManager().getActivatingAction();
                try {
                    actionTargetingTooltip = ToolTipMaster.getActionTargetingTooltip(hero, action);
                } catch (Exception e) {
                    if (!action.isBroken()) {
                        e.printStackTrace();
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
                    if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.OUTLINES))
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





            INFO_LEVEL   info_level =
              new EnumMaster<INFO_LEVEL>().
               retrieveEnumConst(INFO_LEVEL.class,
            OptionsMaster.getGameplayOptions().getValue(GAMEPLAY_OPTION.INFO_DETAIL_LEVEL));

            values.add(getValueContainer(hero, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
            values.add(getValueContainer(hero, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));
if (info_level!=null )
            switch (info_level){
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
                values.add(getValueContainer(hero, PARAMS.C_N_OF_ACTIONS, PARAMS.N_OF_ACTIONS));
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
                    final String name =  StringMaster.getWellFormattedString(counter);
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
            if ( RuleMaster.isRuleOn(RULE.VISIBILITY) || Eidolons.game.isDebugMode()){
                addParamStringToValues(hero, values, PARAMS.LIGHT_EMISSION);
                addParamStringToValues(hero, values, PARAMS.ILLUMINATION);
                addParamStringToValues(hero, values, PARAMS.CONCEALMENT);
//                    addKeyAndValue("Gamma", ""+hero.getGame().getVisionMaster().
//                     getGammaMaster().
//                     getGamma(false, hero.getGame().getManager().getActiveObj(), hero), values);
            }
            if ( hero.getGame().isDebugMode())
            {

                final ValueContainer outlineContainer =
                 new ValueContainer(StringMaster.getWellFormattedString
                  (hero.getOutlineTypeForPlayer()+""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);

                final ValueContainer outlineContainer2 =
                 new ValueContainer(StringMaster.getWellFormattedString
                  (hero.getVisibilityLevel()+""), "");
                outlineContainer.setNameAlignment(Align.left);
                values.add(outlineContainer);
            }
            return values;
        };
    }

    private static void addPropStringToValues(BattleFieldObject hero,
                                              List<ValueContainer> values,
                                              PROPERTY v) {
        String value = hero.getValue(v);
        value = StringMaster.getWellFormattedString(value);
        value = value.replace(";", ", ");
        final ValueContainer valueContainer =
         new ValueContainer(v.getName(), value);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        values.add(valueContainer);
    }

    private static void addParamStringToValues(BattleFieldObject hero,
                                               List<ValueContainer> values,
                                               PARAMETER param) {
        if (hero.getIntParam(param) > 0) {
            String value = hero.getStrParam(param);
            String key = param.getName();
           addKeyAndValue(key, value, values);
        }
    }

    private static void addKeyAndValue(String key, String value, List<ValueContainer> values) {
        final ValueContainer valueContainer =
         new ValueContainer(key, value);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        values.add(valueContainer);}

}
