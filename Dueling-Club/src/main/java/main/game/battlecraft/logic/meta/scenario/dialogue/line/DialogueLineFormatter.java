package main.game.battlecraft.logic.meta.scenario.dialogue.line;

import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XML_Writer;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.text.TextMaster;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/18/2017.
 */
public class DialogueLineFormatter {
    public static final String ID = "Id";
    public static final String ACTOR_SEPARATOR = "::";
    public static final String DIALOGUE_SEPARATOR = "***";
    public static final String LINE_SEPARATOR = ">>";
    private static final String dialogueTextPath = "\\dialogue\\raw\\";
    private static final String linearDialoguePath = "\\dialogue\\linear dialogues.xml";
    private static final String introsPath = "\\dialogue\\intros.xml";
    private static final String linesFilePath = "\\dialogue\\lines.xml";
    private static final String linesFilePathIntros = "\\dialogue\\lines - intros.xml";
    private static final String linesBackupFilePath = "\\dialogue\\backup\\lines.xml";
    private static final String ACTOR_NODE = SPEECH_VALUE.ACTOR.name();
    private static final String TEXT_NODE = SPEECH_VALUE.MESSAGE.name();
    private static String oldLinesFileContents="";
    private static String newLinesFileContents="";
    private static  String linearDialogueFileContents="";
    private static Map<Integer, Integer> updateIdMap;
    private static int id;

    public static void main(String[] args) {
        fullUpdate();
    }

    public static void parseDialogue() {

    }

    public static void updateXml() {

    }

    public static void fullUpdate() {
        id = 0;
        readLinesFile();
        createUpdateMap();
        for (File file : FileManager.getFilesFromDirectory(getDialogueTextPath(), false, true)) {
            parseDialogueFile(FileManager.readFile(file));
        }
        createBackup();
        writeLinesFile();
        writeLinearDialoguesFile();
        updateXml();
//        new DialogueFactory().constructScenarioLinearDialogues(getLinearDialoguesFilePath(), new ScenarioMetaMaster(""));
      }
    public static String formatDialogueText(String result) {
        return result.replaceAll("…", "...")
         .replaceAll("’", "'");
    }
    public static void parseDialogueFile(String contents) {
        //odt from textMaster!

        for (String dialogueContents : StringMaster.openContainer(contents, DIALOGUE_SEPARATOR)) {
            boolean dialogue = true;
            for (String lineText : StringMaster.openContainer(dialogueContents, LINE_SEPARATOR)) {
                if (dialogue) {
                    linearDialogueFileContents += DialogueFactory.DIALOGUE_SEPARATOR + lineText.trim() + DialogueFactory.ID_SEPARATOR;
                    linearDialogueFileContents += id + DialogueFactory.ID_SEPARATOR;
                    dialogue = false;
                    continue;
                }
                String actorData =
                 StringMaster.tryGetSplit(lineText, ACTOR_SEPARATOR, 0);
                if (!actorData.isEmpty())
                    actorData = XML_Converter.wrap(ACTOR_NODE, actorData.trim());
                String textData = StringMaster.tryGetSplit(lineText, ACTOR_SEPARATOR, 1);

                textData = formatDialogueText(textData);
                 textData =XML_Converter.wrap(TEXT_NODE,
                 XML_Formatter.formatXmlTextContent(textData, null ));

                String miscData = "";
                String text = actorData;
                text += miscData;
                text += textData;

                String xml = getLineFromTextPart(text);
                newLinesFileContents += xml;
                id++;
//            lineCreated(id, lineText);
                //IDEA: keep a copy of previous raw\\ folder and match?
            }

            if (id != 0) {
                linearDialogueFileContents += id  +StringMaster.NEW_LINE;
            }
        }

    }

    public static String getLineFromTextPart(String text) {
        String lineContents = XML_Converter.wrap(ID + id, text) + StringMaster.NEW_LINE;
        return lineContents;
    }

    private static void createUpdateMap() {
        updateIdMap = new HashMap<>(); // uuid??
        // on newLine() ->

    }

    private static void writeLinesFile() {
        XML_Writer.write(
         XML_Converter.wrap("Lines", newLinesFileContents), getLinesFilePath());
    }
    private static void writeLinearDialoguesFile() {
        XML_Writer.write(linearDialogueFileContents, getLinearDialoguesFilePath());
    }



    private static void createBackup() {
        XML_Writer.write(oldLinesFileContents, getLinesBackupFilePath());
    }

    public static String readLinesFile() {
        oldLinesFileContents = FileManager.readFile(getLinesFilePath());
        return oldLinesFileContents;
        //how to update?
    }

    public static String getLinearDialoguesFilePath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath()
         + TextMaster.getLocale() + linearDialoguePath;
    }
    public static String getIntrosFilePath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath()
         + TextMaster.getLocale() + introsPath;
    }
    public static String getDialogueTextPath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath() + TextMaster.getLocale() + dialogueTextPath;
    }

    public static String getLinesFilePath() {
        return PathFinder.getTextPath() + TextMaster.getLocale() + linesFilePath;
    }

    public static String getLinesBackupFilePath() {
        return PathFinder.getTextPath() + TextMaster.getLocale() + linesBackupFilePath;
    }

    public static String getLinesFilePathIntro() {
        return PathFinder.getTextPath() + TextMaster.getLocale() + linesFilePathIntros;
    }
}
