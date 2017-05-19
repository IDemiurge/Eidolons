package main.game.battlecraft.logic.meta.scenario.dialogue.speech;

import main.data.xml.XML_Converter;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by JustMe on 5/18/2017.
 */
public class SpeechBuilder {
    Map<Integer, String> idToXmlMap;

    public Speech buildSpeech(Speech speech) {
        int id = speech.getId();
        speech.setUnformattedText(getIdToXmlMap().get(id));
//        SpeechData data = getIdToDataMap().get(id);
//        speech.setData(data);
        return speech;
    }

    public Map<Integer, String> getIdToXmlMap() {
        construct();
        return idToXmlMap;
    }

    private void construct() {
        String xml = FileManager.readFile(DialogueLineFormatter.getLinesFilePath());
        Document doc = XML_Converter.getDoc(xml);
        for (Node node : XML_Converter.getNodeList(doc)) {
            if (node.hasChildNodes()){

            }
            String idString = node.getNodeName();
            int id = StringMaster.getInteger(idString.replace(DialogueLineFormatter. ID, ""));
            idToXmlMap.put(id, node.getTextContent());
//            StringMaster.getnum
        }

    }
}
