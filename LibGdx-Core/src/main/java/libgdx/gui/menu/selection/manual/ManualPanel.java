package libgdx.gui.menu.selection.manual;

import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.SelectionPanel;
import libgdx.gui.menu.selection.manual.ManualArticles.MANUAL_ARTICLE;
import eidolons.content.consts.Sprites;
import eidolons.system.text.TextMaster;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 12/5/2017.
 */
public class ManualPanel extends SelectionPanel {
    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new ManualDetails(null);
    }
    protected String getDoneText() {
        return "Understood!";
    }
    @Override
    protected List<SelectableItemData> createListData() {
        List<SelectableItemData> list = new ArrayList<>();
        String path =
         StrPathBuilder.build(
          PathFinder.getTextPath(),
          TextMaster.getLocale(),
          getArticlesPath(), "Manual.txt");
        String text = FileManager.readFile(path);
        String[] articles = text.split("---");
        for (String sub : articles) {
            String name = sub.split(Strings.NEW_LINE)[1];
            MANUAL_ARTICLE article =
             new EnumMaster<MANUAL_ARTICLE>().retrieveEnumConst(MANUAL_ARTICLE.class, name);
            if (article == null) {
                continue;
            }
            text = sub.replaceFirst(name, "");
            list.add(new SelectableItemData(article.name, text,
             article.getPreview(), article.getFullImage()));
        }

        return list;
    }

    @Override
    protected String getBackgroundSpritePath() {
        return Sprites.BG_DEFAULT;
    }

    @Override
    protected float getBgAlpha() {
        return 0.8f;
    }

    private String getArticlesPath() {
        return "manual";
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ManualArticles();
    }

    public void close() {
        GuiEventManager.trigger(GuiEventType.SHOW_MANUAL_PANEL,
         null);
    }

    protected boolean isDoneSupported() {
        return true;
    }

    protected String getTitle() {
        return "Game Manual";
    }

}
