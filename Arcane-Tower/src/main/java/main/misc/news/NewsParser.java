package main.misc.news;

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
        Elements pngs = doc.select("img[src$=.png]");
// img with src ending .png

        Element masthead = doc.select("div.masthead").first();
        links.forEach(linkObject -> {
            String name = linkObject.text();
            String link = linkObject.attr("href");
            String contents = NewsReader.getHtmlFromLink(link);
            if (content == null) {
                link = website + link;
            }
            contents = NewsReader.getHtmlFromLink(link);
            articles.add( new NewsArticle(name, link, contents));
        });

        return articles;
    }
}
