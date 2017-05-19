package main.game.battlecraft.logic.meta.scenario.dialogue.line;

import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Writer;
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
    public static final String actorSeparator = "::";
    public static final  String dialogueSeparator = "***";
    public static final  String lineSeparator = ">>";
    private static final String dialogueTextPath="\\dialogue\\raw\\";
    private static final String linesFilePath="\\dialogue\\lines.xml";
    private static final String linesBackupFilePath="\\dialogue\\backup\\lines.xml";

    private static String oldLinesFileContents;
    private static String newLinesFileContents;
    private static  Map<Integer, Integer> updateIdMap;
    private static int id;



    public static String getDialogueTextPath() {
        return PathFinder.getTextPath()+ TextMaster.getLocale() + dialogueTextPath;
    }

    public static String getLinesFilePath() {
        return PathFinder.getTextPath()+ TextMaster.getLocale() + linesFilePath;
    }

    public static String getLinesBackupFilePath() {
        return PathFinder.getTextPath()+ TextMaster.getLocale() + linesBackupFilePath;
    }
    public static void parseDialogue(){

    }
        public static void fullUpdate(){
        id = 0;
        readLinesFile();
        createUpdateMap();
        for (File file : FileManager.getFilesFromDirectory(dialogueTextPath, false, true)) {
            parseDialogueFile(FileManager.readFile(file));
        }
        createBackup();
        writeLinesFile();
        updateXml();
    }

    public static void parseDialogueFile(String contents){
        //odt from textMaster!

        for(String lineText: StringMaster.openContainer( contents)){
//            lineCreated(id, lineText);
            //IDEA: keep a copy of previous raw\\ folder and match?
            id++;

        }

    }
    private static void createUpdateMap() {
        updateIdMap = new HashMap<>(); // uuid??
        // on newLine() ->

    }

    private static void writeLinesFile() {
        XML_Writer.write(newLinesFileContents, getLinesFilePath());
    }

    private static void createBackup() {
        XML_Writer.write(oldLinesFileContents, getLinesBackupFilePath());
    }

    private static void readLinesFile() {
        oldLinesFileContents = FileManager.readFile(getLinesFilePath());
        //how to update?
    }

    public static void updateXml(){

    }
    public static String getLineFromTextPart( String text){
        String lineContents= text;
        //actor etc, format, replace names, ...
        XML_Converter.wrap(ID + id, lineContents);

        return lineContents;
    }

}
