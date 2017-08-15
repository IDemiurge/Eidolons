package main.misc.news;

import main.system.auxiliary.StringMaster;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 7/31/2017.
 */
public class NewsParser {
    public static List<NewsArticle> parse(String content, String website) {
        List<NewsArticle> articles = new LinkedList<>();
        Document doc = Jsoup.parse(content, "UTF-8");
        Elements links = doc.select("a[href]"); // a with href
//        Elements pngs = doc.select("img[src$=.png]");
// img with src ending .png
//        Element masthead = doc.select("div.masthead").first();


        for (Element linkObject : links) {
            String name = linkObject.text();
            String link = linkObject.attr("href");
            if (!link.contains("http.") && !link.contains("www.")) {
                link = (website + link);
            }if (!checkLink(link)) continue;
            String contents = NewsReader.getHtmlFromLink(link);
            if (content == null)
                continue;
            articles.addAll(parse(contents, website));
            articles.add(new NewsArticle(name, link, contents));
        }

        return articles;
    }
    static String blockedWebsites="facebook";
    private static boolean checkLink(String link) {

        for(String blocked: StringMaster.openContainer( blockedWebsites )){
        if (StringMaster.contains(link, blocked))
            return false;}
        return true;
    }
}
