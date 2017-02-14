package main.client.cc.gui.views;

import main.client.cc.gui.lists.ShopListsPanel;
import main.client.cc.gui.lists.VendorListsPanel;
import main.content.CONTENT_CONSTS.QUALITY_LEVEL;
import main.content.*;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.elements.conditions.*;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.system.ConditionMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.util.Comparator;
import java.util.List;

public class ShopView extends HeroItemView {
    public static final QUALITY_LEVEL[] DEFAULT_QUALITY_LEVELS = {
            QUALITY_LEVEL.INFERIOR, QUALITY_LEVEL.NORMAL,
            QUALITY_LEVEL.SUPERIOR};
    private QUALITY_LEVEL[] qualityLevels;

    public ShopView(DC_HeroObj hero) {
        this(hero, true, true);
    }

    public ShopView(DC_HeroObj hero, boolean responsive, boolean showAll) {
        super(hero, responsive, showAll);
        setQualityLevels(DEFAULT_QUALITY_LEVELS);
    }

    protected VendorListsPanel generatePanel() {
        return new ShopListsPanel(hero, getTYPE(), getPROP(), isResponsive(),
                isShowAll(), getItemManager(), getTypeFilter()) {

            protected boolean checkSpecial(String name) {
                return isSpecial(name);
            }

            protected List<ObjType> getSpecialData() {
                return getAdditionalTypesData();
            }

            public Comparator<? super ObjType> getSorter() {
                return getItemSorter();

            }
        };
    }

    protected PARAMETER getSortingParam() {
        return PARAMS.GOLD_COST;
    }

    @Override
    public PARAMS getPoolParam() {
        return PARAMS.GOLD;
    }

    @Override
    public BORDER getBorder(ObjType value) {
        String r = hero.getGame().getRequirementsManager().check(hero, value);
        if (r == null) {
            return null;
        }

        // if (r.contains(InfoMaster.ITEM_REASON_BLOCKED)) {
        // // return BORDER.BLOCKED;
        // }

        return BORDER.HIDDEN;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return C_OBJ_TYPE.ITEMS;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.INVENTORY;
    }

    @Override
    public void activate() {

    }

    @Override
    protected Condition getFilterConditions() {
        // non-enchanted jewelry permitted to enchant manually?
        // new NotCondition(
        // new StringComparison(StringMaster.getValueRef
        // (KEYS.MATCH, PROPS.MAGICAL_ITEM_TRAIT), "", true));

        OrConditions specialConditions = new OrConditions(
                ConditionMaster.getTYPECondition(OBJ_TYPES.JEWELRY),
                new Conditions(new NotCondition(new StringComparison("{MATCH_"
                        + G_PROPS.QUALITY_LEVEL + "}", "", true)),
                        ConditionMaster.getTYPECondition(OBJ_TYPES.ITEMS)));

        Conditions conditions = new Conditions(new NotCondition(
                new StringComparison("{MATCH_" + G_PROPS.MATERIAL + "}", "",
                        true)));
        Conditions qualityConditions = new OrConditions();
        for (QUALITY_LEVEL quality : getQualityLevels()) {
            qualityConditions.add(new StringComparison(StringMaster
                    .getValueRef(KEYS.MATCH, G_PROPS.QUALITY_LEVEL),
                    StringMaster.getWellFormattedString(quality.toString()),
                    true));
        }
        conditions.add(qualityConditions);
        return new OrConditions(specialConditions, conditions);
    }

    public QUALITY_LEVEL[] getQualityLevels() {
        if (qualityLevels == null) {
            return DEFAULT_QUALITY_LEVELS;
        }
        return qualityLevels;
    }

    public void setQualityLevels(QUALITY_LEVEL[] qualityLevels) {
        this.qualityLevels = qualityLevels;
    }
}
