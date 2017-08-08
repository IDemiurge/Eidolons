package main.misc.news;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsReader {
    private static List<String> websites;

    public static void watch() {
        for (String sub : websites) {
            try {
                watch(sub);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NewsAlerter.sendAlerts();
        NewsLogger.writeLog();
    }

    public static void watch(String website) {
        String content = getHtmlFromLink(website);

        List<NewsArticle> articles =
         NewsParser.parse(content, website);
        for (NewsArticle article : articles) {
            NewsLogger.articleChecked(article);
           String keywords= NewsFilterer.
             getKeywordsPresent(article.getContents());
            if (!keywords.isEmpty() ) {
                NewsAlerter.spotted(article, keywords, website);
            }
        }
    }

    public static String getHtmlFromLink(String link) {
        String content = null;
        URL url = null;
        try {
            url = new URL(link);

            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
            encoding = encoding == null ?
//             NewsManager.getEncoding()
             "UTF-8": encoding;
            content = IOUtils.toString(in, encoding);
//            System.out.println(content);
//            NewsLogger.logRaw(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static List<String> getWebsites() {
        return websites;
    }

    public static void setWebsites(List<String> websites) {
        NewsReader.websites = websites;
    }


}
