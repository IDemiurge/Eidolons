package main.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/31/2017.
 */
public class NewsParser {
    static String blockedWebsites = "facebook";

    public static NewsArticle getArticleFromLink(Element linkObject, String website) {
        String link = linkObject.attr("href");
        if (!link.contains("http") && !link.contains("www.")) {
            link = (website + link);
        }
        String contents = NewsReader.getHtmlFromLink(link);
        if (contents == null)
            return null;
        String name = linkObject.text();

//        if (!checkLink(link)) continue;

        return new NewsArticle(name, link, contents);
    }

    public static Elements getLinks(String content) {
        return getElements(content, "a[href]");
    }
    public static Elements getElements(String content, String identifier) {
        Document doc = Jsoup.parse(content, "UTF-8");
        return doc.select(identifier);
    }

    public static List<NewsArticle> parse(String content, String website) {
        List<NewsArticle> articles = new ArrayList<>();
        Elements links = getLinks(content); // a with href

        for (Element linkObject : links) {
            NewsArticle a = getArticleFromLink(linkObject, website);
            if (a == null)
                continue;
            articles.add(a);
            Elements sublinks = getLinks(a.getContents());
//            articles.addAll(parse(contents, website));
            for (Element sub : sublinks) {
                if (!checkLink(sub))
                    continue;
                a = getArticleFromLink(sub, website);
                if (a == null)
                    continue;
                NewsFilterer.checkArticle(a, website);
                articles.add(a);
            }
        }

        return articles;
    }

    private static boolean checkLink(Element link) {

        String text = link.text();
        for (char sub : text.toCharArray()) {
            if (Character.UnicodeBlock.of(sub).equals(Character.UnicodeBlock.CYRILLIC))
                return true;
        }
//        for (String blocked : StringMaster.open(blockedWebsites)) {
//            if (StringMaster.contains(link, blocked))
//                return false;
//        }
        return false;
    }
}
