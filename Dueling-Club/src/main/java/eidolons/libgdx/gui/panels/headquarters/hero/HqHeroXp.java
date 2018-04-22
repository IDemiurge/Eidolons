package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.system.DC_Formulas;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroXp extends HqElement {


    private final ValueContainer cur;
    private final ValueContainer next;

    public HqHeroXp() {
        setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanel()));
        GdxMaster.adjustAndSetSize(this, 380, 96);

       add( cur = new ValueContainer("", "" )).left();
       row();
        add(  next = new ValueContainer("", "" )).left();

        cur.setWidth(Align.left);
        next.setNameAlignment(Align.left);

        cur.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        next.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));

        cur.setValueAlignment(Align.right);
        next.setValueAlignment(Align.right);

        cur.setNameAlignment(Align.left);
        next.setNameAlignment(Align.left);
    }

    @Override
    protected void update(float delta) {
        cur.setValueText("Experience: " +dataSource.getParamRounded(PARAMS.TOTAL_XP));
        next.setValueText("Next Level: "
         +
         DC_Formulas.getTotalXpForLevel(dataSource.getLevel() + 1));
//         dataSource.getParam(PARAMS.XP));
    }
}
