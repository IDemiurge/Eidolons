package main.game.battlecraft.logic.battle.test;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.mission.MissionScriptExecutor;
import main.game.battlecraft.logic.battle.mission.MissionScriptExecutor.MISSION_SCRIPT_FUNCTION;
import main.game.battlecraft.logic.battle.test.TestScriptExecutor.TEST_SCRIPT;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.battle.universal.ScriptManager;
import main.game.battlecraft.logic.meta.scenario.script.ScriptGenerator;
import main.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.game.core.game.TestGame;
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
                break;
            case ENABLE_ACTION:
                doEnableAction(ref, args);
                break;
            case HIGHLIGHT:
                doHighlight(ref, args);
                break;
        }

        return true;
    }
    @Override
    public void init() {
        String text=readScriptsFile();
        parseScripts(text);

        MissionScriptExecutor genericScriptsExecutor =
         new MissionScriptExecutor(getMaster()){
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
        text=genericScriptsExecutor.readScriptsFile();
        for (String script : StringMaster.openContainer(text,
         ScriptSyntax.SCRIPTS_SEPARATOR)) {
            addTrigger(ScriptParser.parseScript(
             script,
             getMaster().getGame(),
             genericScriptsExecutor,
             MISSION_SCRIPT_FUNCTION.class));
        }
    }

    @Override
    protected String readScriptsFile() {

        String text = FileManager.readFile(
     StringMaster.buildPath(
     getMaster().getMetaMaster().getMetaDataManager().getDataPath()
      , "tutorial "+ ScriptGenerator.SCRIPTS_FILE_NAME));
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
        Entity entity=null ;
        if (type!=null ){
            DC_TYPE TYPE = (DC_TYPE) type.getOBJ_TYPE_ENUM();
            entity = findEntity(nameOrKey, ref, TYPE);

        } else {
           entity= ref.getObj(nameOrKey);
        }
//        GuiEventManager.trigger(GuiEventType.HIGHLIGHT, entity);
    }

    private Entity findEntity(String entityName, Ref ref, DC_TYPE TYPE) {
        Unit hero = (Unit) ref.getSourceObj();
        switch (TYPE){
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

    public enum TEST_SCRIPT{
        GUI_EVENT,
ENABLE_ACTION,
        HIGHLIGHT,
        setMainHero,

    }
}
