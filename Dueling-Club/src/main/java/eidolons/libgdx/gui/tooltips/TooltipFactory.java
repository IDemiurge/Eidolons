package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.NumberUtils;
import main.system.images.ImageManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 4/5/2018.
 */
public abstract class TooltipFactory<T, A extends Actor> {

    public void add(A actor, T data) {
        Tooltip tooltip = createTooltip(actor);
        tooltip.setUserObject(supplier(data));
        actor.addListener(tooltip.getController());

    }

    protected abstract Tooltip createTooltip(A actor);

    protected abstract Supplier<List<ValueContainer>> supplier(T data);

    protected void addPropStringToValues(BattleFieldObject hero,
                                         List<ValueContainer> values,
                                         PROPERTY v) {
        String value = hero.getValue(v);
        value = StringMaster.getWellFormattedString(value);
        value = value.replace(";", ", ");
        final ValueContainer valueContainer =
         new ValueContainer(v.getDisplayedName(), value);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        values.add(valueContainer);
    }

    protected void addParamStringToValues(BattleFieldObject hero,
                                          List<ValueContainer> values,
                                          PARAMETER param) {
        if (hero.getIntParam(param) > 0) {
            String value = hero.getStrParam(param);
            String key = param.getDisplayedName();
            addKeyAndValue(key, value, values);
        }
    }

    protected void addKeyAndValue(String key, String value, List<ValueContainer> values) {
        final ValueContainer valueContainer =
         new ValueContainer(key, value);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        values.add(valueContainer);
    }

    protected ValueContainer getValueContainer(BattleFieldObject hero, PARAMS cur, PARAMS max) {
        final Integer cv = NumberUtils.getInteger(hero.getCachedValue(max));
        final Integer v = hero.getIntParam(cur);
        final String name = max.getDisplayedName();
        final TextureRegion iconTexture =

         TextureCache.getOrCreateR(
          ImageManager.getValueIconPath(max)
//          "UI/value icons/" +
//         name.replaceAll("_", " ") + ".png"
         );
        final ValueContainer valueContainer = new ValueContainer(iconTexture, name, v + "/" + cv);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        return valueContainer;
    }

}
