package main.system;

import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.swing.listeners.ListChooserSortOptionListener.SORT_TEMPLATE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class SortMaster {

    private static Comparator<? super Entity> idSorter;

    public static List<? extends Entity> sortByValue(List<? extends Entity> listData,
                                                     VALUE sortValue) {
        return sortByValue(listData, (PARAMETER) sortValue, true);
    }

    public static List<? extends Entity> sortByValue(List<? extends Entity> pool, PARAMETER p,
                                                     boolean descending) {
        if (ListMaster.isNotEmpty(pool)) {
            Collections.sort(pool, getSorter(p, descending));
            // OBJ_TYPE TYPE = pool.get(0).getOBJ_TYPE_ENUM();
            // List<String> types = sortByValue(DataManager.toStringList(pool),
            // p, TYPE, descending);
            // return DataManager.toTypeList(types, TYPE);
        }
        return pool;
    }

    public static List<String> sortByValue(List<String> listData, VALUE sortValue, OBJ_TYPE TYPE,
                                           boolean descending) {
        List<ObjType> data = DataManager.toTypeList(listData, TYPE);
        Collections.sort(data, getSorter(sortValue, descending));
        listData = DataManager.toStringList(data);
        return listData;
    }

    public static List<String> sortByValue(List<String> listData, VALUE sortValue, OBJ_TYPE TYPE) {
        return sortByValue(listData, sortValue, TYPE, false);
    }

    public static void sortById(List<? extends Entity> data) {
        Collections.sort(data, getIdSorter());
    }

    public static Comparator<? super Entity> getIdSorter() {
        return getSorter(G_PROPS.ID, true);
    }

    public static Comparator<Integer> getNaturalIntegerComparator(final boolean descending) {
        return new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 > o2) {
                    return descending ? -1 : 1;
                }
                return !descending ? -1 : 1;

            }

        };
    }

    public static Comparator<? super Entity> getHeroBackgroundSorter() {
        return getSublistSorter(BACKGROUND.class);
    }

    public static Comparator<? super Entity> getSublistSorter(final Class<?> enumClass) {
        return new Comparator<Entity>() {
            protected int checkHeroSubList(Entity o1, Entity o2) {

                String prop = StringMaster.getWellFormattedString(enumClass.getName());
                Object e1 = EnumMaster.getEnumConst(enumClass, o1.getProp(prop));
                Object e2 = EnumMaster.getEnumConst(enumClass, o2.getProp(prop));

                return new EnumMaster<>().getEnumSorter(enumClass).compare(e1, e2);
            }

            public int compare(Entity o1, Entity o2) {
                /*
                 * get background type from bg prop OR just race get id -> sort
				 * into sublists by id sort sublists by name
				 */
                int result = checkHeroSubList(o1, o2);
                if (result != 0) {
                    return result;
                }
                return compareAlphabetically(o1.getName(), o2.getName());
            }

        };
    }

    public static Comparator<? super Entity> getSorter(final VALUE p, final Boolean descending) {
        return (Comparator<Entity>) (o1, o2) -> compareValue(p, descending, o1, o2);

    }

    public static Comparator<? super String> getSorterString(final VALUE p,final OBJ_TYPE TYPE, final Boolean descending) {
        return (Comparator<String>) (o1, o2) -> compareValue(p, descending, DataManager.getType(o1, TYPE), DataManager.getType(o2, TYPE));

    }
    //        public static Comparator<? super Entity> getSorterByNaturalOrder
//        (Function<Entity, Integer> function) {
//        return Comparator.naturalOrder(p -> function.apply(p));
//    }
    public static Comparator<? super Entity> getSorterByExpression
            (Function<Entity, Integer> function) {
        return Comparator.comparingInt(o -> function.apply((Entity) o)).reversed();

    }

    public static Comparator<? super Obj> getSorterByExpressionObj
            (Function<Obj, Integer> function) {
        return Comparator.comparingInt(o -> function.apply(o));

    }

    public static Comparator<? super Entity> getEntitySorter(SORT_TEMPLATE t, OBJ_TYPE TYPE) {
        switch (t) {
            case ALPHABETIC:
                return getAlphabeticSorter();
            case BY_ID:
                return getIdSorter();
            case GROUP:
                return getSublistSorter(EnumMaster.getEnumClass(TYPE.getGroupingKey().name()));
            case SUBGROUP:
                return getSublistSorter(EnumMaster.getEnumClass(TYPE.getSubGroupingKey().name()));
        }
        return null;
    }

    public static Comparator<? super Entity> getAlphabeticSorter() {
        return getSorter(G_PROPS.NAME, false);
    }

    public static Comparator<? super String> getSorter(SORT_TEMPLATE t) {
        // getIdSorter()
        return null;
    }

    protected static int compareAlphabetically(String id1, String id2) {
        return id1.compareTo(id2);
    }

    public static int compareValue(final VALUE p, final Boolean descending, String s1, String s2,
                                   OBJ_TYPE TYPE) {
        ObjType o1 = DataManager.getType(s1, TYPE);
        ObjType o2 = DataManager.getType(s2, TYPE);
        return compareValue(p, descending, o1, o2);
    }

    public static int compareValue(final VALUE p, final Boolean descending, Entity o1, Entity o2) {
        String id1 = o1.getValue(p);
        String id2 = o2.getValue(p);

        if (StringMaster.isInteger(id1)) {
            if (StringMaster.getInteger(id1) > StringMaster.getInteger(id2)) {
                int i = descending ? -1 : 1;
                return i;
            }
            if (StringMaster.getInteger(id1) == StringMaster.getInteger(id2)) {
                return 0;
            }
            return descending ? 1 : -1;
        }
        if (descending) {
            return compareAlphabetically(id2, id1);
        }
        return compareAlphabetically(id1, id2);
    }

    public static int compare(Entity o1, Entity o2, PROPERTY p, String value, boolean negative) {
        int i = 1;
        if (negative) {
            i = -1;
        }
        if (o1.checkProperty(p, value)) {
            if (!o2.checkProperty(p, value)) {
                return i;
            }
        }
        if (o2.checkProperty(p, value)) {
            if (!o1.checkProperty(p, value)) {
                return -i;
            }
        }
        return 0;
    }

    public static int compare(Entity o1, Entity o2, PARAMETER p) {
        if (o1.getIntParam(p) == o2.getIntParam(p)) {
            return 0;
        }
        if (o1.getIntParam(p) > o2.getIntParam(p)) {
            return 1;
        }
        return -1;
    }

}
