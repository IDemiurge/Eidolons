package main.libgdx.screens.map.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.game.module.adventure.entity.MacroAction;
import main.game.module.adventure.entity.MacroActionManager;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.gui.map.MacroAP_Holder.MACRO_ACTION_GROUPS;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import main.libgdx.gui.panels.dc.actionpanel.BaseSlotPanel;
import main.libgdx.texture.TextureCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapActionPanel extends BaseSlotPanel {

    public MapActionPanel( ) {
        super(64);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final MacroParty source = (MacroParty) getUserObject();

        final List<ActionValueContainer> sources = getActions(source);
        initContainer(sources, "UI/EMPTY_LIST_ITEM.jpg");
    }

    private List<ActionValueContainer> getActions(MacroParty source) {
        //cache
        List<MacroAction> actions = MacroActionManager.getMacroActions(
         MACRO_ACTION_GROUPS.PARTY, source);
     List<ActionValueContainer> list = new ArrayList<>();
        for (MacroAction sub : actions) {
            boolean valid=true;
            TextureRegion texture= TextureCache.getOrCreateR(sub.getImagePath());
            list.add(new ActionValueContainer(valid, texture, ()->{
                sub.invokeClicked();
            }));
        }
        return list;
    }
}
