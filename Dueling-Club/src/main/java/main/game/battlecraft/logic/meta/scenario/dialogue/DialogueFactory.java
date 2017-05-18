package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.PROPS;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.ability.construct.ConstructionManager;
import main.data.xml.XML_Converter;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.speech.Speech;
import org.w3c.dom.Document;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueFactory {
    public static GameDialogue getDialogue(String data, ScenarioMetaMaster master) {
        ObjType type = DataManager.getType(data, MACRO_OBJ_TYPES.DIALOGUE);
        Document node = XML_Converter.getDoc(type.getProperty(PROPS.DIALOGUE_DATA));
       Speech root= (Speech) ConstructionManager.construct(node);
       //options - skippable,
        root.init(master, null );
        /*
        in xml:
        > constructors will be messed up if I add <Replica> there

         */


        return  new GameDialogue(root);
    }
}
