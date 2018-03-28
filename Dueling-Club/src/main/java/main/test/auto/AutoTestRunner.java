package main.test.auto;

import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE_SCOPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.test.auto.AutoTest.TEST_ARGS;

public class AutoTestRunner {

    AutoTest test;
    private AutoTestMaster master;

    public AutoTestRunner(AutoTestMaster master, AutoTest test) {
        this.master = master;
        this.test = test;
    }

    public void run() {
        Unit unit = master.getSource();
        RuleMaster.setScope(RULE_SCOPE.TEST);
        if (test.getArg(TEST_ARGS.RULE_SCOPE) != null) {
            RuleMaster.setScope(new EnumMaster<RULE_SCOPE>().retrieveEnumConst(RULE_SCOPE.class,
             test.getArg(TEST_ARGS.RULE_SCOPE)));
        }
        switch (test.getType()) {
            case ACTION_SKILL:
                runActionSkillTest();
                break;
            default:
                addTestTypes(unit);
                break;
        }
        runTestFunctions();
        // switch (test.getAssertions()) {
        // }

    }

    private void runTestFunctions() {
        // if (test.getType() == MODE) {
        //
        // }
    }

    private void runActionSkillTest() {
        String actionNames = test.getArgMap().get(TEST_ARGS.ACTION_NAMES);
        Ref ref = test.getRef();
        Unit unit = master.getSource();
        for (String actionName : StringMaster.open(actionNames)) {
            actionTest(actionName, ref, unit);
        }
        addTestTypes(unit);
        for (String actionName : StringMaster.open(actionNames)) {
            actionTest(actionName, ref, unit);
        }
    }

    private void actionTest(String name, Ref ref, Unit unit) {
        unit.getAction(name).setRef(ref);
        unit.getAction(name).activate();
        logMeasurements();
        logAssertions();
    }

    private void addTestTypes(Unit unit) {
        for (String typeName : StringMaster.open(test.getArgMap().get(
         TEST_ARGS.TEST_SKILLS))) {
            // ObjType type = DataManager.getType(typeName, OBJ_TYPES.SKILLS);
            // DC_FeatObj feat;
            // unit.getSkills().add(feat);
            if (test.getE() != null) {
                master.getSource().addFeat((DC_FeatObj) test.getE());
            } else {
                master.getSource().addFeat(
                 (DC_FeatObj) master.getFactory().initEntity(
                  DataManager.getType(typeName, C_OBJ_TYPE.FEATS)));
            }
        }
        // unit.toBase();
        unit.fullReset(unit.getGame());
    }

    private void logMeasurements() {
        // TODO Auto-generated method stub

    }

    private void logAssertions() {
        // for (Assertion assertion : test.getAssertions()) {
        // result = assertion.makeAssertion();
        // fileLog += "\n" + result;
        // }
    }

    public enum TEST_FUNCTION {
        END_TURN,
    }

}
