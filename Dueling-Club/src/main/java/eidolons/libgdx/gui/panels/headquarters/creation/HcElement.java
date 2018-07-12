package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.libgdx.gui.panels.headquarters.HqElement;

/**
 * Created by JustMe on 7/4/2018.
 */
public abstract class HcElement extends HqElement {

    public HcElement() {
        super(HeroCreationWorkspace.SELECTION_WIDTH, HeroCreationWorkspace.SELECTION_HEIGHT);
    }
    @Override
    protected void update(float delta) {

    }
}
