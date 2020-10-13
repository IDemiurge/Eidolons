package eidolons.libgdx.gui.panels.dc.actionpanel.datasource;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ActionContainerFactory {

    public static final ActionContainerFactory instance = new ActionContainerFactory();
    private static final ObjectMap<DC_ActiveObj, ActionContainer> cache = new ObjectMap<>();

    private ActionContainerFactory() {
        GuiEventManager.bind(GuiEventType. HIGHLIGHT_ACTION, p-> {
            getValueContainer((DC_ActiveObj) p.get(), 64) .setHighlight(true);
        });
        GuiEventManager.bind(GuiEventType. HIGHLIGHT_ACTION_OFF, p-> {
            getValueContainer((DC_ActiveObj) p.get(), 64) .setHighlight(false);
        });
    }

    public static ActionContainer getValueContainer(DC_ActiveObj el, int size) {
        ActionContainer container = cache.get(el);
        boolean valid = el.canBeActivated();
        boolean b=false;
        if (container != null) {
//            container.reset(getImage(el));
//            container.setValid(valid);
            b = container.isHighlighted();
//            if (b)
//                return container;
        }
            container = new ActionContainer(
                    size, valid, getImage(el)
                    , el::invokeClicked);

        if (b) {
            container.setHighlight(b);
        }
            cache.put(el, container);

        container.setUserObject(el);

        ActionCostTooltip tooltip = new ActionCostTooltip(el);

        tooltip.addTo(container);
        return container;
    }

    private static String getImage(DC_ActiveObj el) {
        //        if (el.can)
        return el.getImagePath();
    }
}
