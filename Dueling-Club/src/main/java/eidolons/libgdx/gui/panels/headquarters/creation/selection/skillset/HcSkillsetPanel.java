package eidolons.libgdx.gui.panels.headquarters.creation.selection.skillset;

import eidolons.content.PARAMS;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationWorkspace;

/**
 * Created by JustMe on 7/2/2018.
 */
public class HcSkillsetPanel extends HqElement {

    private final LabelX xp;
    HcSkillsetTabs tabs;

    public HcSkillsetPanel() {
        setSize(HeroCreationWorkspace.SELECTION_WIDTH, HeroCreationWorkspace.SELECTION_HEIGHT - 100);
        add(xp = new LabelX("", 20)).center(). row();
        add(tabs = new HcSkillsetTabs());
    }

    private String getText() {
        return "Experience Points: " + getUserObject().getIntParam(PARAMS.XP);
    }

    @Override
    protected void update(float delta) {
        xp.setText(getText());
    }
}