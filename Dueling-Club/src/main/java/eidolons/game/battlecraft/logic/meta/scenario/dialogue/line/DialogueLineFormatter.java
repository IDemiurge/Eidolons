package eidolons.game.battlecraft.logic.meta.scenario.dialogue.line;

import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.system.text.TextMaster;
import main.data.dialogue.DataString.SPEECH_VALUE;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XML_Writer;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;

/**
 * Created by JustMe on 5/18/2017.
 */
public class DialogueLineFormatter {
    public static final String ID = "Id";
    public static final String ACTOR_SEPARATOR = "::";
    public static final String DIALOGUE_SEPARATOR = "***";
    public static final String LINE_SEPARATOR = ">>";
    private static final String dialogueTextPath = "\\raw\\";
    private static final String linearDialoguePath = "\\linear dialogues.xml";
    private static final String introsPath = "\\intros.xml";
    private static final String linesFilePath = "\\lines.xml";
    //    private static final String linesFilePathIntros = "\\lines - intros.xml";
    private static final String ACTOR_NODE = SPEECH_VALUE.ACTOR.name();
    private static final String TEXT_NODE = SPEECH_VALUE.MESSAGE.name();
    private static final String INTRO_IDENTIFIER = "Intro:";
    private static String newLinesFileContents = "";
    private static String linearDialogueFileContents = "";
    private static String introsFileContents = "";
    private static int id;

    public static void main(String[] args) {
        fullUpdate();
    }


    public static void generate() {

    }

    public static void fullUpdate() {
        //TODO TUTORIAL?
        for (File scenarioFolder : FileManager.getFilesFromDirectory(
         PathFinder.getEnginePath() + PathFinder.getScenariosPath()
         , false, true)) {
            //TODO scenario intro?
            for (File missionFolder : FileManager.getFilesFromDirectory(scenarioFolder.getPath()
             , false, true)) {
                if (missionFolder.isDirectory()) {
                    String path = getDialogueTextPath(missionFolder.getName() + dialogueTextPath);
                    parseDocs(path);
                }

            }
        }
        String path = PathFinder.getEnginePath()
         + "tutorial" + StringMaster.getPathSeparator()
         + TextMaster.getLocale()
         + dialogueTextPath;
        parseDocs(path);
    }

    public static String getDialogueTextPath(String fileName) {
        return PathFinder.getEnginePath() + PathFinder.getScenariosPath()
         + fileName + StringMaster.getPathSeparator()
         + TextMaster.getLocale()
         ;
    }

    public static void parseDocs(String path) {
        id = 0;
        for (File file : FileManager.getFilesFromDirectory(path, false, true)) {
            parseDialogueFile(FileManager.readFile(file));
        }
        XML_Writer.write(XML_Converter.wrap("Lines", newLinesFileContents), getLinesFilePath(path));
        XML_Writer.write(linearDialogueFileContents, getLinearDialoguesFilePath());
        XML_Writer.write(introsFileContents, getIntrosFilePath());
//        new DialogueFactory().constructScenarioLinearDialogues(getLinearDialoguesFilePath(), new ScenarioMetaMaster(""));
    }

    public static String formatDialogueText(String result) {
        return result.replaceAll("…", "...")
         .replaceAll("’", "'");
    }

    //odt from textMaster!
/* FORMAT EXAMPLE:
***Interrogation
>> Billy:: Please, mercy, my lords… I didn’t want to fight anyone, I’m just a thief…
>> Gwyn:: He could have run.
***Interrogation2
>> Gwyn::  Either he is too desperate to think clearly, or he is not a coward.
...
 */
    public static void parseDialogueFile(String contents) {
        for (String dialogueContents : StringMaster.open(contents, DIALOGUE_SEPARATOR)) {
            boolean dialogue = true;
            for (String lineText : StringMaster.open(dialogueContents, LINE_SEPARATOR)) {
                boolean intro = lineText.contains(INTRO_IDENTIFIER);
                if (dialogue) {
                    //TODO check intro!
                    if (intro) {
                        lineText = lineText.replace(INTRO_IDENTIFIER, "");
                        introsFileContents += DialogueFactory.DIALOGUE_SEPARATOR + lineText.trim() + DialogueFactory.ID_SEPARATOR;
                        introsFileContents += id + DialogueFactory.ID_SEPARATOR;
                    } else {
                        linearDialogueFileContents += DialogueFactory.DIALOGUE_SEPARATOR + lineText.trim() + DialogueFactory.ID_SEPARATOR;
                        linearDialogueFileContents += id + DialogueFactory.ID_SEPARATOR;
                    }
                    dialogue = false;
                    continue;
                }
                String actorData =
                 StringMaster.tryGetSplit(lineText, ACTOR_SEPARATOR, 0);
                if (!actorData.isEmpty())
                    actorData = XML_Converter.wrap(ACTOR_NODE, actorData.trim());
                String textData = StringMaster.tryGetSplit(lineText, ACTOR_SEPARATOR, 1);

                textData = formatDialogueText(textData);
                textData = XML_Converter.wrap(TEXT_NODE,
                 XML_Formatter.formatXmlTextContent(textData, null));

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
                linearDialogueFileContents += id + StringMaster.NEW_LINE;
            }
        }

    }

    public static String getLineFromTextPart(String text) {
        String lineContents = XML_Converter.wrap(ID + id, text) + StringMaster.NEW_LINE;
        return lineContents;
    }


    public static String getLinearDialoguesFilePath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath()
         + TextMaster.getLocale() + linearDialoguePath;
    }

    public static String getIntrosFilePath() {
        return PathFinder.getEnginePath() + PathFinder.getTextPath()
         + TextMaster.getLocale() + introsPath;
    }


    public static String getLinesFilePath(String root) {
        return root + linesFilePath;
    }


}
