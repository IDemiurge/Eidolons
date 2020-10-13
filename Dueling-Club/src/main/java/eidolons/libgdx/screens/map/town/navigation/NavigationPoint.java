package eidolons.libgdx.screens.map.town.navigation;

import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
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
    SmartTextButton body;
    SmartTextButton action; //text?

    @Override
    public void act(float delta) {
        super.act(delta);
        //hide action
    }

    public NavigationPoint(Navigable navigable) {
        this.navigable = navigable;
        addActor(icon = new FadeImageContainer(navigable.getIconPath()));
        addActor(body = new SmartTextButton(navigable.getName(),
         STD_BUTTON.TAB_HIGHLIGHT, navigable::interact));
        //is disabled

    }


}
