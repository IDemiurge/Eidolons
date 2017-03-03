package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.choice.utility.FilterOptionListener;
import main.client.cc.gui.neo.choice.utility.SortOptionListener;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.swing.components.PagedOptionsComp;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.datatypes.DequeImpl;
import main.system.entity.ConditionMaster;

import java.util.*;

public abstract class EntityChoiceView extends ChoiceView<ObjType> {

    private Comparator<? super ObjType> sorter;
    private List<? extends Entity> list;

    public EntityChoiceView(ChoiceSequence choiceSequence, Unit hero) {
        super(choiceSequence, hero);
    }

    public EntityChoiceView(ChoiceSequence choiceSequence, Unit hero,
                            List<? extends Entity> entities) {
        super(choiceSequence, hero);
        list = entities;
    }

    public EntityChoiceView(ChoiceSequence choiceSequence, Unit hero, Entity... entities) {
        super(choiceSequence, hero);
        list = Arrays.asList(entities);
    }

    @Override
    public boolean isInfoPanelNeeded() {
        return true;
    }

    @Override
    protected void applyChoice() {
        if (isSaveHero()) {
            CharacterCreator.getHeroManager().saveHero(hero);
        }
        if (getPROP() != null) {
            hero.setProperty(getPROP(), ((Entity) data.get(getSelectedIndex())).getName(), true);
        }
    }

    protected boolean isSaveHero() {
        return false;
    }

    protected abstract PROPERTY getPROP();

    protected void initData() {
        if (list != null) { // empty?
            if (list.get(0) instanceof ObjType) {
                data = new LinkedList<>(new DequeImpl<ObjType>().getAddAllCast(list));
            } else {
                // TODO
            }
        } else {

            if (getGroup() != null) {
                data = DataManager.getFilteredTypes(getGroup(), getTYPE(), getFilterValue());
            } else {
                data = DataManager.getTypes(getTYPE());
            }
            Ref ref = new Ref(DC_Game.game);
            if (hero != null) {
                ref = Ref.getCopy(hero.getRef());
            }
            if (getFilterOption() != null) {
                WORKSPACE_GROUP ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumConst(
                        WORKSPACE_GROUP.class, getFilterOption());
                data = new Filter<ObjType>(ref, ConditionMaster.getWorkspaceCondition(true, ws))
                        .filter(data);
            }
            if (getFilterConditions() != null) {
                data = new Filter<ObjType>(ref, getFilterConditions()).filter(data);
            }
        }
        sortData();

    }

    protected void addSortOptionComp() {
        sortOptionsComp = new PagedOptionsComp<>("Sort by: ", SORT_TEMPLATE.class);
        sortOptionsComp.addListener(new SortOptionListener());
        add(sortOptionsComp, "id sortOptionsComp, pos ip.x ip.y2");
        sortOptionsComp.refresh();
    }

    protected void addFilterOptionComp() {
        Class<?> filterOptionClass = getFilterOptionClass();
        if (filterOptionClass != null) {
            filterOptionsComp = new PagedOptionsComp<>("Filter: ", filterOptionClass);
            filterOptionsComp.refresh();
            add(filterOptionsComp, "id filterOptionsComp, pos ip.x sortOptionsComp.y2+12");
            addFilterOptionListener();

        }
    }

    @Override
    protected void addFilterOptionListener() {
        filterOptionsComp.addListener(new FilterOptionListener<WORKSPACE_GROUP>());
    }

    @Override
    protected Class<?> getFilterOptionClass() {
        return WORKSPACE_GROUP.class;
    }

    protected void sortData() {
        if (getSorterOption() != null) {
            SORT_TEMPLATE t = new EnumMaster<SORT_TEMPLATE>().retrieveEnumConst(
                    SORT_TEMPLATE.class, getSorterOption());
            Collections.sort(data, SortMaster.getEntitySorter(t, getTYPE()));

        }
        if (getSorter() != null) {
            Collections.sort(data, getSorter());
            return;
        }

        if (DataManager.isIdSorted(getTYPE())) {
            SortMaster.sortById(data);
        }
    }

    protected Comparator<? super ObjType> getSorter() {
        return sorter;
    }

    protected Condition getFilterConditions() {
        return null;
    }

    protected abstract VALUE getFilterValue();

    protected String getGroup() {
        return null;
    }

    protected abstract OBJ_TYPE getTYPE();

}
