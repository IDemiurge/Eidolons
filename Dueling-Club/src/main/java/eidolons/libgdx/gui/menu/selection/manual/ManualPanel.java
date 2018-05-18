package eidolons.libgdx.gui.menu.selection.manual;

import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.menu.selection.SelectionPanel;
import eidolons.libgdx.gui.menu.selection.manual.ManualArticles.MANUAL_ARTICLE;
import eidolons.system.text.TextMaster;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 12/5/2017.
 */
public class ManualPanel extends SelectionPanel {
    @Override
    protected ItemInfoPanel createInfoPanel() {
        return new ManualDetails(null);
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
            String name = sub.split(StringMaster.NEW_LINE)[1];
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
