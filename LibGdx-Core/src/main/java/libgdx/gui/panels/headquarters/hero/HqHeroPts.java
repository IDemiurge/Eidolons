package libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.headquarters.HqElement;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroPts extends HqElement {


    private final ValueContainer clazz;
    private final ValueContainer mstr;
    private final ValueContainer skill;
    private final ValueContainer spell;

    public HqHeroPts() {
        setBackground(getDefaultBackground());
//        GdxMaster.adjustAndSetSize(this,
                setSize(380, 112);

       add( clazz = new ValueContainer("", "" )).left().uniform();
        row();
        add(  mstr = new ValueContainer("", "" )).left().uniform();
        row();
        add(  skill = new ValueContainer("", "" )).left().uniform();
        row();
        add(  spell = new ValueContainer("", "" )).left().uniform();

        clazz.setWidth(Align.left);
        mstr.setNameAlignment(Align.left);
        skill.setNameAlignment(Align.left);
        spell.setNameAlignment(Align.left);

        clazz.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        mstr.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        skill.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
        spell.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));

        clazz.setValueAlignment(Align.right);
        mstr.setValueAlignment(Align.right);
        skill.setValueAlignment(Align.right);
        spell.setValueAlignment(Align.right);

        clazz.setNameAlignment(Align.left);
        mstr.setNameAlignment(Align.left);
        skill.setNameAlignment(Align.left);
        spell.setNameAlignment(Align.left);

        clazz.setFixedMinSize(true);
        mstr.setFixedMinSize(true);
        skill.setFixedMinSize(true);
        spell.setFixedMinSize(true);

        clazz.setWidth(getWidth()*0.8f);
        mstr.setWidth(getWidth()*0.8f);
        skill.setWidth(getWidth()*0.8f);
        spell.setWidth(getWidth()*0.8f);

        clazz.setHeight(getHeight()*0.3f);
        mstr.setHeight(getHeight()*0.3f);
        skill.setHeight(getHeight()*0.3f);
        spell.setHeight(getHeight()*0.3f);
    }

    @Override
    protected void update(float delta) {
        clazz.setValueText("Class Rank: " + StringMaster.getCurOutOfMax(
                dataSource.getParamRounded(PARAMS.CLASS_RANKS_UNSPENT),
                dataSource.getParamRounded(PARAMS.CLASS_RANKS)));
        mstr.setValueText("Mastery Rank: " + StringMaster.getCurOutOfMax(
                dataSource.getParamRounded(PARAMS.MASTERY_RANKS_UNSPENT),
                dataSource.getParamRounded(PARAMS.MASTERY_RANKS)));
        skill.setValueText("Skill Points: " + StringMaster.getCurOutOfMax(
                dataSource.getParamRounded(PARAMS.SKILL_POINTS_UNSPENT),
                dataSource.getParamRounded(PARAMS.SKILL_POINTS )));
        spell.setValueText("Spell Points: " + StringMaster.getCurOutOfMax(
                dataSource.getParamRounded(PARAMS.SPELL_POINTS_UNSPENT),
                dataSource.getParamRounded(PARAMS.SPELL_POINTS )));
    }
}
