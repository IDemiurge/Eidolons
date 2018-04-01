package eidolons.game.battlecraft.logic.battle.test;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION;
import eidolons.game.battlecraft.logic.battle.test.TestScriptExecutor.TEST_SCRIPT;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.battle.universal.ScriptManager;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptGenerator;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import eidolons.game.core.game.TestGame;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TestScriptExecutor extends ScriptManager<TestBattle, TEST_SCRIPT> {

    public TestScriptExecutor(BattleMaster<TestBattle> master) {
        super(master);
    }

    @Override
    public boolean execute(TEST_SCRIPT function, Ref ref, String... args) {
        switch (function) {
            case GUI_EVENT:
                doGuiEvent(ref, args);
                break;
            case ENABLE_ACTION:
                doEnableAction(ref, args);
                break;
            case HIGHLIGHT:
                doHighlight(ref, args);
                break;
            case setMainHero:
                doSetMainHero(ref, args);
                break;
        }


        return true;
    }

    private void doGuiEvent(Ref ref, String[] args) {
        String name = args[0];
        GuiEventType type = new EnumMaster<GuiEventType>().retrieveEnumConst(GuiEventType.class, name);
        Object arg = getGuiEventArg(ref, type, args);
        GuiEventManager.trigger(type, arg);
    }

    private Object getGuiEventArg(Ref ref, GuiEventType type, String[] args) {
        return null;
    }

    private void doSetMainHero(Ref ref, String[] args) {
        Unit hero = getGame().getMaster().getUnitByName(args[0], ref, true, null, null);
        hero.getOwner().setHeroObj(hero);
    }

    @Override
    public void init() {
        String text = readScriptsFile();
        parseScripts(text);

        CombatScriptExecutor genericScriptsExecutor =
         new CombatScriptExecutor(getMaster()) {
             @Override
             public String readScriptsFile() {
                 String text = FileManager.readFile(
                  StringMaster.buildPath(
                   getMaster().getMetaMaster().getMetaDataManager().getDataPath()
                   , ScriptGenerator.SCRIPTS_FILE_NAME));
                 text = StringMaster.getLastPart(text, ScriptSyntax.COMMENT_CLOSE);
                 return text;
             }
         };
        text = genericScriptsExecutor.readScriptsFile();
        for (String script : StringMaster.open(text,
         ScriptSyntax.SCRIPTS_SEPARATOR)) {
            addTrigger(ScriptParser.parseScript(
             script,
             getMaster().getGame(),
             genericScriptsExecutor,
             COMBAT_SCRIPT_FUNCTION.class));
        }
    }

    @Override
    protected String readScriptsFile() {

        String text = FileManager.readFile(
         StringMaster.buildPath(
          getMaster().getMetaMaster().getMetaDataManager().getDataPath()
          , "tutorial " + ScriptGenerator.SCRIPTS_FILE_NAME));
        return text;
    }

    @Override
    protected Class<TEST_SCRIPT> getFunctionClass() {
        return TEST_SCRIPT.class;
    }

    @Override
    public TestGame getGame() {
        return (TestGame) super.getGame();
    }

    private void doEnableAction(Ref ref, String[] args) {
//        DC_Obj action = (DC_Obj) findEntity(args[1], ref, DC_TYPE.ACTIONS);
        getGame().getCombatMaster().setBlockActionExceptions(args[0]);
    }

    private void doHighlight(Ref ref, String[] args) {
//        GuiEventManager.trigger(GuiEventType.HIGHLIGHT_OFF);
        String nameOrKey = args[0];
        ObjType type = DataManager.getType(nameOrKey);
        Entity entity = null;
        if (type != null) {
            DC_TYPE TYPE = (DC_TYPE) type.getOBJ_TYPE_ENUM();
            entity = findEntity(nameOrKey, ref, TYPE);

        } else {
            entity = ref.getObj(nameOrKey);
        }
//        GuiEventManager.trigger(GuiEventType.HIGHLIGHT, entity);
    }

    private Entity findEntity(String entityName, Ref ref, DC_TYPE TYPE) {
        Unit hero = (Unit) ref.getSourceObj();
        switch (TYPE) {
            case ACTIONS:
                return hero.getAction(entityName);
            case SPELLS:
                return hero.getSpell(entityName);
            case WEAPONS:
                return hero.getItem(entityName);

            case UNITS:
            case CHARS:
            case BF_OBJ:
                getGame().getMaster().getUnitByName(entityName, ref);
        }
        return null;
    }

    @Override
    public String getSeparator(TEST_SCRIPT func) {
        return ScriptSyntax.SCRIPTS_SEPARATOR_ALT;
    }

// use real time

    public enum TEST_SCRIPT {
        GUI_EVENT,
        ENABLE_ACTION,
        HIGHLIGHT,
        setMainHero,
        endRound,
    }
}
