package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import eidolons.content.DC_ContentValsManager;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureManager;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqParamPanel extends HqElement{
    private   PARAMETER[] params;
    private   HorizontalGroup group;
    Array<ValueContainer> containers;

    public HqParamPanel(boolean dynamic) {
        this((dynamic)
         ? DC_ContentValsManager.DYNAMIC_PARAMETERS
         : DC_ContentValsManager.MAIN_PARAMETERS);
    }
    public HqParamPanel(PARAMETER...params) {
        setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanel()));
        setSize(GDX.size(300), GDX.size(75));
        add(group = new HorizontalGroup()).center();
        group.setSize(getWidth()-60, getHeight());
        group.space(GDX.size(10));
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
            container.overrideImageSize(32,32);
            container.setSize(GDX.size(50), GDX.size(64));
            container.setStyle(StyleHolder.getHqLabelStyle(18));
            container.addListener(new ValueTooltip(sub.getName()).getController());

            containers.add(container);
            group.addActor(container);
        }
    }

    @Override
    protected void update(float delta) {
        int i =0;
        for (PARAMETER sub : params) {
            containers.get(i).setValueText(dataSource.getParamRounded(sub));
            i++;
        }
    }
}
