package eidolons.libgdx.gui.panels.headquarters.tabs.inv;

import eidolons.libgdx.GDX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqInvTab extends HqElement{

    StashPanel stash;
    HqInvPanel inventory;


    public HqInvTab() {
        add(inventory = new HqInvPanel());

        setFixedSize(true);
        setSize(GDX.size(HqMaster.TAB_WIDTH) ,
         GDX.size(HqMaster.TAB_HEIGHT) );
    }

    @Override
    protected void update(float delta) {

    }
}
