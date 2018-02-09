package main.libgdx.gui.menu.selection.manual;

import main.entity.Entity;
import main.libgdx.gui.menu.selection.ItemListPanel;
import main.system.auxiliary.StringMaster;

import java.util.List;

/**
 * Created by JustMe on 12/5/2017.
 */
public class ManualArticles extends ItemListPanel {


    public enum MANUAL_ARTICLE {
        Introduction,
        This_release,
        General,
        Exploration,
        Exploration_Advanced,
        Dungeon_Map,
        Combat,
        Attacks,
        Units,
        Controls,;

        public String name=StringMaster.getWellFormattedString(name());

        public String getArticleFileName() {
            return name+".txt";
        }

        public String getPreview() {
            return null;
        }

        public String getFullImage() {
            return null ;
//            return StrPathBuilder.build("big","manual",name, ".png");
        }
    }

    @Override
    public List<SelectableItemData> toDataList(List<? extends Entity> objTypes) {
        return super.toDataList(objTypes);
    }

    @Override
    public List<SelectableItemData> getItems() {
        return super.getItems();
    }


}