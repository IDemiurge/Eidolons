package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.math.MathMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueFactory {
    //***Tavern Chat>3>4***Another Chat>5>10 ...
    public static final String DIALOGUE_SEPARATOR = "***";
    public static final String ID_SEPARATOR = ">";
    public  Map<String, GameDialogue> map = new HashMap<>();



        public  void constructDialogues
         (String path, ScenarioMetaMaster master ) {
          String  data = FileManager.readFile(path);
        for (String contents : StringMaster.openContainer(
       data, DIALOGUE_SEPARATOR)) {
            String[] array = contents.split(ID_SEPARATOR);
            String name = array[0];
            int firstId = StringMaster.getInteger(array[1]);
            int lastId = StringMaster.getInteger(array[2]);
            List<Integer> ids = MathMaster.getIntsInRange(firstId, lastId);
            GameDialogue dialogue = createDialogue(StringMaster.joinList(ids), master);
            map.put(name, dialogue);

        }


    }
    //scenario path?
public void init(ScenarioMetaMaster master){
        //shouldn't do all at once, be lazy or on target
     constructDialogues(getFilePath(), master);
//IDEA - just different files, that's it.
    // lines? perhaps id space will be unique for each mission!
    // pathing will be synced with scenario-mission

}

    protected String getFilePath() {
       return  DialogueLineFormatter.getLinearDialoguesFilePath();
    }

    public GameDialogue getDialogue
     (String name) {
        return    map.get(name);
    }


    public  LinearDialogue createDialogue
     (String idSequence, ScenarioMetaMaster master) {
        //ids also are supposed to be built linearly, right?

        //build Speech objects!
//        List<Speech> speechSequence = new LinkedList<>();
        Speech parent = null;
        Speech root = null;
        for (String ID : StringMaster.openContainer(idSequence)) {
            Speech speech = getSpeech(StringMaster.getInteger(ID));

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

    protected Speech getSpeech(Integer integer) {
      return   new Speech(integer);
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
