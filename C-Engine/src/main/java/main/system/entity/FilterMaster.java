package main.system.entity;

import main.content.C_OBJ_TYPE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.elements.Filter;
import main.elements.Filter.FILTERS;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NotCondition;
import main.elements.conditions.standard.OccupiedCondition;
import main.elements.conditions.standard.OwnershipCondition;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.Formula;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FilterMaster {

    public static List<? extends Entity> getFilteredList(Condition c, List<? extends Entity> list) {
        return filterOut(new LinkedList(list), c);

    }

    public static Collection<? extends Entity> filterByPropJ8
            (Collection<? extends Entity> list,
             String prop, String value) {
        list.removeIf(getPredicateProperty(ContentManager.getPROP(prop), value, true));
        return list;
    }

    private static Predicate<Entity> getPredicateProperty(PROPERTY prop,
                                                          String value) {
        return getPredicateProperty(prop, value, false);
    }

        private static Predicate<Entity> getPredicateProperty(PROPERTY prop, String value, boolean negative) {
            return e -> {
            if (negative) return !e.checkProperty((prop), value);
            return e.checkProperty((prop), value);
        };
    }
    public static List<ObjType> getFilteredTypeList(OBJ_TYPE TYPE, PROPERTY prop, String value) {
         List<ObjType> list = new LinkedList<>(DataManager.getTypes(TYPE));

        list.removeIf(getPredicateProperty(prop, value, true));
        return list;
    }
    public static Collection<?> filterByProp(Collection<?> list, String prop, String value) {
        return filterByProp(list, prop, value, false);
    }

    public static Collection<?> filterByProp(Collection<?> list, String prop, String value,
                                             boolean filterOut) {
        return filterByProp(list, prop, value, null, filterOut);
    }

    public static Collection<?> filterByProp(Collection<?> list, String prop, String value,
                                             DC_TYPE TYPE) {
        return filterByProp(list, prop, value, TYPE, false);
    }

    public static Collection<?> filterByProp(Collection<?> list, String prop, String value,
                                             OBJ_TYPE TYPE, boolean filterOut) {
        return filter(list, prop, value, TYPE, true, filterOut, null);
    }

    public static Collection<?> filterByParam(List<ObjType> list, PARAMETER param, int value,
                                              OBJ_TYPE TYPE, Boolean greater_less_equal) {
        return filter(list, param.getName(), value + "", TYPE, false, false,
                greater_less_equal);
    }

    public static Collection<?> filter(Collection<?> list, String valueName, String value,
                                       OBJ_TYPE TYPE, boolean prop, boolean filterOut, Boolean strict_or_greater_less_equal) {
        List<Object> filteredList = new LinkedList<>();
        for (Object l : list) {

            Entity entity;
            if (l instanceof Entity) {
                entity = (Entity) l;
            } else {
                entity = DataManager.getType(l.toString(), TYPE);
            }
            if (entity == null) {
                continue;
            }
            boolean result;

            if (prop) {
                PROPERTY property = ContentManager.getPROP(valueName);
                if (property.isContainer())
                    // if (!BooleanMaster.isFalse(strict_or_greater_less_equal))
                {
                    result = entity.checkContainerProp(property, value,
                            strict_or_greater_less_equal == null);
                } else {
                    result = StringMaster.compareByChar(entity.getProperty(property), value,
                            BooleanMaster.isTrue(strict_or_greater_less_equal));
                }
                // entity.checkProperty();
            } else {
                PARAMETER param = ContentManager.getPARAM(valueName);
                int amount = new Formula(value).getInt(new Ref(entity));
                if (strict_or_greater_less_equal == null) {
                    result = entity.getIntParam(param) == amount;
                } else {
                    result = entity.checkParam(param, "" + amount);
                    if (!strict_or_greater_less_equal) {
                        result = !result;
                    }
                }
            }

            if (filterOut) {
                if (result) {
                    filteredList.add(l);
                }
            } else if (!result) {
                filteredList.add(l);
            }
        }
        for (Object l : filteredList) {
            list.remove(l);
        }
        return list;
    }

    public static List<? extends Entity> filterOut(List<? extends Entity> list, Condition condition) {
        return filter(list, condition, true);
    }

    public static List<? extends Entity> filter(List<? extends Entity> list, Condition condition) {
        return filter(list, condition, false);
    }

    public static void filter(List<Integer> list, Conditions conditions, Game game) {
        List<Obj> objList = new ListMaster<>().convertToObjList(list, game);
        filter(objList, conditions);
        List<Integer> idList = new ListMaster<>().convertToIdList(objList);
        list.clear();
        for (Integer id : idList) {
            list.add(id);
        }

    }

    public static List<? extends Entity> filter(List<? extends Entity> list, Condition condition,
                                                boolean out) {
        if (list == null) {
            return list;
        }
        Collection<Entity> removeList = new LinkedList<>();
        for (Entity e : list) {
            if (e == null) {
                continue;
            }
            Ref REF = e.getRef().getCopy();
            REF.setID(KEYS.MATCH.name(), e.getId());
            boolean check = !condition.preCheck(REF);
            if (out) {
                check = !check;
            }
            if (check) {
                removeList.add(e);
            }
        }
        list.removeAll(removeList);
        return list;
    }

    public static void applyFilter(Collection<Obj> objects, FILTERS filter, Ref ref, boolean out) {
        List<Obj> objectsToRemove = new LinkedList<>();
        for (Obj obj : objects) {
            if ((out) == checkFilter(obj, filter, ref)) {
                objectsToRemove.add(obj);
            }
        }
        for (Obj obj : objectsToRemove) {
            objects.remove(obj);
        }
    }

    private static boolean checkFilter(Obj obj, FILTERS filter, Ref ref) {
        switch (filter) {
            case ALIVE:
                return !obj.isDead();
            case ALLY:
                return obj.getOwner() == ref.getSourceObj().getOwner();
            case ENEMY:
                return obj.getOwner() != ref.getSourceObj().getOwner();
            case SELF:
                return obj == ref.getSourceObj();
            case NOT_SELF:
                return obj != ref.getSourceObj();
        }
        return false;
    }

    public static Set<Obj> getCellsInRadius(Obj centerObj, Formula radius) {
        return getCellsInRadius(centerObj, radius.getInt(centerObj.getRef()));
    }

    public static List<? extends Entity> getUnitsInRadius(Obj unit, int radius) {
        Conditions conditions = new Conditions(ConditionMaster.getAliveFilterCondition(),
                ConditionMaster.getDistanceFilterCondition("SOURCE", radius, true));
        Set<Obj> set = new Filter<Obj>(unit.getRef(), conditions, C_OBJ_TYPE.UNITS_CHARS)
                .getObjects();
        return new LinkedList<>(set);
    }

    public static Collection<?> filterByProp(List<?> list, PROPERTY prop, Object value) {
        return filterByProp(list, prop.toString(), value.toString());
    }

    public static Set<Obj> getCellsInRadius(Obj centerObj, Integer radius) {
        Conditions conditions = new Conditions();
        conditions.add(ConditionMaster.getDistanceFilterCondition("SOURCE", radius, true));
        conditions.add(new NotCondition(new OccupiedCondition("MATCH")));
        Set<Obj> set = new Filter<Obj>(centerObj.getRef(), conditions, DC_TYPE.TERRAIN)
                .getObjects();
        return set;
    }

    public static Set<Obj> getPlayerControlledUnits(Player player) {
        Conditions c = new Conditions();
        Ref ref;
        if (player == Player.NEUTRAL) {
            c.add(new OwnershipCondition("MATCH", true));
            c.add(ConditionMaster.getBFObjTypesCondition());
            ref = new Ref(player.getGame());
        } else {
            c.add(new OwnershipCondition("MATCH", "SOURCE"));
            c.add(ConditionMaster.getBFObjTypesCondition());
            ref = player.getHeroObj().getRef();
        }
        // spells too?

        Filter<Obj> filter = new Filter<>(ref, c);
        Set<Obj> set = filter.getObjects();

        return set;
    }


}
