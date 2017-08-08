package main.misc.news;

import main.data.filesys.PathFinder;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 8/6/2017.
 */
public class NewsLogger {
    private static String logText = null;

    public static void articleChecked(NewsArticle article) {
        logText += "Checked - link:" +
         article.getLink() +
         "; Name: " + article.getName() +
         "\n";
    }

    public static String getLogText() {
        if (logText == null) {
            logText = "\nKeywords: " + NewsFilterer.keywords.toString();
            logText = "\nEmails: " + NewsAlerter.getEmails().toString();
            logText = "\nWebsites: " + NewsReader.getWebsites().toString();
        }
        return logText;
    }

    public static void articleSpotted(String keywords, String website, NewsArticle article) {
        logText += website + "'s article (link - " + article.getLink() +
         ") contains " + keywords + "\n";

    }

    public static void writeLog() {
//        FileManager.getUniqueFileVersion()
        FileManager.write(logText, getLogFilePath());
        logText = "";
    }

    private static String getLogFilePath() {
        return PathFinder.getXML_PATH() + "logs//log from " + TimeMaster.getDateString() + " at " +
         TimeMaster.getFormattedTime() +
         ".txt";
    }
}
