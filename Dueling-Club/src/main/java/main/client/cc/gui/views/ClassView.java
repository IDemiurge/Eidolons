package main.client.cc.gui.views;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.OrConditions;
import main.elements.conditions.StringComparison;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager.BORDER;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassView extends HeroItemView {

    public static final String MULTICLASS = HeroEnums.CLASS_GROUP.MULTICLASS.toString();
    private Map<String, List<ObjType>> additionalTypesMap;

    public ClassView(Unit hero) {
        super(hero, true, true);
        // init();
    }

    public static boolean isMulticlass(Entity type) {
        return type.getProperty(G_PROPS.CLASS_GROUP).equalsIgnoreCase(
                HeroEnums.CLASS_GROUP.MULTICLASS.toString());
    }

    @Override
    public void activate() {

    }

    protected PARAMETER getSortingParam() {
        return PARAMS.XP_COST;
    }

    @Override
    public PARAMS getPoolParam() {
        return PARAMS.XP;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.CLASSES;
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.CLASSES;
    }

    @Override
    public BORDER getBorder(ObjType value) {

        if (StringMaster.checkContainer(hero.getProperty(getPROP()), value.getName(), true)) {
            return BORDER.SILVER_64;// TODO CIRCLE CHECK!
        }
        String r = hero.getGame().getRequirementsManager().check(hero, value);
        if (r == null) {
            return null;
        }

        return BORDER.HIDDEN;
    }

    @Override
    protected Filter<ObjType> getSpecialTypeFilter() {
        return new Filter<>(hero.getRef(), new NotCondition(new StringComparison("{Match_}"
                + G_PROPS.CLASS_GROUP.getName(), MULTICLASS, true)));
    }

    protected boolean isSpecial(String name) {
        return name.equalsIgnoreCase(MULTICLASS);
    }

    @Override
    protected List<ObjType> getAdditionalTypesData() {
        String group = vendorPanel.getSelectedTabName();
        List<ObjType> list = getAdditionalTypesMap().get(group);

        if (list != null) {
            return list;
        }
        String types = StringMaster.joinStringList(DataManager.getTypeNamesGroup(getTYPE(), group),
                ";");

        Filter<ObjType> filter = new Filter<>(hero.getRef(), new Conditions(new StringComparison(
                "{Match_}" + G_PROPS.CLASS_GROUP, MULTICLASS, true), new OrConditions(
                new Conditions(
                        // new EmptyStringCondition("{Match_}"
                        // + PROPS.BASE_CLASSES_ONE),
                        // new EmptyStringCondition("{Match_}"
                        // + PROPS.BASE_CLASSES_TWO)
                        // ,
                        // new StringComparison("{Match_}" + G_PROPS.CLASS_TYPE,
                        // group, true)
                        // ,
                        new StringComparison(group, "{Match_}" + G_PROPS.BASE_TYPE, false)),

                new StringComparison("{Match_}" + PROPS.BASE_CLASSES_ONE, types, false),
                new StringComparison("{Match_}" + PROPS.BASE_CLASSES_TWO, types, false))),
                DC_TYPE.CLASSES);
        list = new LinkedList<>(filter.getTypes());
        getAdditionalTypesMap().put(vendorPanel.getSelectedTabName(), list);
        return list;

    }

    public Map<String, List<ObjType>> getAdditionalTypesMap() {
        if (additionalTypesMap == null) {
            additionalTypesMap = new HashMap<>();
        }
        return additionalTypesMap;
    }

    public void setAdditionalTypesMap(Map<String, List<ObjType>> additionalTypesMap) {
        this.additionalTypesMap = additionalTypesMap;
    }

}
