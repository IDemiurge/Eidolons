package eidolons.libgdx.gui.panels.headquarters.creation.skillset;

import eidolons.libgdx.gui.panels.headquarters.HqElement;

/**
 * Created by JustMe on 7/2/2018.
 */
public class HcSkillsetPanel extends HqElement {

    HcSkillsetTabs tabs;

    public HcSkillsetPanel( ) {

        add(tabs= new HcSkillsetTabs());
    }

    @Override
    protected void update(float delta) {

    }
}
