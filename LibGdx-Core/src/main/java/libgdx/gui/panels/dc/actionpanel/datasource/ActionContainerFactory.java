package libgdx.gui.panels.dc.actionpanel.datasource;

import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.spaces.Feat;
import libgdx.gui.panels.dc.actionpanel.spaces.ActionContainer;
import libgdx.gui.panels.dc.actionpanel.spaces.FeatContainer;
import libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class ActionContainerFactory {

    public static final ActionContainerFactory instance = new ActionContainerFactory();
    private static final ObjectMap<Feat, ActionContainer> cache = new ObjectMap<>();

    private ActionContainerFactory() {
        GuiEventManager.bind(GuiEventType.HIGHLIGHT_ACTION, p -> {
            getValueContainer((DC_ActiveObj) p.get(), 64).setHighlight(true);
        });
        GuiEventManager.bind(GuiEventType.HIGHLIGHT_ACTION_OFF, p -> {
            getValueContainer((DC_ActiveObj) p.get(), 64).setHighlight(false);
        });
    }

    public static FeatContainer getValueContainer(Feat el, int size) {
        ActionContainer container = cache.get(el);
        boolean valid = el.canBeActivated();
        boolean highlighted = false;
        if (container != null) {
            //            container.reset(getImage(el));
            //            container.setValid(valid);
            highlighted = container.isHighlighted();
            //            if (b)
            //                return container;
        }
        if (el.isActive()) {
            container = new ActionContainer(()-> el.getCharges(),
                    size, valid, getImage(el)
                    , el::invokeClicked);
            ActionCostTooltip tooltip = new ActionCostTooltip(el.getActive());
            tooltip.addTo(container);
        } else {

            //TODO NF Rules - Passives
        }
        if (highlighted) {
            container.setHighlight(true);
        }
        cache.put(el, container);

        container.setUserObject(el);

        return container;
    }

    private static String getImage(Feat el) {
        //        if (el.can)
        return el.getImagePath();
    }
}
