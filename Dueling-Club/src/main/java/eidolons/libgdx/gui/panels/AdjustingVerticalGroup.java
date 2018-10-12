package eidolons.libgdx.gui.panels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import eidolons.libgdx.GdxMaster;

/**
 * Created by JustMe on 10/12/2018.
 */
public class AdjustingVerticalGroup extends VerticalGroup {

    public AdjustingVerticalGroup(float width, float percentaToClaim) {
        float w = GdxMaster.adjustWidth(width);
        setWidth(MathUtils.lerp(width, w, percentaToClaim));
    }
}
