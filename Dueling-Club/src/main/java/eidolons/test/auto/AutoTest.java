package eidolons.test.auto;

import main.content.CONTENT_CONSTS2.AUTO_TEST_ASSERTION;
import main.content.CONTENT_CONSTS2.AUTO_TEST_TYPE;
import eidolons.content.PROPS;
import main.data.XLinkedMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.List;
import java.util.Map;

public class AutoTest {

    protected boolean running;
    List<TestCheck> checks;
    private AUTO_TEST_TYPE type;
    private String arg;
    private AutoTestMaster master;
    private Entity entity;
    private Ref ref;
    private Map<TEST_ARGS, String> argMap = new XLinkedMap<>();
    private List<Assertion> assertions;
    private ObjType testType;

    public AutoTest(ObjType testType, String args, AUTO_TEST_TYPE type, AutoTestMaster master) {
        this.master = master;
        this.type = type;
        this.testType = testType;
        for (String a : StringMaster.open(args)) {
            TEST_ARGS argType = new EnumMaster<TEST_ARGS>().retrieveEnumConst(TEST_ARGS.class, a
             .split(StringMaster.PAIR_SEPARATOR)[0]);
            a = a.substring(a.indexOf(StringMaster.PAIR_SEPARATOR) + 1);
            argMap.put(argType, a);
        }

        Map<AUTO_TEST_ASSERTION, String> map = new RandomWizard<AUTO_TEST_ASSERTION>()
         .constructStringWeightMap(testType.getProperty(PROPS.AUTO_TEST_ASSERTIONS),
          AUTO_TEST_ASSERTION.class);
        for (AUTO_TEST_ASSERTION t : map.keySet()) {
            new Assertion(t, map.get(type));
        }
        // AUTO_TEST_TYPE(null, true, "skills", "actions", "spells", "abils"),
        // AUTO_TEST_RULE_FLAGS(null, true, "skills", "actions", "spells",
        // "abils"),
        // AUTO_TEST_ASSERTIONS(null, true, "skills", "actions", "spells",
        // "abils"),
        // AUTO_TEST_MEASUREMENTS(null, true, "skills", "actions", "spells",
        // "abils"),
        // AUTO_TEST_PREFS(null, true, "skills", "actions", "spells", "abils"),
        // AUTO_TEST_CONSTRAINTS
    }

    @Override
    public String toString() {
        String string = "Test ";
        if (type != null) {
            string += "Type: " + type + ", ";
        }
        if (getEntity() != null) {
            string += "Entity: " + getEntity().getName() + ", ";
        }
        if (argMap != null) {
            string += "argMap: " + argMap + " ";
        }
        return string;
    }

    public boolean runTest() {
        running = true;
        return true;
    }

    public void generateConstraints() {
        for (String s : StringMaster.open(getEntity().getProperty(
         PROPS.AUTO_TEST_CONSTRAINTS))) {
//			constraints.add(new Constraint(s, getEntity()));
        }
    }

    public void initSource() {
        Unit unit = getSourceUnit();
//		for (Constraint constraint : constraints) {
//			constraint.init(unit);
//		}
        // autoInit();

    }

    private Unit getSourceUnit() {
        return (Unit) getSource();
    }

    public Obj getSource() {
        return getRef().getSourceObj();
    }

    public Ref getRef() {
        return ref;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public String getArg(TEST_ARGS arg) {
        String string = getArgMap().get(arg);
        if (string == null) {
            return arg.getDefVal();
        }
        return string;
    }

    public AUTO_TEST_TYPE getType() {
        return type;
    }

    // protected abstract boolean runThis();

    public ObjType getTestType() {
        return testType;
    }

    public List<TestCheck> getChecks() {
        return checks;
    }

    public boolean isRunning() {
        return running;
    }

    public String getArg() {
        return arg;
    }

    public AutoTestMaster getMaster() {
        return master;
    }

    public Entity getE() {
        return getEntity();
    }

    public Map<TEST_ARGS, String> getArgMap() {
        return argMap;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public enum TEST_ARGS {
        ACTION_NAMES,
        TEST_SKILLS,
        WEAPON,
        SOURCE("Base Hero Type"),
        TARGET("Base Hero Type"),
        NAME,
        RULE_SCOPE;
        private String defVal;

        TEST_ARGS() {

        }

        TEST_ARGS(String defVal) {
            this.defVal = defVal;
        }

        public String getDefVal() {
            return defVal;
        }
    }

    public enum TEST_SCOPE {
        RULE, CONTENT, AI, MATH, SYSTEM
    }

}
