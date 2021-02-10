package libgdx.screens.map.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.dc.actionpanel.ActionContainer;
import libgdx.gui.panels.dc.actionpanel.BaseSlotPanel;
import libgdx.texture.TextureCache;
import eidolons.macro.entity.action.MACRO_ACTION_GROUPS;
import eidolons.macro.entity.action.MacroAction;
import eidolons.macro.entity.action.MacroActionManager;
import eidolons.macro.entity.party.MacroParty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/9/2018.
 */
public class MapActionPanel extends BaseSlotPanel {

    public MapActionPanel() {
        super(64);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final MacroParty source = (MacroParty) getUserObject();

        final List<ValueContainer> sources = getActions(source);
        initContainer(sources, "ui/empty_list_item.jpg");
    }

    private List<ValueContainer> getActions(MacroParty source) {
        //cache
        List<MacroAction> actions = MacroActionManager.getMacroActions(
         MACRO_ACTION_GROUPS.PARTY, source);
        List<ValueContainer> list = new ArrayList<>();
        for (MacroAction sub : actions) {
            boolean valid = true;
            TextureRegion texture = TextureCache.getOrCreateR(sub.getImagePath());
            list.add(new ActionContainer(valid, texture, sub::invokeClicked));
        }
        return list;
    }
}
