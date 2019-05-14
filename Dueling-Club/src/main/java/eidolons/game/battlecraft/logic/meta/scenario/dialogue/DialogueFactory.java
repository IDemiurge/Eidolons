package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.system.text.TextMaster;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.util.Refactor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.auxiliary.StringMaster.formatMapKey;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueFactory {
    //***Tavern Chat>3>4***Another Chat>5>10 ...
    public static final String DIALOGUE_SEPARATOR = "***";
    public static final String ID_SEPARATOR = ">";
    private static final String FILE_NAME = "linear dialogues.txt";
    public Map<String, GameDialogue> map = new HashMap<>();
    protected MetaGameMaster master;


    public void constructDialogues
     (String path) {
        String data = FileManager.readFile(path);
        for (String contents : ContainerUtils.open(
         data, DIALOGUE_SEPARATOR)) {
            String[] array = contents.split(ID_SEPARATOR);
            String name = array[0];
            int firstId = NumberUtils.getInteger(array[1]);
            int lastId = NumberUtils.getInteger(array[2]);
            List<Integer> ids = MathMaster.getIntsInRange(firstId, lastId);
            GameDialogue dialogue = createDialogue(name, ContainerUtils.joinList(ids));
            map.put(formatMapKey(name), dialogue);

        }


    }

    public void init(MetaGameMaster master) {
        this.master = master;
        constructDialogues(StrPathBuilder.build(  getFileRootPath(), getFileName()));
    }

    protected String getFileRootPath() {
        if (CoreEngine.isIggDemoRunning()){
            return PathFinder.getDialoguesPath(TextMaster.getLocale());
        }
        if (master.isRngDungeon()){
            return getCommonDialoguePath();
        }
        return
         PathUtils.buildPath(
          master.getMetaDataManager().getDataPath()
          , TextMaster.getLocale(),
          PathUtils.getPathSeparator());
    }

    protected String getCommonDialoguePath() {
        return  PathFinder.getTextPath()+"/dialogue/"+ TextMaster.getLocale();
    }

    protected String getFileName() {
        return FILE_NAME;
    }

    @Refactor
    public GameDialogue getDialogue(String name) {
//       TODO igg demo hack if (map.isEmpty())
            init(Eidolons.game.getMetaMaster());
        return map.get(StringMaster.formatMapKey(name));
    }


    public LinearDialogue createDialogue
     (String name, String idSequence) {
        Speech parent = null;
        Speech root = null;
        for (String ID : ContainerUtils.open(idSequence)) {
            Speech speech = getSpeech(NumberUtils.getInteger(ID));

            String pathRoot = getFileRootPath();
//             PathFinder.getEnginePath() +   PathFinder.getScenariosPath() +p +StringMaster.getPathSeparator()+
//                 TextMaster.getLocale();
            String path =  DialogueLineFormatter.getLinesFilePath(pathRoot);

            speech.getSpeechBuilder(path).buildSpeech(speech);

            if (root == null)
                root = speech;
            if (parent != null) {
                parent.addChild(speech);
                speech.init(master, parent);
            }
            parent = speech;
        }

        return new LinearDialogue(root, name);
    }

    protected Speech getSpeech(Integer integer) {
        return new Speech(integer);
    }

//    public  GameDialogue getDialogue(String data, ScenarioMetaMaster master) {
//        ObjType type = DataManager.getType(data, MACRO_OBJ_TYPES.DIALOGUE);
//        Document node = XML_Converter.getDoc(type.getProperty(PROPS.DIALOGUE_DATA));
//        Speech root = (Speech) ConstructionManager.construct(node);
//        //options - skippable,
//        root.init(master, null);
        /*
        in xml:
        > constructors will be messed up if I add <Replica> there

         */


//        return new GameDialogue(root);
//    }
}
