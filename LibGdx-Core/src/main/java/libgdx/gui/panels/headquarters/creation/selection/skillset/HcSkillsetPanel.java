package libgdx.gui.panels.headquarters.creation.selection.skillset;

import eidolons.content.PARAMS;
import libgdx.gui.LabelX;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.creation.HeroCreationWorkspace;

/**
 * Created by JustMe on 7/2/2018.
 */
public class HcSkillsetPanel extends HqElement {

    private final LabelX xp;
    HcSkillsetTabs tabs;
    boolean spell;

    public HcSkillsetPanel() {
        setSize(HeroCreationWorkspace.SELECTION_WIDTH, HeroCreationWorkspace.SELECTION_HEIGHT - 100);
        add(xp = new LabelX("", 20)).center().row();
        add(tabs = new HcSkillsetTabs());
    }

    @Override
    public void layout() {
        super.layout();
        tabs.setX(tabs.getX() + 10);
    }

    private String getText() {
        return "Points: " + getUserObject().getIntParam(
                spell ?
                        PARAMS.SKILL_POINTS_UNSPENT :
                        PARAMS.SPELL_POINTS_UNSPENT);
    }

    @Override
    protected void update(float delta) {
        xp.setText(getText());
    }
}
