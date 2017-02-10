package main.system.math;

import main.content.CONTENT_CONSTS.STD_BOOLS;
import main.content.OBJ_TYPES;
import main.data.ability.construct.VariableManager;
import main.data.ability.construct.VariableManager.AUTOVAR;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.Game;
import main.system.ConditionMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.text.TextParser;

import java.util.*;

public class FunctionManager {

    private static Ref ref;

    public static String evaluateFunction(Ref ref_, String func) {
        ref = ref_;
        func = func.replace("[", "").replace("]", "");
        String arguments[];
        String funcName;
        if (func.contains("(")) {
            arguments = func
                    .substring(func.indexOf('(') + 1, func.indexOf(')')).split(
                            ",");
            funcName = func.substring(0, func.indexOf('('));
        } else {
            arguments = null;
            funcName = func;
        }
        FUNCTIONS function = FUNCTIONS.valueOf(funcName);
        return evaluateFunction(ref_, function, arguments);
    }

    public static String evaluateFunction(Ref ref, FUNCTIONS function,
                                          String[] arguments) {
        return function.evaluate(ref, arguments).toString();
    }

    public static String evaluateFunction(Ref ref, FUNCTIONS function,
                                          String string) {
        String[] arguments = StringMaster.getSubString(string, "" + ('('),
                "" + (')'), false).split(",");
        return evaluateFunction(ref, function, arguments);
    }

    public static Condition getAllConditions() {
        Conditions c = new Conditions(
                ConditionMaster.getUnit_CharTypeCondition());
        c.add(ConditionMaster.getNotDeadCondition());
        return c;
    }

    public static Condition getSumAllyConditions() {
        Conditions c = new Conditions(
                ConditionMaster.getOwnershipFilterCondition(
                        Ref.KEYS.MATCH.name(), true));
        c.add(ConditionMaster.getUnit_CharTypeCondition());
        c.add(ConditionMaster.getNotDeadCondition());
        return c;
    }

    public static Condition getSumEnemyConditions() {
        Conditions c = new Conditions(
                ConditionMaster.getOwnershipFilterCondition(
                        Ref.KEYS.MATCH.name(), false));
        c.add(ConditionMaster.getUnit_CharTypeCondition());
        c.add(ConditionMaster.getNotDeadCondition());
        return c;
    }

    public static Condition getSumAdjacentConditions() {
        Conditions c = new Conditions(
                ConditionMaster.getTYPECondition(OBJ_TYPES.TERRAIN));
        c.add(ConditionMaster.getAdjacentCondition());
        return c;
    }

    public static int getSum(String s, Set<Obj> objs) {
        Formula formula = new Formula("{SOURCE_" + s + "}");
        int result = 0;
        for (Obj obj : objs) {
            result += formula.getInt(obj.getRef());
        }
        return result;

    }

    public static Collection<FUNCTIONS> getFunctionList() {
        return Arrays.asList(FUNCTIONS.values());
    }

    public static String getAutovarString(AUTOVAR av, String string) {
        return "[AV(" + av + "," + string + ")]";
    }

    public enum FUNC_ARGS {
        OBJ_REF {
            public Object evaluate(String s_arg, Ref ref) {
                try {
                    Obj obj = ref.getObj(s_arg);
                    if (obj == null) {
                        LogMaster.log(1, s_arg
                                + " - failed to evaluate obj_ref!" + ref);
                        throw new RuntimeException();
                    }
                    return obj;
                } catch (Exception e) {
                    ObjType type = ref.getType(s_arg);
                    if (type == null) {
                        LogMaster.log(1, s_arg
                                + " - failed to evaluate type ref!" + ref);
                        throw new RuntimeException();
                    }

                    LogMaster.log(1,
                            s_arg + " evaluated to type: " + type.getName());
                    return type;
                }
            }
        },
        // REF {
        // public Object evaluate(String s_arg, Ref ref) {
        // return ref.getObj(s_arg).getRef();
        // }
        // },
        FORMULA {
            public Object evaluate(String s_arg, Ref ref) {
                return new Formula(s_arg);
            }
        },
        INTEGER {
            public Object evaluate(String s_arg, Ref ref) {
                return new Formula(s_arg).getInt(ref);
            }
        },
        CONST {
            public Object evaluate(String s_arg, Ref ref) {
                return s_arg;
            }
        },;

        public Object evaluate(String s_arg, Ref ref) {
            return null;
        }

    }

    public enum FUNCTIONS {
        BOOL(false, FUNC_ARGS.CONST, FUNC_ARGS.CONST, FUNC_ARGS.CONST) {
            public Object evaluate(List<Object> values) {
                if (values.size() != 3) {
                    return false;
                }
                String condition = values.get(0).toString();
                String IF = values.get(1).toString();
                String ELSE = values.get(2).toString();
                return ConditionMaster.checkStringCondition(condition,
                        new Ref()) ? IF : ELSE;
            }
        },
        RANDOM(true, FUNC_ARGS.CONST) {
            public Object evaluate(List<Object> values) {
                if (TextParser.isInfoParsing()) {
                    return Integer.valueOf(values.get(0).toString()) / 2;
                }
                if (Game.game != null) {
                    if (Game.game.isSimulation()) {
                        return Integer.valueOf(values.get(0).toString()) / 2;
                    }
                }
                return RandomWizard.getRandomInt(Integer.valueOf(values.get(0)
                        .toString()));
            }
        },
        FILTER_LIST_SIZE(true, FUNC_ARGS.CONST) {
            @Override
            public Object evaluate(List<Object> values) {

                Filter<Obj> filter = new Filter<>((Ref) values.get(1),
                        ConditionMaster.toConditions(values.get(0).toString()));
                return getSum(values.get(0).toString(), filter.getObjects());

            }
        },
        SUM_ADJACENT(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {

                Filter<Obj> filter = new Filter<>((Ref) values.get(1),
                        getSumAdjacentConditions());
                return getSum(values.get(0).toString(), filter.getObjects());

            }
        },
        SUM_ALL(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {

                Filter<Obj> filter = new Filter<>((Ref) values.get(1),
                        getAllConditions());
                return getSum(values.get(0).toString(), filter.getObjects());

            }
        },
        SUM_ENEMIES(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {

                Filter<Obj> filter = new Filter<>((Ref) values.get(1),
                        getSumEnemyConditions());
                return getSum(values.get(0).toString(), filter.getObjects());

            }
        },

        SUM_ALLIES(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {

                Filter<Obj> filter = new Filter<>((Ref) values.get(1),
                        getSumAllyConditions());
                return getSum(values.get(0).toString(), filter.getObjects());

            }
        },

        AV(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {
                return VariableManager.getAutoVarValue(
                        values.get(0).toString(),
                        (values.size() > 1) ? (Entity) values.get(1) : ref
                                .getThisObj(), null); // REF would be better
                // for AV!
            }
        },
        AUTOVAR(true, FUNC_ARGS.CONST, FUNC_ARGS.OBJ_REF, FUNC_ARGS.CONST) {
            @Override
            public Object evaluate(List<Object> values) {
                return VariableManager.getAutoVarValue(
                        values.get(0).toString(), (Obj) values.get(1), values
                                .get(2).toString());
            }
        },
        CONST(true, FUNC_ARGS.CONST) {
            @Override
            public Object evaluate(List<Object> values) {
                return ConstantManager.getConst(values.get(0).toString());
            }
        },
        ABS(true, FUNC_ARGS.INTEGER) {
            public Object evaluate(List<Object> values) {
                return new Integer(Math.abs(Integer.valueOf(values.get(0)
                        .toString())));
            }
        },
        DISTANCE(true, FUNC_ARGS.OBJ_REF, FUNC_ARGS.OBJ_REF) {
            @Override
            public Object evaluate(List<Object> values) {
                Obj obj1 = (Obj) values.get(0);

                Integer integer = Math.round(PositionMaster.getDistance(obj1,
                        (Obj) values.get(1)));
                boolean shortDiags;
                try {
                    shortDiags = obj1.getRef().getObj(KEYS.ACTIVE)
                            .checkBool(STD_BOOLS.SHORTEN_DIAGONALS);
                } catch (Exception e) {
                    return integer;
                }
                if (shortDiags) {
                    if (!PositionMaster.inLine(obj1, (Obj) values.get(1))) {
                        integer++;
                    }
                }
                return integer;

            }
        },;
        private FUNC_ARGS[] args;

        FUNCTIONS(String value) {

        }

        FUNCTIONS(boolean integerResult, FUNC_ARGS... args) {
            this.args = args;
        }

        public Object evaluate(List<Object> values) {
            return null;
        }

        public Object evaluate(Ref ref, String[] args) {
            int i = 0;
            List<Object> values = new LinkedList<>();
            for (String s_arg : args) {
                FUNC_ARGS arg = this.args[i];
                Object value = arg.evaluate(s_arg, ref);
                values.add(value);
                i++;
            }

            return evaluate(values);
        }
    }
}
