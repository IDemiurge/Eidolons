package eidolons.libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.atb.AtbController;
import eidolons.game.core.atb.AtbMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.content.values.parameters.PARAMETER;
import main.system.images.ImageManager;
import main.system.math.MathMaster;
import main.system.text.TextWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static eidolons.content.UNIT_INFO_PARAMS.*;
import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 9/20/2017.
 */
public class ActionCostSourceImpl implements ActionCostSource {
    private final DC_ActiveObj action;

    public ActionCostSourceImpl(DC_ActiveObj action) {
        this.action = action;
    }

    public static List<ValueContainer> getActionCostList(DC_ActiveObj activeObj) {
        List<ValueContainer> costsList = new ArrayList<>();
        for (int i = 0, costsLength = RESOURCE_COSTS.length; i < costsLength; i++) {
            PARAMETER cost = RESOURCE_COSTS[i];
            final double param = activeObj.getParamDouble(cost);
            String text = String.format(Locale.US, "%.1f", param);
            if (cost == PARAMS.AP_COST) {
                if (DC_Engine.isAtbMode()) {
                    text =   AtbMaster.getDisplayedReadinessCost(activeObj) + "%";
                }
            }
            if (param > 0) {
                final String iconPath = ImageManager.getValueIconPath(COSTS_ICON_PARAMS[i]);
                costsList.add(new ValueContainer(getOrCreateR(iconPath), text));
            }
        }

        final double reqRes = activeObj.getParamDouble(MIN_REQ_RES_FOR_USE.getLeft());
        if (reqRes > 0) {
            final String iconPath = ImageManager.getValueIconPath(MIN_REQ_RES_FOR_USE.getRight());
            costsList.add(new ValueContainer(getOrCreateR(iconPath), String.format(Locale.US, "> %.1f", reqRes)));
        }
        return costsList;
    }

    @Override
    public ValueContainer getDescription() {
        return new ValueContainer(
         TextWrapper.wrapWithNewLine(action.getDescription(), 60), "");
    }

    @Override
    public ValueContainer getName() {
        return new ValueContainer(action.getName(), "");
    }

    @Override
    public List<ValueContainer> getCostsList() {
        return getActionCostList(action);
    }

}
