package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueSyntax;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.line.DialogueLineFormatter;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.system.text.Texts;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.dialogue.SpeechData;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.elements.conditions.Condition;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

import static eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechScript.SCRIPT_KEY;

/**
 * Created by JustMe on 5/18/2017.
 */
public class SpeechBuilder {
    Map<Integer, SpeechData> idToDataMap;
    private final String linesPath;
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
        if (text.split(DialogueSyntax.SCRIPT_QUOTE).length == 1) {
            return text;
        }
        String metaData = text.split(DialogueSyntax.SCRIPT_QUOTE)[1];
        text = text.split(DialogueSyntax.SCRIPT_QUOTE)[0];
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
        part = part.trim();
        if (!StringMaster.isEmpty(part)) {
            part = processData(part);
            if (!StringMaster.isEmpty(part)) {
                part = part.trim();
                SpeechScript script = new SpeechScript(part, master);
                speech.setScript(script);
                Integer time = script.getIntValue(SpeechScript.SCRIPT.TIME_THIS);
                if (time != 0) {
                    speech.setTime(time);
                }
            }
        }
//            Integer time = DialogueSyntax.getTime(metaData);
//            speech.setTime(time);

        return text;
    }

    private static String processData(String data) {
        if (data.contains(SCRIPT_KEY)) {
            String key = data.split(SCRIPT_KEY)[1];
            data = Texts.getTextMap("scripts").get(key);
        }

        return data;
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
        for (Node node : XmlNodeMaster.getNodeList(doc.getFirstChild())) {
            String idString = node.getNodeName();
            int id = NumberUtils.getIntParse(idString.replace(DialogueLineFormatter.ID, ""));

            if (node.hasChildNodes()) {
                SpeechData data = new SpeechData();
                for (Node subNode : XmlNodeMaster.getNodeList(node)) {
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
