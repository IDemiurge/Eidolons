package main.libgdx.gui.panels.dc.unitinfo.tooltips;

import main.content.PARAMS;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.Obj;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.system.images.ImageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static main.content.UNIT_INFO_PARAMS.*;
import static main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster.getIconPathForTableRow;
import static main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster.getStringForTableValue;
import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 8/10/2017.
 */
public class AttackTooltipFactory {
    public static ActionToolTip createAttackTooltip(DC_UnitAction el) {
        Pair<PARAMS, PARAMS> pair = ACTION_TOOLTIPS_PARAMS_MAP.get(ACTION_TOOLTIP_HEADER_KEY);
        String name = getStringForTableValue(ACTION_TOOLTIP_HEADER_KEY, el);
        final String leftImage = ActionTooltipMaster.getIconPathForTableRow(pair.getLeft());
        final String rightImage = ActionTooltipMaster.getIconPathForTableRow(pair.getRight());
        MultiValueContainer head = new MultiValueContainer(name, leftImage, rightImage);

        VALUE[] baseKeys = ACTION_TOOLTIP_BASE_KEYS;
        final List<MultiValueContainer> base =  extractActionValues(el, baseKeys);

        baseKeys = ACTION_TOOLTIP_RANGE_KEYS;
        final List<MultiValueContainer> range =  extractActionValues(el, baseKeys);

        List/*<List<MultiValueContainer>>*/ textsList = new ArrayList<>();
        for (PARAMS[] params : ACTION_TOOLTIP_PARAMS_TEXT) {
            textsList.add(Arrays.stream(params).map(p -> {
                 final String textForTableValue = ActionTooltipMaster.
                  getTextForTableValue(p, el);
                 if (StringUtils.isEmpty(textForTableValue)) {
                     return null;
                 } else {
                     return new ValueContainer(textForTableValue, "");
                 }
             }
            ).filter(Objects::nonNull).collect(Collectors.toList()));
        }

        ActionToolTip toolTip = new ActionToolTip();
        toolTip.setUserObject(new ActionTooltipSource() {
            @Override
            public MultiValueContainer getHead() {
                return head;
            }

            @Override
            public List<MultiValueContainer> getBase() {
                return base;
            }

            @Override
            public List<MultiValueContainer> getRange() {
                return range;
            }

            @Override
            public List<List<ValueContainer>> getText() {
                return textsList;
            }

            @Override
            public CostTableSource getCostsSource() {
                return () -> getActionCostList(el);
            }
        });
        return toolTip;
    }

    private static List<MultiValueContainer> extractActionValues(DC_UnitAction el
     , VALUE[] baseKeys) {
        List<MultiValueContainer> list = new ArrayList<>();
        Pair<PARAMS, PARAMS> pair;
        for (VALUE key : baseKeys) {
            pair = ACTION_TOOLTIPS_PARAMS_MAP.get(key);

            String name = getStringForTableValue(key, el);
            String imagePath = getIconPathForTableRow(key);
            final String leftVal = ActionTooltipMaster.getValueForTableParam(pair.getLeft(), el);
            final String rightVal = ActionTooltipMaster.getValueForTableParam(pair.getRight(), el);
            MultiValueContainer mvc;
            if (!ImageManager.isImage(imagePath)) {
                mvc = new MultiValueContainer(name, leftVal, rightVal);
            } else {
                mvc = new MultiValueContainer(getOrCreateR(imagePath), name, leftVal, rightVal);
            }
            list.add(mvc);
        }
        return list;
    }

    public static <T extends Obj> Function<T, ValueContainer> getObjValueContainerMapper() {
        return obj -> {
            final ValueContainer container = new ValueContainer(getOrCreateR(obj.getType().getProperty(G_PROPS.IMAGE)));

            ToolTip toolTip = new ValueTooltip();
            toolTip.setUserObject(Arrays.asList(new ValueContainer(obj.getName(), "")));
            container.addListener(toolTip.getController());

            return container;
        };
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
