package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import main.content.PARAMS;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.StringMaster;
import main.system.entity.CounterMaster;
import main.system.text.ToolTipMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
            new LinkedList<>(Arrays.asList(new ValueContainer("Error", "")));
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
                    if (hero.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT) {
                        final ValueContainer nameContainer = new ValueContainer(hero.getNameIfKnown(), "");
                        nameContainer.setNameAlignment(Align.left);
                        values.add(nameContainer);

                        final ValueContainer valueContainer =
                         new ValueContainer(StringMaster.getWellFormattedString(hero.getUnitVisionStatus().name()), "");
                        valueContainer.setNameAlignment(Align.left);
                        values.add(valueContainer);

                        String text = hero.getGame().getVisionMaster().getHintMaster().getHintsString(hero);
                        TextureRegion texture = TextureCache.getOrCreateR(VISUALS.QUESTION.getImgPath());
                        final ValueContainer hintsContainer = new ValueContainer(texture, text);
                        hintsContainer.setNameAlignment(Align.left);
                        hintsContainer.setValueAlignment(Align.right);
                        values.add(hintsContainer);

//                    hero.getUnitVisionStatus();
//                    hero.getOutlineTypeForPlayer;
//                    hero.getGame().getVisionMaster().getOutlineMaster().get
//                    final ValueContainer outlineContainer =
//                     new ValueContainer(StringMaster.getWellFormattedString(hero.getOutlineTypeForPlayer().name()), "");
//                    outlineContainer.setNameAlignment(Align.left);
//                    values.add(outlineContainer);

                        addParamStringToValues(hero, values, PARAMS.LIGHT_EMISSION);
                        addParamStringToValues(hero, values, PARAMS.ILLUMINATION);
                        addParamStringToValues(hero, values, PARAMS.CONCEALMENT);
                        return values;
                    }
            final ValueContainer nameContainer = new ValueContainer(hero.getName(), "");
            nameContainer.setNameAlignment(Align.left);
            values.add(nameContainer);

            values.add(getValueContainer(hero, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
            values.add(getValueContainer(hero, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));

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
            addParamStringToValues(hero, values, PARAMS.LIGHT_EMISSION);
            addParamStringToValues(hero, values, PARAMS.ILLUMINATION);
            addParamStringToValues(hero, values, PARAMS.CONCEALMENT);

            if (hero.getCustomParamMap() != null) {
                hero.getCustomParamMap().keySet().forEach(counter -> {
                    final String name = counter;
                    TextureRegion texture = TextureCache.getOrCreateR(
                     CounterMaster.getImagePath(counter));

                    final ValueContainer valueContainer = (texture == null)
                     ? new ValueContainer(name, hero.getCustomParamMap().get(counter))
                     : new ValueContainer(texture, name, hero.getCustomParamMap().get(counter));
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                });
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

    private static void addParamStringToValues(BattleFieldObject hero, List<ValueContainer> values,
                                               PARAMETER param) {
        if (hero.getIntParam(param) > 0) {
            final ValueContainer valueContainer =
             new ValueContainer(param.getName(), hero.getStrParam(param));
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setValueAlignment(Align.right);
            values.add(valueContainer);
        }
    }
}
