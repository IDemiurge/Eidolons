package libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.headquarters.HqElement;
import eidolons.system.DC_Formulas;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroXp extends HqElement {


    private final ValueContainer cur;
    private final ValueContainer next;
    private final ValueContainer unspent;

    public HqHeroXp() {
        setBackground(getDefaultBackground());
//        GdxMaster.adjustAndSetSize(this,
                setSize(380, 112);

       add( cur = new ValueContainer("", "" )).left().uniform();
        row();
        add(  next = new ValueContainer("", "" )).left().uniform();
        row();
        add(  unspent = new ValueContainer("", "" )).left().uniform();

        cur.setWidth(Align.left);
        next.setNameAlignment(Align.left);
        unspent.setNameAlignment(Align.left);

        cur.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        next.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        unspent.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));

        cur.setValueAlignment(Align.right);
        next.setValueAlignment(Align.right);
        unspent.setValueAlignment(Align.right);

        cur.setNameAlignment(Align.left);
        next.setNameAlignment(Align.left);
        unspent.setNameAlignment(Align.left);

        cur.setFixedMinSize(true);
        next.setFixedMinSize(true);
        unspent.setFixedMinSize(true);

        cur.setWidth(getWidth()*0.8f);
        next.setWidth(getWidth()*0.8f);
        unspent.setWidth(getWidth()*0.8f);

        cur.setHeight(getHeight()*0.3f);
        next.setHeight(getHeight()*0.3f);
        unspent.setHeight(getHeight()*0.3f);
    }

    @Override
    protected void update(float delta) {
        cur.setValueText("Experience: " +dataSource.getParamRounded(PARAMS.TOTAL_XP));
        next.setValueText("Next Level:  "
         +
         DC_Formulas.getTotalXpForLevel(dataSource.getLevel() + 1));
        unspent.setValueText("Unspent:    " +dataSource.getParamRounded(PARAMS.XP));
//         dataSource.getParam(PARAMS.XP));
    }
}
