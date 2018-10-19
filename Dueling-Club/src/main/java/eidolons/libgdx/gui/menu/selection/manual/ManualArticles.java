package eidolons.libgdx.gui.menu.selection.manual;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import main.entity.Entity;
import main.system.auxiliary.StringMaster;

import java.util.Collection;
import java.util.List;

/**
 * Created by JustMe on 12/5/2017.
 */
public class ManualArticles extends ItemListPanel {


    @Override
    public List<SelectableItemData> toDataList(Collection<? extends Entity> objTypes) {
        return super.toDataList(objTypes);
    }

    @Override
    public List<SelectableItemData> getItems() {
        return super.getItems();
    }

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

        public String name = StringMaster.getWellFormattedString(name());

        public String getArticleFileName() {
            return name + ".txt";
        }

        public String getPreview() {
            return null;
        }

        public String getFullImage() {
            return null;
//            return StrPathBuilder.build("big","manual",name, ".png");
        }
    }


}
