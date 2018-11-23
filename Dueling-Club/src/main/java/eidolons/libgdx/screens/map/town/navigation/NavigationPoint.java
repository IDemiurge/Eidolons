package eidolons.libgdx.screens.map.town.navigation;

import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.screens.map.town.navigation.data.Navigable;

/**
 * Created by JustMe on 11/21/2018.
 can be:
 Place (in town - shop, library, ..
 Dungeon entrance
 NPC

 so it's a wrapper
 all we need is impl. the 'interact'
 */
public class NavigationPoint extends HorizontalFlowGroup{
    Navigable navigable;
FadeImageContainer icon;
SmartButton body;

    public NavigationPoint(Navigable navigable) {
        this.navigable = navigable;
        addActor(icon = new FadeImageContainer(navigable.getIconPath()));
        addActor(body = new SmartButton(navigable.getName(), STD_BUTTON.TAB_HIGHLIGHT, () -> navigable.interact()));
        //is disabled

    }


}
