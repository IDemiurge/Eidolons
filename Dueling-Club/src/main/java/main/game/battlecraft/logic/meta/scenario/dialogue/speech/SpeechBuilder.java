package main.game.battlecraft.logic.meta.scenario.dialogue.speech;

import main.data.dialogue.SpeechData;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/18/2017.
 */
public class SpeechBuilder {
    Map<Integer, String> idToXmlMap;
    Map<Integer, SpeechData> idToDataMap;
    private String linesPath;

    public SpeechBuilder(String linesPath) {
        this.linesPath = linesPath;
    }
    public SpeechBuilder() {
        this(PathFinder.getEnginePath()+ DialogueLineFormatter.getLinesFilePath());
    }

    public Speech buildSpeech(Speech speech) {
        int id = speech.getId();
        speech.setUnformattedText(getIdToXmlMap().get(id));
        SpeechData data = idToDataMap .get(id);
        speech.setData(data);
        return speech;
    }

    public Map<Integer, String> getIdToXmlMap() {
        if (idToXmlMap==null )
            construct();
        return idToXmlMap;
    }

    private void construct() {
        idToXmlMap = new HashMap<>();
        idToDataMap = new HashMap<>();
        String xml = FileManager.readFile(linesPath);
        Document doc = XML_Converter.getDoc(xml);
        for (Node node : XML_Converter.getNodeList(doc.getFirstChild())) {
            String idString = node.getNodeName();
            int id = StringMaster.getInteger(idString.replace(DialogueLineFormatter. ID, ""));
            idToXmlMap.put(id, node.getTextContent());
            if (node.hasChildNodes()){
                SpeechData data = new SpeechData();
                for (Node subNode : XML_Converter.getNodeList(node)) {
                    data.setValue(
                       subNode.getNodeName(), subNode.getTextContent() );
                }
                idToDataMap.put(id, data);
            }
//            StringMaster.getnum
        }

    }
}
