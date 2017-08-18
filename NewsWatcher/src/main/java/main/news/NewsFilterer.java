package main.news;

import main.system.auxiliary.StringMaster;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsFilterer {
    public static List<String> keywords;
    static String[] watchedTags = {
     "title" ,
     "meta" ,
     "div" ,
     "a"
    };
    static String[] watched  = {
     "<title>" ,
     "<meta>" ,
     "<div>" ,
     "<a>"
    };
    static String[] watchedTagsClosed = {
     "</title>" ,
     "</meta>" ,
     "</div>" ,
     "</a>"
    };
    static  String delimiters= StringMaster.join("|", watchedTags);
    static  String delimitersClosed= StringMaster.join("|", watchedTagsClosed);
/*

 */
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
        String relevant="";
        for (String sub: watchedTags)
        {
            Elements elements = NewsParser.getElements(contents, sub);
            for (Element e : elements) {
              relevant+=  e.text();
            }
        }
//        String[] pieces = contents.split(delimiters);
//        for (String sub : pieces) {
//            String[] subParts = sub.split(delimitersClosed);
//            for (String subPart : subParts) {
//                 relevant+= subPart;
//                break;
//            }
//        }
        /*

         */
        relevant = relevant.toLowerCase();
        for (String sub : keywords) {
            if (relevant.contains(sub))
//            if (StringMaster.contains(contents, sub, true, false))
               keywordsPresent+=sub+", ";
        }
        return keywordsPresent;
    }

    public static void checkArticle(NewsArticle article, String website) {
        NewsLogger.articleChecked(article);
        String keywords= NewsFilterer.
         getKeywordsPresent(article.getContents());
        if (!keywords.isEmpty() ) {
            NewsAlerter.spotted(article, keywords, website);
        }
    }
}
