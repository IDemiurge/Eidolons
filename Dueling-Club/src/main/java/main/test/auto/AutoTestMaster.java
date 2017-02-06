package main.test.auto;

import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.DC_FeatObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.logic.dungeon.DungeonMaster;
import main.system.auxiliary.FileManager;
import main.system.auxiliary.TimeMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.auto.AutoTest.TEST_ARGS;
import main.test.debug.GameLauncher.CODE;
import main.test.frontend.FAST_DC;

import java.util.LinkedList;
import java.util.List;

public class AutoTestMaster {
    public static final String TEST_DUNGEON = "Test\\Underworld Keep.xml";
    public static final String SKILL_PRESET_TEST_TYPES = "Spinning Axes;";
    public static final String CLASS_PRESET_TEST_TYPES = "Rogue;Swashbuckler;Thug;Squire;Knight;Man-at-Arms;Scout;Ranger;Marksman;";
    private static final Integer PRESET = null;
    private static AutoTestMaster instance;
    private static DC_Game game;
    private static OBJ_TYPE TYPE = C_OBJ_TYPE.FEATS;// OBJ_TYPES.SKILLS;
    private static List<String> logStrings;
    private static boolean running;
    // complete condition - automatically in this mode...
    List<ObjType> testTypes;
    List<AutoTest> tests;
    private String[] args;
    private boolean paused;
    private Entity target;
    private boolean skill = true;
    private boolean workspace = false;
    private MicroObj source;
    private AutoTestFactory factory;

    public static void main(String[] args) {
        // map
        running = true;
        FAST_DC.ENEMY_CODE = CODE.PRESET;
        FAST_DC.PARTY_CODE = CODE.PRESET;
        DungeonMaster.RANDOM_DUNGEON = false;
        DungeonMaster.setDEFAULT_DUNGEON_PATH(TEST_DUNGEON);
        FAST_DC.DEFAULT_DUNGEON = TEST_DUNGEON;
        FAST_DC.ENEMY_PARTY = "Base Hero Type";
        FAST_DC.PLAYER_PARTY = "Base Hero Type";
        FAST_DC.getGameLauncher().DUMMY_MODE = true; // TODO not all of it
        FAST_DC.getGameLauncher().DUMMY_PP = false;

        FAST_DC.main(FAST_DC.SKIP_CHOICE_ARGS);

        game = DC_Game.game;
        game.getPlayer(false).setAi(false);
        runTests();
    }

    public static void runTests() {
        instance = new AutoTestMaster();
        if (TYPE instanceof C_OBJ_TYPE) {
            C_OBJ_TYPE cTYPE = (C_OBJ_TYPE) TYPE;
            for (OBJ_TYPES t : cTYPE.getTypes()) {
                instance.runTests(t);
            }
        } else {
            instance.runTests((OBJ_TYPES) TYPE);
        }
    }

    public static void testParty(ObjType partyType, List<ObjType> testedTypes) {

    }

    public static void testType(ObjType selectedType) {
        TYPE = (OBJ_TYPES) selectedType.getOBJ_TYPE_ENUM();
        if (instance == null) {
            main(new String[]{selectedType.getName()});
        } else {
            instance.runAutoTest(new AutoTest(selectedType, "", instance.getFactory().getType(
                    selectedType), instance));
        }

    }

    public static void saveTestsAsSuite() {

    }

    public static void writeLogToFile() {
        String contents = "";
        for (String line : logStrings) {
            contents += line + "\n";
        }
        FileManager.write(contents, PathFinder.getLogPath() + "\\tests\\" + TYPE
                + TimeMaster.getFormattedTime(false, true) + ".txt");
    }

    public static AutoTestMaster getInstance() {
        return instance;
    }

    public static String[] getDcTestArgs() {
        return null;
    }

    public static Integer getPreset() {
        return PRESET;
    }

    public static OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public static List<String> getLogStrings() {
        if (logStrings == null) {
            logStrings = new LinkedList<>();
        }
        return logStrings;
    }

    public static boolean isRunning() {
        return running;
    }

    public void runTests(OBJ_TYPES t) {
        // if (tests == null)
        // if (args.length < 1)

        factory = new AutoTestFactory(this);
        factory.setTYPE(t);
        tests = factory.initTests();

        for (AutoTest test : tests) {
            int result = runAutoTest(test);
            if (isManualControlMode()) {
                WaitMaster.waitForInput(WAIT_OPERATIONS.AUTO_TEST_INPUT);
            }
        }
    }

    private boolean isManualControlMode() {
        return true;
    }

    public int runAutoTest(AutoTest test) {
        log("Running test: " + test);
        clear();
        initSimulation(test);
        initTestDungeon(test);

        new AutoTestRunner(this, test).run();
        return 0;
    }

    private void initTestDungeon(AutoTest test) {
        // TODO Auto-generated method stub
    }

    private void initSimulation(AutoTest test) {
        int x = game.getDungeon().getCellsX() / 2;
        int y = game.getDungeon().getCellsY() / 2;
        String arg = test.getArg(TEST_ARGS.SOURCE);

        source = DC_Game.game.createUnit(DataManager.getType(arg, OBJ_TYPES.CHARS), x, y, game
                .getPlayer(true), new Ref(game));

        y--;
        arg = test.getArg(TEST_ARGS.TARGET);
        target = DC_Game.game.createUnit(DataManager.getType(arg, OBJ_TYPES.CHARS), x, y, game
                .getPlayer(true), new Ref(game));

        arg = test.getArg(TEST_ARGS.WEAPON);
        ObjType weaponType = DataManager.getType(arg, OBJ_TYPES.WEAPONS);
        if (weaponType != null) {
            getSource().setWeapon(new DC_WeaponObj(weaponType, getSource()));
            AbilityConstructor.constructActives(source);
        }
        test.setEntity(initEntity(test.getTestType()));
        // graphicsOn = test.isGraphicsOn();

        Ref ref = new Ref(getSource());
        ref.setTarget(getTestTarget().getId());
        test.setRef(ref);
    }

    public Entity initEntity(ObjType type) {
        switch ((OBJ_TYPES) type.getOBJ_TYPE_ENUM()) {
            case CLASSES:
            case SKILLS:
                return new DC_FeatObj(type, new Ref(getSource()));
        }
        return null;
    }

    private void clear() {
        DC_Game.game.getManager().killAll(false);
        // DC_Game.game.getArenaManager().getSpawnManager().
        // DC_Game.game.getDebugMaster().setArg(arg)

    }

    public void pause() {
        paused = true;
    }

    public void log(String string) {
        main.system.auxiliary.LogMaster.log(1, "**********************************" + string);
        getLogStrings().add(string);
    }

    public DC_HeroObj getSource() {
        // if (source != null)
        return (DC_HeroObj) source;
        // return (DC_HeroObj)
        // DC_Game.game.getPlayer(true).getControlledUnits().toArray()[0];
        // return DC_Game.game.getMainHero();
    }

    public Entity getTestTarget() {
        if (target != null) {
            return target;
        }
        return game.getPlayer(false).getHeroObj();
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public List<ObjType> getTestTypes() {
        return testTypes;
    }

    public List<AutoTest> getTests() {
        return tests;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isSkill() {
        return skill;
    }

    public boolean isWorkspace() {
        return workspace;
    }

    public AutoTestFactory getFactory() {
        return factory;
    }

}
