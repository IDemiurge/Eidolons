package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.content.VALUE;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static main.content.PARAMS.*;
import static main.libgdx.texture.TextureCache.getOrCreate;

public class ActionToolTip extends ToolTip<Supplier<Map<VALUE, List<MultiValueContainer>>>> {
    @Override
    public void updateAct() {
        final Map<VALUE, List<MultiValueContainer>> paramsListMap = getUserObject().get();

        for (MultiValueContainer valueTooltip : paramsListMap.get(BASE_DAMAGE)) {
             inner.addElement(valueTooltip);
        }

        for (MultiValueContainer valueTooltip : paramsListMap.get(COUNTER_MOD)) {
            inner.addElement(valueTooltip);

        }

        for (MultiValueContainer valueTooltip : paramsListMap.get(INSTANT_DAMAGE_MOD)) {
            inner.addElement(valueTooltip);

        }
    }

    @Override
    protected void postUpdateAct() {
        inner.pad(20);

        NinePatchDrawable ninePatchDrawable =
                new NinePatchDrawable(new NinePatch(getOrCreate("UI/components/tooltip_background.9.png")));
        setBackground(ninePatchDrawable);
    }
}
