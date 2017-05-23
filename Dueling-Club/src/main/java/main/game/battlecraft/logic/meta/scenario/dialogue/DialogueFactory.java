package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.PROPS;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.ability.construct.ConstructionManager;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueFactory {
    //***Tavern Chat>3>4***Another Chat>5>10 ...
    public static final String DIALOGUE_SEPARATOR = "***";
    public static final String ID_SEPARATOR = ">";
    public static Map<String, GameDialogue> map = new HashMap<>();


        public static void constructScenarioLinearDialogues
        (String data, ScenarioMetaMaster master) {
//        File file = new File(
//         PathFinder.getEnginePath() +
//         PathFinder.getTextPath() +
//         TextMaster.getLocale() +
//         "\\dialogues\\" + scenarioPath);
//        for (File file : FileManager.getFilesFromDirectory(PathFinder.getTextPath() +
//          TextMaster.getLocale() +
//          "\\dialogues\\" + scenarioPath, false))
//            data =   FileManager.readFile(file);
        for (String contents : StringMaster.openContainer(
       data, DIALOGUE_SEPARATOR)) {
            String[] array = contents.split(ID_SEPARATOR);
            String name = array[0];
            int firstId = StringMaster.getInteger(array[1]);
            int lastId = StringMaster.getInteger(array[2]);
            List<Integer> ids = MathMaster.getIntsInRange(firstId, lastId);
            GameDialogue dialogue = getLinearDialogue(StringMaster.joinList(ids), master);
            map.put(name, dialogue);

        }


    }

    public static GameDialogue getLinearDialogue
     (String name) {
        return map.get(name);
    }

    public static GameDialogue getLinearDialogue
     (String idSequence, ScenarioMetaMaster master) {
        //ids also are supposed to be built linearly, right?

        //build Speech objects!
        List<Speech> speechSequence = new LinkedList<>();
        Speech parent = null;
        Speech root = null;
        for (String ID : StringMaster.openContainer(idSequence)) {
            Speech speech = new Speech(StringMaster.getInteger(ID));
            if (root == null)
                root = speech;
            if (parent != null) {
                parent.addChild(speech);
                speech.init(master, parent);
            }
            parent = speech;

        }

        return new LinearDialogue(root);
    }

    public static GameDialogue getDialogue(String data, ScenarioMetaMaster master) {
        ObjType type = DataManager.getType(data, MACRO_OBJ_TYPES.DIALOGUE);
        Document node = XML_Converter.getDoc(type.getProperty(PROPS.DIALOGUE_DATA));
        Speech root = (Speech) ConstructionManager.construct(node);
        //options - skippable,
        root.init(master, null);
        /*
        in xml:
        > constructors will be messed up if I add <Replica> there

         */


        return new GameDialogue(root);
    }
}
