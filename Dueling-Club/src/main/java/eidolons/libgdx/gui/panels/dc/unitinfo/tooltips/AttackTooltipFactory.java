package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.rules.buff.MoraleBuffRule;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSourceImpl;
import eidolons.libgdx.gui.panels.dc.unitinfo.old.MultiValueContainer;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.VALUE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.text.TextWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eidolons.content.UNIT_INFO_PARAMS.*;

/**
 * Created by JustMe on 8/10/2017.
 */
public class AttackTooltipFactory {

    public static AttackTooltip createAttackTooltip(DC_UnitAction el) {
        return createAttackTooltip(el, false, true, true, false, null);
    }

    public static AttackTooltip createAttackTooltip(DC_UnitAction activeObj, DC_Obj target) {
        return createAttackTooltip(activeObj, true, true, true, false, target);
    }

    private static ValueContainer createPrecalcRow(boolean precalc, DC_UnitAction el, DC_Obj target) {
        if (!precalc) {
            return null;
        }
        int min_damage = FutureBuilder.precalculateDamage(el, target, true, true);
        int max_damage = FutureBuilder.precalculateDamage(el, target, true, false);

        LogMaster.log(1, el.getName() + " on " + target
         + " - damage precalculated: " + min_damage + " - " + max_damage);
        DAMAGE_TYPE dmgType = el.getDamageType();
//        TODO display all bonus damage!
//          Attack attack = EffectFinder.getAttackFromAction(el);
//        DamageFactory.getDamageFromAttack();
        String info = min_damage + "-" + max_damage + dmgType.getName() + " damage";
        String tooltip = "";
//        if ()
//            DamageCalculator.isUnconscious()
//        "(will drop)";
//        tooltip +=" (shield!)";
        ValueContainer container = new ValueContainer(info, tooltip);
        return container;
    }

    public static AttackTooltip createAttackTooltip(DC_UnitAction el,
                                                    boolean precalc, boolean costs,
                                                    boolean additionalInfo, boolean combatMode, DC_Obj target) {
        Pair<PARAMS, PARAMS> pair = ACTION_TOOLTIPS_PARAMS_MAP.get(ACTION_TOOLTIP_HEADER_KEY);
        String name = ActionTooltipMaster.getStringForTableValue(ACTION_TOOLTIP_HEADER_KEY, el);
        final String leftImage = ActionTooltipMaster.getIconPathForTableRow(pair.getLeft());
        final String rightImage = ActionTooltipMaster.getIconPathForTableRow(pair.getRight());
        MultiValueContainer head = new MultiValueContainer(name, leftImage, rightImage);

        VALUE[] baseKeys = ACTION_TOOLTIP_BASE_KEYS;
        final List<MultiValueContainer> base = extractActionValues(el, baseKeys);

        baseKeys = ACTION_TOOLTIP_RANGE_KEYS;
        final List<MultiValueContainer> range = extractActionValues(el, baseKeys);

        List/*<List<MultiValueContainer>>*/ textsList = new ArrayList<>();
        for (PARAMS[] params : ACTION_TOOLTIP_PARAMS_TEXT) {
            textsList.add(Arrays.stream(params).map(p -> {
                 String textForTableValue = ActionTooltipMaster.
                  getTextForTableValue(p, el);
                 textForTableValue =
                  TextWrapper.wrapWithNewLine(textForTableValue, 50);
                 if (StringUtils.isEmpty(textForTableValue)) {
                     return null;
                 } else {
                     return new MultiValueContainer(textForTableValue, "");
                 }
             }
            ).filter(Objects::nonNull).collect(Collectors.toList()));
        }

        AttackTooltip toolTip = new AttackTooltip(el);

        ValueContainer precalcRow = createPrecalcRow(precalc, el, target);
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
                return () ->
                 ActionCostSourceImpl.getActionCostList(el);
            }

            @Override
            public ValueContainer getPrecalcRow() {
                return precalcRow;
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

            String name = ActionTooltipMaster.getStringForTableValue(key, el);
            String imagePath = ActionTooltipMaster.getIconPathForTableRow(key);
            final String leftVal = ActionTooltipMaster.getValueForTableParam(pair.getLeft(), el);
            final String rightVal = ActionTooltipMaster.getValueForTableParam(pair.getRight(), el);
            MultiValueContainer mvc;
            if (!ImageManager.isImage(imagePath)) {
                mvc = new MultiValueContainer(name, leftVal, rightVal);
            } else {
                mvc = new MultiValueContainer(TextureCache.getOrCreateR(imagePath), name, leftVal, rightVal);
            }
            list.add(mvc);
        }
        return list;
    }

    public static <T extends Obj> Function<T, ValueContainer> getObjValueContainerMapper() {
        return obj -> {
            final ValueContainer container = new ValueContainer(TextureCache.getOrCreateR(obj.getType().getProperty(G_PROPS.IMAGE)));

            Tooltip tooltip = new ValueTooltip();
            String descr = getDescriptionForBuff(obj);

            tooltip.setUserObject(Arrays.asList(new VerticalValueContainer(obj.getName(), descr)));
            container.addListener(tooltip.getController());
            tooltip.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
            return container;
        };
    }

    private static <T extends Obj> String getDescriptionForBuff(T obj) {
        if (obj instanceof BuffObj) {
            if (((BuffObj) obj).isDynamic()) {
                return obj.getDescription();
            }
        }
        switch (obj.getName()) {
            case "Inspired":
                break;
            case "":
                return obj.getDescription();
        }
        return "";
    }


}
