package eidolons.game.battlecraft.logic.mission.test;

/**
 * Created by JustMe on 6/2/2017.
 */
public class TestScriptExecutor {
//         extends ScriptManager<TestMission, TEST_SCRIPT> {
//
//     public TestScriptExecutor(MissionMaster<TestMission> master) {
//         super(master);
//     }
//
//     @Override
//     public boolean execute(TEST_SCRIPT function, Ref ref, Object... args) {
//         switch (function) {
//             case GUI_EVENT:
//                 doGuiEvent(ref, args);
//                 break;
//             case ENABLE_ACTION:
//                 doEnableAction(ref, args);
//                 break;
//             case HIGHLIGHT:
//                 doHighlight(ref, args);
//                 break;
//             case setMainHero:
//                 doSetMainHero(ref, args);
//                 break;
//         }
//
//
//         return true;
//     }
//
//     private void doGuiEvent(Ref ref, Object[] args) {
//         String name = args[0].toString();
//         GuiEventType type = new EnumMaster<GuiEventType>().retrieveEnumConst(GuiEventType.class, name);
//         Object arg = getGuiEventArg(ref, type, args);
//         GuiEventManager.trigger(type, arg);
//     }
//
//     private Object getGuiEventArg(Ref ref, GuiEventType type, Object[] args) {
//         return null;
//     }
//
//     private void doSetMainHero(Ref ref, Object[] args) {
//         Unit hero = (Unit) getGame().getObjMaster().getByName(args[0].toString(), ref, true, null, null);
//         hero.getOwner().setHeroObj(hero);
//     }
//
//     @Override
//     public void init() {
//         String text = readScriptsFile();
//         parseScripts(text);
//
//         CombatScriptExecutor genericScriptsExecutor =
//          new CombatScriptExecutor(getMaster()) {
//              @Override
//              public String readScriptsFile() {
//                  String text = FileManager.readFile(
//                   PathUtils.buildPath(
//                    getMaster().getMetaMaster().getMetaDataManager().getDataPath()
//                    , ScriptGenerator.SCRIPTS_FILE_NAME));
//                  text = StringMaster.getLastPart(text, ScriptSyntax.COMMENT_CLOSE);
//                  return text;
//              }
//          };
//         text = genericScriptsExecutor.readScriptsFile();
//         for (String script : ContainerUtils.open(text,
//          ScriptSyntax.SCRIPTS_SEPARATOR)) {
//             addTrigger(ScriptParser.parseScript(
//              script,
//              getMaster().getGame(),
//              genericScriptsExecutor,
//              COMBAT_SCRIPT_FUNCTION.class));
//         }
//     }
//
//     @Override
//     protected String readScriptsFile() {
//
//         return FileManager.readFile(
//          PathUtils.buildPath(
//           getMaster().getMetaMaster().getMetaDataManager().getDataPath()
//           , "tutorial " + ScriptGenerator.SCRIPTS_FILE_NAME));
//     }
//
//     @Override
//     protected Class<TEST_SCRIPT> getFunctionClass() {
//         return TEST_SCRIPT.class;
//     }
//
//     @Override
//     public TestGame getGame() {
//         return (TestGame) super.getGame();
//     }
//
//     private void doEnableAction(Ref ref, Object[] args) {
// //        DC_Obj action = (DC_Obj) findEntity(args[1], ref, DC_TYPE.ACTIONS);
//         getGame().getCombatMaster().setBlockActionExceptions(args[0].toString());
//     }
//
//     private void doHighlight(Ref ref, Object[] args) {
// //        GuiEventManager.trigger(GuiEventType.HIGHLIGHT_OFF);
//         String nameOrKey = args[0].toString();
//         ObjType type = DataManager.getType(nameOrKey);
//         Entity entity = null;
//         if (type != null) {
//             DC_TYPE TYPE = (DC_TYPE) type.getOBJ_TYPE_ENUM();
//             entity = findEntity(nameOrKey, ref, TYPE);
//
//         } else {
//             entity = ref.getObj(nameOrKey);
//         }
// //        GuiEventManager.trigger(GuiEventType.HIGHLIGHT, entity);
//     }
//
//     private Entity findEntity(String entityName, Ref ref, DC_TYPE TYPE) {
//         Unit hero = (Unit) ref.getSourceObj();
//         switch (TYPE) {
//             case ACTIONS:
//                 return hero.getAction(entityName);
//             case SPELLS:
//                 return hero.getSpell(entityName);
//             case WEAPONS:
//                 return hero.getItem(entityName);
//
//             case UNITS:
//             case CHARS:
//             case BF_OBJ:
//                 getGame().getObjMaster().getByName(entityName, ref);
//         }
//         return null;
//     }
//
//     @Override
//     public String getSeparator(TEST_SCRIPT func) {
//         return ScriptSyntax.SCRIPTS_SEPARATOR_ALT;
//     }
//
// // use real time
//
//     public enum TEST_SCRIPT {
//         GUI_EVENT,
//         ENABLE_ACTION,
//         HIGHLIGHT,
//         setMainHero,
//         endRound,
//     }
}
