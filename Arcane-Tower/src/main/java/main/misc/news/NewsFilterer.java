package main.misc.news;

import main.system.auxiliary.StringMaster;

import java.util.List;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsFilterer {
    static List<String> keywords;

    public static boolean checkAnyKeywordPresent(String content) {
        for (String sub : keywords) {
            if (StringMaster.contains(content, sub, true, false))
                return true;
        }
        return false;
    }

    public static void setKeywords(List<String> keywords) {
        NewsFilterer.keywords = keywords;
    }

    public static String getKeywordsPresent(String contents) {
        String keywordsPresent="";
        for (String sub : keywords) {
            if (StringMaster.contains(contents, sub, true, false))
               keywordsPresent+=sub+", ";
        }
        return keywordsPresent;
    }
}
