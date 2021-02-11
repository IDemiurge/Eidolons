package libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.Color;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.future.FutureBuilder;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.DC_AttackMaster;
import eidolons.game.battlecraft.rules.combat.attack.SneakRule;
import eidolons.game.battlecraft.rules.perk.RangeRule;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.dc.actionpanel.datasource.ActionCostSourceImpl;
import libgdx.gui.panels.dc.logpanel.text.TextBuilder;
import libgdx.gui.panels.dc.unitinfo.old.MultiValueContainer;
import libgdx.gui.tooltips.Tooltip;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.texture.TextureCache;
import main.content.VALUE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.game.bf.directions.DirectionMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.text.TextWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static eidolons.content.values.UNIT_INFO_PARAMS.*;

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

    public static TablePanelX createCasesTable(Unit source, BattleFieldObject target) {
        TablePanelX table= new TablePanelX();
        table.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawableNoMinSize());

        Attack attack = DC_AttackMaster.getAttackFromAction(source.getAttackAction(false));
        Ref ref = source.getRef().getCopy();
        ref.setTarget(target.getId());
        for (AttackDataSource.ATTACK_CASE value : AttackDataSource.ATTACK_CASE.values()) {
            if (checkCase(value, ref)){
                ValueContainer container = createContainer(value);
                table.add(container).row();
            }

        }

        return table;
    }

    private static boolean checkCase(AttackDataSource.ATTACK_CASE value, Ref ref) {
        switch (value) {
            case SNEAK:
               return SneakRule.checkSneak(ref);
            case CLOSE_QUARTERS:
                return RangeRule.isCloseQuartersOrLongReach(ref);
            case LONG_REACH:
                return !RangeRule.isCloseQuartersOrLongReach(ref);
            case SIDEWAYS:
                return FacingMaster.getSingleFacing_(ref.getSourceObj(), ref.getTargetObj())== UnitEnums.FACING_SINGLE.TO_THE_SIDE;
            case DIAGONAL:
                return DirectionMaster.getRelativeDirection(ref.getSourceObj(), ref.getTargetObj()).isDiagonal();
        }
        return false;
    }

    private static ValueContainer createContainer(AttackDataSource.ATTACK_CASE value) {
        String pic="";
        String text = StringMaster.format(value.toString())+ " Attack";
        String tooltip=text + " modificators will apply";
        Color color = null;
        switch (value) {
            case SNEAK:
                tooltip="Target may have reduced defense";
                color = GdxColorMaster.LILAC;
                break;
            case CLOSE_QUARTERS:
                color = GdxColorMaster.RED;
                break;
            case LONG_REACH:
                color = GdxColorMaster.BLUE;
                break;
            case SIDEWAYS:
                color = GdxColorMaster.YELLOW;
                break;
            case DIAGONAL:
                color = GdxColorMaster.GREEN;
                break;
        }

        text = TextBuilder.wrapInColor(color, text);
        ValueContainer container = new ValueContainer(text, pic);

        container.addListener(new ValueTooltip(tooltip).getController());
        return container;
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
        return new ValueContainer(info, tooltip);
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

            @Override
            public String getDescription() {
                return el.getDescription();
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

            Tooltip tooltip = createBuffTooltip(obj);
            container.addListener(tooltip.getController());
            return container;
        };
    }

    public static <T extends Obj> Tooltip createBuffTooltip(Entity obj) {
        ValueTooltip tooltip = new ValueTooltip();
        String descr = getDescriptionForBuff(obj);

        tooltip.setUserObject(Collections.singletonList(new ValueContainer(obj.getName() + Strings.NEW_LINE + descr)));
        tooltip.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        return tooltip;
    }

    private static  String getDescriptionForBuff(Entity obj) {
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
