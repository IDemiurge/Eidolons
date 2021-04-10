package libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import eidolons.content.ContentConsts;
import eidolons.content.PARAMS;
import libgdx.GDX;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.texture.TextureManager;
import eidolons.system.text.DescriptionTooltips;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqParamPanel extends HqElement{
    private final PARAMETER[] params;
    private   HorizontalGroup group;
    Array<ValueContainer> containers;

    public HqParamPanel(boolean dynamic) {
        this((dynamic)
         ? ContentConsts.DYNAMIC_PARAMETERS
         : ContentConsts.MAIN_PARAMETERS);
    }
    public HqParamPanel(PARAMETER...params) {
        setBackground(getDefaultBackground());
        setSize(params.length*100, GDX.size(75));
//        add(group = new HorizontalGroup()).center();
//        group.setSize(getWidth()-60, getHeight());
//        group.space(GDX.size(10));

       defaults().space(GDX.size(30));

        this.params = params;
        containers = new Array<>(6);


        for (PARAMETER sub : params) {
            ValueContainer container = new ValueContainer(
             TextureManager.getParamTexture(sub), ""){
                @Override
                protected boolean isVertical() {
                    return true;
                }


            };
            container.getValueContainer().align(Align.left);
            container.overrideImageSize(32,32);
//            container.setSize(GDX.size(100), GDX.size(64));
            container.setStyle(StyleHolder.getHqLabelStyle(18));

            containers.add(container);
            add(container).uniform().center();

            container.setBackground(NinePatchFactory.getLightPanelFilledSmallDrawable());
//            group.addActor(container);
        }
    }

    @Override
    protected void update(float delta) {
        int i =0;
        for (PARAMETER sub : params) {
            CharSequence text=getText(sub);
            containers.get(i).setValueText(text);
            int size = 18 + Math.round(18 * 2.0f / (1 + text.length()) / 10);
            containers.get(i).setStyle(StyleHolder.getHqLabelStyle(size));
            containers.get(i).clearListeners();
            containers.get(i).addListener(new ValueTooltip(DescriptionTooltips.tooltip(sub, getUserObject().getEntity())).getController());

            i++;
        }
    }

    private String getText(PARAMETER sub) {
        if (checkShowFraction(sub)) {
            String c = getUserObject().getParamRounded(sub);
            String m = getUserObject().getParamRounded(ContentValsManager.getBaseParameterFromCurrent(sub));
           if (!c.equalsIgnoreCase(m))
            return c + "/" + m;
        }
        return dataSource.getParamRounded(sub);
    }

    private boolean checkShowFraction(PARAMETER sub) {
        if (sub instanceof PARAMS) {
            switch (((PARAMS) sub)) {
//                case C_ENDURANCE:
                case C_TOUGHNESS:
//                case C_MORALE:
                case C_ESSENCE:
//                case C_FOCUS:
                    return true;
            }
        }
        return false;
    }
}
