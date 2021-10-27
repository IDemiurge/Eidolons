package libgdx.gui.dungeon.panels.headquarters.tabs.inv;

import libgdx.GDX;
import libgdx.gui.dungeon.panels.headquarters.HqElement;
import libgdx.gui.dungeon.panels.headquarters.HqMaster;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqInvTab extends HqElement{

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
