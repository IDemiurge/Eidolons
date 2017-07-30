package main.misc.news;

import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsManager {
    private static final String KEYWORDS_PATH = "keywords.txt";
    private static final String EMAILS_PATH = "emails.txt";
    private static final String WEBSITES_PATH = "websites.txt";


    static long watchPeriod;
    private static boolean jar;
    private static boolean cyrillic = true;


    public static void main(String[] websitesToWatch) {
        start();
    }

    public static void start() {
        readConfig();
//        NewsFilterer.setFilterKeywords(filterKeywords);
        NewsReader.watch();
//        if (!continuous)
//            return ;
//        Timer timer = new Timer();
//        timer.schedule(getTask(), watchPeriod);
    }

    private static void readConfig() {
        List<String> list = StringMaster.openContainer(
         readFile(getKeywordsPath(),cyrillic), "#");
        NewsFilterer.setKeywords(list);
        list = StringMaster.openContainer(
         readFile(getWebsitesPath(),false), "\n");
        NewsReader.setWebsites(list);
        list = StringMaster.openContainer(
         readFile(getEmailsPath(),false), "\n");
        NewsAlerter.setEmails(list);
    }

    private static String readFile(String path, boolean cyrillic) {
        if (cyrillic)
            return CyrillicReader.read(path);
        return FileManager.readFile(path);
    }

    private static String getKeywordsPath() {
        if (!jar) {
            return PathFinder.getEnginePath() + "xml\\" + KEYWORDS_PATH;
        }
        return PathFinder.getEnginePath() + KEYWORDS_PATH;
    }

    private static String getEmailsPath() {
        if (!jar) {
            return PathFinder.getEnginePath() + "xml\\" + EMAILS_PATH;
        }
        return PathFinder.getEnginePath() + KEYWORDS_PATH;
    }

    private static String getWebsitesPath() {
        if (!jar) {
            return PathFinder.getEnginePath() + "xml\\" + WEBSITES_PATH;
        }
        return PathFinder.getEnginePath() + KEYWORDS_PATH;
    }

    private static TimerTask getTask() {
        return new TimerTask() {
            @Override
            public void run() {
                NewsReader.watch();
            }
        };
    }


    public static String getEncoding() {
        if (cyrillic)
            return CyrillicReader.ENCODING;
        return "UTF-8";
    }
}
