package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueSyntax;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.elements.conditions.Condition;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.TEST_MODE;

/**
 * Created by JustMe on 5/18/2017.
 */
public class SpeechBuilder {
    Map<Integer, SpeechData> idToDataMap;
    private String linesPath;
    MetaGameMaster master;

    public SpeechBuilder(String linesPath, MetaGameMaster master) {
        this.linesPath = linesPath;
        this.master = master;
    }

    public Speech buildSpeech(Speech speech) {
        int id = speech.getId();
        SpeechData data = getIdToDataMap().get(id);
        speech.setData(data);
        String text = data.getValue(SPEECH_VALUE.MESSAGE);
        speech.setUnformattedText(text);

        text = processText(text, speech);

        speech.setFormattedText(text);
        return speech;
    }

    protected String processText(String text, Speech speech) {
        if (text.split(DialogueSyntax.SCRIPT).length == 1 ) {
            return text;
        }
        String metaData =  text.split(DialogueSyntax.SCRIPT)[1];
        text = text.split(DialogueSyntax.SCRIPT) [0];
        try {
            Condition reqs = DialogueSyntax.getConditions(metaData);
            speech.setConditions(reqs);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
//        try { TODO
//            Abilities abils = DialogueSyntax.getAbilities(text);
//            speech.setAbilities(abils);
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }

        String part = metaData.substring(0, metaData.indexOf(DialogueSyntax.SCRIPT_CLOSE));// DialogueSyntax.getScriptPart(metaData);
        if (!StringMaster.isEmpty(part)) {
            SpeechScript script = new SpeechScript(part, master);
            speech.setScript(script);
            Integer time = script.getIntValue(SpeechScript.SPEECH_ACTION.TIME_THIS);
            if (time != 0) {
                speech.setTime(time);
            }
        }
//            Integer time = DialogueSyntax.getTime(metaData);
//            speech.setTime(time);

        return text;
    }

    public Map<Integer, SpeechData> getIdToDataMap() {
        if (idToDataMap == null)
            construct();
        return idToDataMap;
    }


    private void construct() {
        idToDataMap = new HashMap<>();
        String xml = FileManager.readFile(linesPath);
        Document doc = XML_Converter.getDoc(xml);
        for (Node node : XML_Converter.getNodeList(doc.getFirstChild())) {
            String idString = node.getNodeName();
            int id = NumberUtils.getInteger(idString.replace(DialogueLineFormatter.ID, ""));

            if (node.hasChildNodes()) {
                SpeechData data = new SpeechData();
                for (Node subNode : XML_Converter.getNodeList(node)) {
                    String value = subNode.getTextContent();
                    value = XML_Formatter.restoreXmlNodeText(value);
                    data.setValue(
                            subNode.getNodeName(), value);
//                    if ()
//                        idToXmlMap.put(id, node.getTextContent());
                }
                idToDataMap.put(id, data);
            }
//            StringMaster.getnum
        }

    }
}
