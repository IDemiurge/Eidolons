package main.misc.news;

/**
 * Created by JustMe on 7/31/2017.
 */
public class NewsArticle {
    String name;
    String link;
    String contents;

    public NewsArticle(String name, String link, String contents) {
        this.name = name;
        this.link = link;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
