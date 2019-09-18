package eidolons.libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.HashMap;
import java.util.Map;

public class ActionContainerFactory {

    public static final ActionContainerFactory instance = new ActionContainerFactory();
    private static Map<DC_ActiveObj, ActionValueContainer> cache = new HashMap<>();

    private ActionContainerFactory() {
        GuiEventManager.bind(GuiEventType. HIGHLIGHT_ACTION, p-> {
            getValueContainer((DC_ActiveObj) p.get(), 64) .setHighlight(true);
        });
        GuiEventManager.bind(GuiEventType. HIGHLIGHT_ACTION_OFF, p-> {
            getValueContainer((DC_ActiveObj) p.get(), 64) .setHighlight(false);
        });
    }

    public static ActionValueContainer getValueContainer(DC_ActiveObj el, int size) {
        ActionValueContainer container = cache.get(el);
        boolean valid = el.canBeManuallyActivated();
        boolean b=false;
        if (container != null) {
//            container.reset(getImage(el));
//            container.setValid(valid);
            b = container.isHighlighted();
//            if (b)
//                return container;
        }
            container = new ActionValueContainer(
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
        String image = el.getImagePath();
        //        if (el.can)
        return image;
    }
}
