package eidolons.system.test;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.test.frontend.FAST_DC;
import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.system.MetaEnums;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.entity.type.SpellType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.entity.FilterMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class TestMasterContent {
    public static final String TEST_WEAPONS = "inferior iron long sword;"
     + "inferior iron long sword;"
     + "inferior iron short sword;"
     + "inferior iron dirk;"
     + "inferior iron dagger;"
     + "inferior iron knife;"
     + "inferior iron war axe;"
     + "inferior iron great axe;"
     + "inferior iron hand axe;"
     + "inferior iron mace;"
     + "inferior iron battle hammer;"
     + "inferior redwood battlestaff;"
     + "inferior redwood longbow;"
     + "inferior redwood staff;"
     + "inferior iron spear;"
     + "inferior iron lance;"
     + "inferior iron trident;"
     + "inferior iron halbert;"
     + "inferior iron halbert;"
     + "inferior redwood staff;"
     + "inferior redwood staff;"
     + "inferior redwood staff;";
    public static final boolean ALL_WEAPONS_TEST = true;
    private static final String DEFAULT_SKILLS = "Turn About;Leap;Side Step;Quick Turn;";
    private static final String testGroups = "Fire;Water;Air;Earth;";
    private static final String TEST_SKILLS = "Warcry: To Arms!;Druidic Visions;Roots of Wisdom;Coating Expert;Coating Mastery III;Toss Item;Feint Throw;Cleave (Axe);"
     + "Slam;Knockdown (Polearm);Power Strike;"
     + ""
     + "Nimble Fingers;"
     + ""
     + "Aura of Resilience;"
     + ""
     + ""
     + "Quick Swap;"
     + "Practiced Channeler;"
     + "Shortcut Schematics;Fast Reload;"
     + ""
     + "Spellcasting Algorithms;"
     + "Maneuver: Swap Positions;"
     + "Maneuver: Displace;"
     + "Hide;"
     + "Stealth Mode;"

     + "Offensive Formation;"

     + "Dirty Tactics;"
     + "Noble Tactics;"
     + "Command: Retreat!;"
     + "Command: Forward!;"
     + "Command: Hold Position!;" + "" + "" + "";
    private static final String TEST_ITEMS = "" + "Potent Liquid Shadow;" + "Potent Fire Bomb;"
     + "Potent Paralyzing Poison;" + "Potent Poison Coating;" + "Normal Poison Coating;"
     + "Cheap Poison Coating;" + "Potent Weakening Poison;" + "Inferior Dark Steel Dagger;"
     + "Masterpiece Dark Steel Dagger;" + "Ancient Dark Steel Bolts;"
     + "Ancient Dark Steel Bolts;";
    public static boolean addSpells = true;
    public static boolean addAllSpells = true;
    public static boolean test_on = false;
    static boolean auto_test_list = false;
    static boolean full_test = false;
    static boolean char_test_active_filtering = true;
    /*
     * specify test units specify test spells imagine just specifying all this
     * stuff here... the running it and seeing results logged almost
     * immediately!
     */
    private static Boolean immortal = null;
    private static boolean forceFree;
    private static String FOCUS_LIST = "";
    private static String FIX_LIST = "";
    private static String POLISH_LIST = "";
    private static String ANIM_TEST_LIST = //Gust of Wind
     "Searing Light;Summon Vampire Bat;"
//      +"Sorcerous Flame;Ray of Arcanum;Chaos Shockwave;" +
//      "Fire Bolt;Chaos Bolt;" +
//      "Light;Haze;" +
//      "Scare;Freeze;" +
//      "Scorching Light"
     ;

    private static String GRAPHICS_TEST_LIST = "Raise Skeleton;Fire Bolt;Chaos Bolt;Force Field;" +
     "Summon Vampire Bat;Blink";
    private static String TEST_LIST =
//     "Raise Skeleton;" +
//     "Light;Haze;" +
//      "Leap into Darkness;Blink;Summon Vampire Bat;"+
     // + "Enchant Weapon;"
     // + "Enchant Armor;"
//      + "Arcane Bolt;Ray of Arcanum;Time Warp;"
     // + "Sorcerous Flames;"
     "Force Field;"
     // "Arms of Faith;Armor of Faith;Resurrection;"+
//      "Mass Terror;Mass Confusion;Mass Madness;" + "Conjure Weapon;Conjure Armor;" + ""
     // + "Awaken Treant;"
     // + "Sacrifice;"
     // + "Soul Web;Rapid Growth;"
     ;
    private static String AI_SPELL_TEST_LIST = "Summon Lesser Demon;";
    private static List<ObjType> addedSpells = new ArrayList<>();
    private static String FOCUS_SKILL_LIST = "";
    private static String FIX_SKILL_LIST = "";
    private static String POLISH_SKILL_LIST = "";
    private static String TEST_SKILL_LIST = "";

    // WORKSPACE_GROUP[] ws_groups_included = {WORKSPACE_GROUP.TEST,
    // WORKSPACE_GROUP.FIX, WORKSPACE_GROUP.POLISH};
    private static boolean addSkills;
    private static boolean addItems;
    private static boolean addActives;
    private static boolean first;
    private static boolean weaponTest = true;
    String MAGIC_SCHOOLS = "";
    int circle;
    // TODO also give them for free to all units!
    TestingConfigurations testConfig;
    private DC_Game game;

    public TestMasterContent(DC_Game game) {
        this.game = game;
        if (auto_test_list) {
            initAutoTestList();
        }
        initTestConfig();
    }

    public static void initTestCase(TestCase testCase) {
        switch (testCase.getType()) {

        }
    }

    public static boolean checkHeroForTestSpell(ObjType type, String typeName, boolean last) {
        if (FAST_DC.FAST_MODE) {
            if (first) {
                return true;
            }
        }
        try {
            return tryCheckHeroForTestSpell(type, typeName, last);

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public static boolean tryCheckHeroForTestSpell(ObjType type, String typeName, boolean last) {

        if (type.checkProperty(PROPS.MEMORIZED_SPELLS, typeName)) {
            return false;
        }
        if (!char_test_active_filtering) {
            return true;// TODO
        }
        /*
         * I want to make sure that all actives are given to somebody... I can
		 * keep a list of added actives and give all non-given to the last
		 * hero...
		 *
		 * checkMastery() or at least aspect preCheck class/masteries for actions
		 */
        SpellType spellType = (SpellType) DataManager.getType(typeName, DC_TYPE.SPELLS);
        if (spellType == null) {
            return false;
        }
        boolean result = false;
        if (type.getIntParam(ContentValsManager.getSpellMasteryForSpell(spellType)) > 0) {
            result = true;
        }
        if (type.getAspect() == spellType.getAspect()) {
            result = true;
        }
        try {
            if (DataManager.getType(type.getProperty(G_PROPS.DEITY), DC_TYPE.DEITIES).getAspect() == spellType
             .getAspect()) {
                result = true;
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (type.getAspect() == spellType.getAspect()) {
            result = true;
        }
        if (last) {
            if (!addedSpells.contains(spellType)) {
                result = true;
            }
        }
        if (result) {
            addedSpells.add(spellType);
            return true;
        }

        return false;
    }

    public static void addToTEST_LIST(String a) {
        TEST_LIST += a + ";";
    }

    public static String getTEST_LIST() {
        return TEST_LIST;
    }

    public static void setTEST_LIST(String tEST_LIST) {
        TEST_LIST = tEST_LIST;
    }

    public static String getFIX_LIST() {
        return FIX_LIST;
    }

    public static void setFIX_LIST(String fIX_LIST) {
        FIX_LIST = fIX_LIST;
    }

    public static String getPOLISH_LIST() {
        return POLISH_LIST;
    }

    public static void setPOLISH_LIST(String pOLISH_LIST) {
        POLISH_LIST = pOLISH_LIST;
    }

    public static void toggleImmortal() {
        if (immortal == null) {
            immortal = true;
        } else {
            immortal = !immortal;
        }

    }

    public static void toggleFree() {
        forceFree = !forceFree;

    }

    public static String getFOCUS_LIST() {
        return FOCUS_LIST;
    }

    public static void setFOCUS_LIST(String fOCUS_LIST) {
        FOCUS_LIST = fOCUS_LIST;
    }

    public static void addTestActives(Boolean full, ObjType type, boolean last) {
        addTestSpells(type, last);

        // for (String s : StringMaster.open(getFOCUS_LIST()))
        // if (checkHeroForTestSpell(type, s, last))
        // type.addProperty(G_PROPS.ACTIVES, s, true);
        // for (String s : StringMaster.open(TestMaster.getFIX_LIST()))
        // if (checkHeroForTestSpell(type, s, last))
        // type.addProperty(G_PROPS.ACTIVES, s, true);
        // for (String s : StringMaster.open(getTEST_LIST()))
        // if (checkHeroForTestSpell(type, s, last))
        // type.addProperty(G_PROPS.ACTIVES, s, true);
        // for (String s : StringMaster.open(getPOLISH_LIST()))
        // if (checkHeroForTestSpell(type, s, last))
        // type.addProperty(G_PROPS.ACTIVES, s, true);

        if (full != null) {
            if (full) {
                for (ObjType s : DataManager.getTypes(DC_TYPE.SPELLS)) {
                    if (s.isUpgrade()) {
                        continue;
                    }
                    if (s.getIntParam(PARAMS.SPELL_DIFFICULTY) < 1) {
                        continue;
                    }
                    type.addProperty(PROPS.VERBATIM_SPELLS, s.getName(), true);
                }
                for (ObjType s : DataManager.getTypes(DC_TYPE.SPELLS)) {
                    if (!s.isUpgrade()) {
                        continue;
                    }
                    if (s.getIntParam(PARAMS.SPELL_DIFFICULTY) < 1) {
                        continue;
                    }
                    type.addProperty(PROPS.MEMORIZED_SPELLS, s.getName(), true);
                }
                addTestActives(type, last);
                return;
            }
        }
    }

    public static void addSpells(Entity type, String list) {
        for (String s : StringMaster.open(list)) {
            type.addProperty(PROPS.VERBATIM_SPELLS, s
             , true);
        }

    }

    public static void addANIM_TEST_Spells(Entity type) {
        addSpells(type, ANIM_TEST_LIST);
    }

    public static void addGRAPHICS_TEST_Spells(Entity type) {
        for (String s : StringMaster.open(GRAPHICS_TEST_LIST)) {
            type.addProperty(PROPS.VERBATIM_SPELLS, s
             , true);
        }
    }

    public static void addTestGroupSpells(Entity type) {
        if (testGroups != null) {
            for (String substring : StringMaster.open(testGroups)) {
                String prop = G_PROPS.SPELL_GROUP.getName();
                String spells = "";
                try {
                    spells =
                     StringMaster.constructEntityNameContainer(
                      (List<? extends Entity>) FilterMaster.filterByPropJ8(DataManager.getTypes(DC_TYPE.SPELLS),
                       prop, substring));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                addSpells(type, spells);

            }
        }
    }

    private static void addTestSpells(ObjType type, boolean last) {
        type.addProperty(PROPS.VERBATIM_SPELLS, "Blink;"
         + getTEST_LIST(), true);
        if (!addSpells) {
            return;
        }
        if (addAllSpells) {
            addAllSpells(type);
            return;
        }

        for (String s : StringMaster.open(getFOCUS_LIST())) {
            if (checkHeroForTestSpell(type, s, last)) {
                type.addProperty(PROPS.VERBATIM_SPELLS, s, true);
            }
        }
        for (String s : StringMaster.open(TestMasterContent.getFIX_LIST())) {
            if (checkHeroForTestSpell(type, s, last)) {
                type.addProperty(PROPS.VERBATIM_SPELLS, s, true);
            }
        }
        for (String s : StringMaster.open(getTEST_LIST())) {
            if (checkHeroForTestSpell(type, s, last)) {
                type.addProperty(PROPS.VERBATIM_SPELLS, s, true);
            }
        }
        for (String s : StringMaster.open(getPOLISH_LIST())) {
            if (checkHeroForTestSpell(type, s, last)) {
                type.addProperty(PROPS.VERBATIM_SPELLS, s, true);
            }
        }
    }

    private static void addAllSpells(ObjType type) {
        StringBuilder builder = new StringBuilder(DataManager.getTypes(DC_TYPE.SPELLS).size() * 10);
        for (ObjType s : DataManager.getTypes(DC_TYPE.SPELLS)) {
            builder.append(s.getName() + ";");
        }
        type.addProperty(PROPS.VERBATIM_SPELLS, builder.toString(), true);
    }

    public static void setAddAllSpells(boolean addAllSpells) {
        if (addAllSpells)
            TestMasterContent.addSpells = true;
        TestMasterContent.addAllSpells = addAllSpells;
    }

    public static void setAddSpells(boolean addSpells) {
        TestMasterContent.addSpells = addSpells;
    }

    public static void addTestActives(ObjType type, boolean last) {
        if (!addActives) {
            return;
        }
        for (ObjType s : DataManager.getTypes(DC_TYPE.ACTIONS)) {
            type.addProperty(G_PROPS.ACTIVES, s.getName(), true);
        }
    }

    public static void addTestItems(ObjType type, Boolean last) {
        first = false;
        if (last == null) {
            first = true;
            last = false;
        }
        if (CoreEngine.isIDE())
            if (weaponTest) {
                if (ALL_WEAPONS_TEST) {
                    for (ObjType s : DataManager.getTypes(DC_TYPE.WEAPONS)) {
                        type.addProperty(PROPS.INVENTORY,
                         s.getName(), true);
                    }
                } else
                    for (String substring : StringMaster.openContainer(TEST_WEAPONS)) {
                        type.addProperty(PROPS.INVENTORY, substring, true);
                    }

            }
        if (!test_on) {
            return;
        }

        addTestSpells(type, last);
        addTestActives(full_test, type, last);
        addTestSkills(full_test, type, last);
        addTestItems(full_test, type, last);
    }

    private static void addTestSkills(boolean full_test, ObjType type, boolean last) {
        if (!addSkills) {
            return;
        }
        if (full_test) {
            for (ObjType s : DataManager.getTypes(DC_TYPE.SKILLS)) {
                if (WorkspaceMaster.checkTypeIsReadyToTest(s)) {
                    type.addProperty(PROPS.SKILLS, s.getName(), true);
                }
            }
        } else {
            for (ObjType s : DataManager.toTypeList(TEST_SKILLS, DC_TYPE.SKILLS)) {
                type.addProperty(PROPS.SKILLS, s.getName(), true);
            }
        }

    }

    private static void addTestItems(boolean full_test, ObjType type, boolean last) {
        // TODO poisons and the like

        if (!addItems) {
            return;
        }
        if (full_test) {
            for (ObjType s : DataManager.getTypes(DC_TYPE.ITEMS)) {
                if (WorkspaceMaster.checkTypeIsReadyToTest(s)) {
                    type.addProperty(PROPS.QUICK_ITEMS, s.getName(), true);
                }
            }
        } else {
            for (ObjType s : DataManager.toTypeList(TEST_ITEMS, C_OBJ_TYPE.ITEMS)) {
                type.addProperty(PROPS.QUICK_ITEMS, s.getName(), true);
            }
        }
    }

    public static Boolean isImmortal() {
        return immortal;
    }

    public static void setImmortal(Boolean immortal) {
        TestMasterContent.immortal = immortal;
    }

    public static boolean isForceFree() {
        return forceFree;
    }

    public static void setForceFree(boolean forceFree) {
        TestMasterContent.forceFree = forceFree;
    }

    public static boolean isArtifact(DC_WeaponObj weapon) {
        if (weapon.isNatural()) {
            return false;
        }
        if (weapon.isShield()) {
            return false;
        }
        if (weapon.getOwnerObj().getLevel() < 4)
            return false;
        return weapon.isMelee();
    }

    public static void setWeaponTest(boolean weaponTest) {
        TestMasterContent.weaponTest = weaponTest;
    }

    private void initTestConfig() {
        testConfig = new TestingConfigurations();
        testConfig.setTestList(StringMaster.openContainer(getTEST_LIST()));
    }

    public boolean isActionFree(String name) {
        Unit activeObj = game.getManager().getActiveObj();
        if (activeObj != null) {
            if (activeObj.getOwner().isAi()) {
                return false;
            }
        }
        if (forceFree || full_test) {
            return true;
        }
        if (getFOCUS_LIST().contains(name)) {
            return true;
        }
        if (getFIX_LIST().contains(name)) {
            return true;
        }
        if (getTEST_LIST().contains(name)) {
            return true;
        }
        // new ListMaster<>().contains(list, item, strict)
        // testConfig.getTestList().contains(name);
        return false;
    }

    private void initAutoTestList() {
        POLISH_LIST = constructTestList(DC_TYPE.SKILLS, MetaEnums.WORKSPACE_GROUP.POLISH);
        FOCUS_LIST = constructTestList(DC_TYPE.SPELLS, MetaEnums.WORKSPACE_GROUP.FOCUS);
        FIX_LIST = constructTestList(DC_TYPE.SPELLS, MetaEnums.WORKSPACE_GROUP.FIX);
        TEST_LIST = constructTestList(DC_TYPE.SPELLS, MetaEnums.WORKSPACE_GROUP.TEST);

        FOCUS_SKILL_LIST = constructTestList(DC_TYPE.SKILLS, MetaEnums.WORKSPACE_GROUP.FOCUS);
        FIX_SKILL_LIST = constructTestList(DC_TYPE.SKILLS, MetaEnums.WORKSPACE_GROUP.FIX);
        POLISH_SKILL_LIST = constructTestList(DC_TYPE.SKILLS, MetaEnums.WORKSPACE_GROUP.POLISH);
        TEST_SKILL_LIST = constructTestList(DC_TYPE.SKILLS, MetaEnums.WORKSPACE_GROUP.TEST);

    }

    private String constructTestList(OBJ_TYPE TYPE, WORKSPACE_GROUP group) {
        List<String> list; // could use some optimization :) TODO
        list = new ArrayList<>();
        for (ObjType t : DataManager.getTypes(TYPE)) {
            if (t.getProperty(G_PROPS.WORKSPACE_GROUP).equalsIgnoreCase(group.name())) {
                list.add(t.getName());
            }
        }
        return StringMaster.constructContainer(list);
    }

    public enum TEST_OPTIONS {
        GOD_MODE,

    }

    public enum TEST_PROFILES {
        MAGE_DARK, MAGE_DEATH, MAGE_CHAOS, MAGE_ARCANE, MAGE_APOSTATE, MAGE_WIZARD, MAGE,

        WARRIOR, ROGUE, PRIEST,

        ALL,

    }

    public class TestCase {
        String name;
        DC_TYPE type;
        boolean AI;

        public TestCase() {

        }

        public String getName() {
            return name;
        }

        public DC_TYPE getType() {
            return type;
        }

        public boolean isAI() {
            return AI;
        }

    }

}
