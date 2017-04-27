package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewTooltipFactory {
    private UnitViewTooltipFactory() {

    }

    private static ValueContainer getValueContainer(BattleFieldObject hero, PARAMS cur, PARAMS max) {
        final Integer cv = hero.getIntParam(max);
        final Integer v = hero.getIntParam(cur);
        final String name = max.getName();
        final TextureRegion iconTexture = getOrCreateR("UI\\value icons\\" + name.replaceAll("_", " ") + ".png");
        final ValueContainer valueContainer = new ValueContainer(iconTexture, name, v + "/" + cv);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        return valueContainer;
    }

/*    public static ToolTip create(BattleFieldObject hero) {
        List<ValueContainer> values = new ArrayList<>();

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

        {
            final ValueContainer valueContainer =
                    new ValueContainer("coord:", hero.getCoordinates().toString());
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setValueAlignment(Align.right);
            values.add(valueContainer);
        }

        if (hero.getFacing() != null || hero.getDirection() != null) {
            final String name = "direction: " + (hero.getFacing() != null ?
                    hero.getFacing().getDirection() :
                    hero.getDirection());
            final ValueContainer valueContainer = new ValueContainer(name, hero.getCoordinates().toString());
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setValueAlignment(Align.right);
            values.add(valueContainer);
        }

        if (hero.getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
            final ValueContainer valueContainer =
                    new ValueContainer("LIGHT_EMISSION", hero.getStrParam(PARAMS.LIGHT_EMISSION));
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setValueAlignment(Align.right);
            values.add(valueContainer);
        }

        if (hero.getCustomParamMap() != null) {
            hero.getCustomParamMap().keySet().forEach(counter -> {
                final String name = counter + " " + hero.getCustomParamMap().get(counter);
                final ValueContainer valueContainer = new ValueContainer(name, "");
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
            });
        }

        final UnitViewTooltip tooltip = new UnitViewTooltip();
        tooltip.setUserObject(values);

        return tooltip;
    }*/

    public static Supplier<List<ValueContainer>> create(BattleFieldObject hero) {
        return () -> {
            List<ValueContainer> values = new ArrayList<>();

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

            {
                final ValueContainer valueContainer =
                        new ValueContainer("coord:", hero.getCoordinates().toString());
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
            }

            if (hero.getFacing() != null || hero.getDirection() != null) {
                final String name = "direction: " + (hero.getFacing() != null ?
                        hero.getFacing().getDirection() :
                        hero.getDirection());
                final ValueContainer valueContainer = new ValueContainer(name, hero.getCoordinates().toString());
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
            }

            if (hero.getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
                final ValueContainer valueContainer =
                        new ValueContainer("LIGHT_EMISSION", hero.getStrParam(PARAMS.LIGHT_EMISSION));
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setValueAlignment(Align.right);
                values.add(valueContainer);
            }

            if (hero.getCustomParamMap() != null) {
                hero.getCustomParamMap().keySet().forEach(counter -> {
                    final String name = counter + " " + hero.getCustomParamMap().get(counter);
                    final ValueContainer valueContainer = new ValueContainer(name, "");
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                });
            }
            return values;
        };
    }
}
