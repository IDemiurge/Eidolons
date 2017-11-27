package main.libgdx.gui.panels.dc.actionpanel.datasource;

import main.content.values.parameters.PARAMETER;
import main.entity.active.DC_ActiveObj;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.system.images.ImageManager;
import main.system.text.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static main.content.UNIT_INFO_PARAMS.*;
import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 9/20/2017.
 */
public class ActionCostSourceImpl implements ActionCostSource {
    private final DC_ActiveObj action;

    public ActionCostSourceImpl(DC_ActiveObj action) {
        this.action = action;
    }
        @Override
        public ValueContainer getDescription() {
        return new ValueContainer(
         TextWrapper.wrapWithNewLine(action.getDescription(), 60) , "");
    }

        @Override
        public ValueContainer getName() {
        return new ValueContainer(action.getName(), "");
    }

        @Override
        public List<ValueContainer> getCostsList() {
        return  getActionCostList(action);
    }
    public static List<ValueContainer> getActionCostList(DC_ActiveObj el) {
        List<ValueContainer> costsList = new ArrayList<>();
        for (int i = 0, costsLength = RESOURCE_COSTS.length; i < costsLength; i++) {
            PARAMETER cost = RESOURCE_COSTS[i];
            final double param = el.getParamDouble(cost);
            if (param > 0) {
                final String iconPath = ImageManager.getValueIconPath(COSTS_ICON_PARAMS[i]);
                costsList.add(new ValueContainer(getOrCreateR(iconPath), String.format(Locale.US, "%.1f", param)));
            }
        }

        final double reqRes = el.getParamDouble(MIN_REQ_RES_FOR_USE.getLeft());
        if (reqRes > 0) {
            final String iconPath = ImageManager.getValueIconPath(MIN_REQ_RES_FOR_USE.getRight());
            costsList.add(new ValueContainer(getOrCreateR(iconPath), String.format(Locale.US, "> %.1f", reqRes)));
        }
        return costsList;
    }

}
