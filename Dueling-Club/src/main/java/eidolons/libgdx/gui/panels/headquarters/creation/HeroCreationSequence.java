package eidolons.libgdx.gui.panels.headquarters.creation;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import main.content.enums.GenericEnums.ASPECT;
import main.system.auxiliary.EnumMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationSequence extends ItemListPanel{

    @Override
    protected boolean clicked(TextButton textButton, SelectableItemData sub) {
       HERO_CREATION_ITEM item=getSequenceItem(sub);
       HeroCreationMaster.setCurrentItem(item);
        return super.clicked(textButton, sub);
    }

    private HERO_CREATION_ITEM getSequenceItem(SelectableItemData sub) {
    return   new EnumMaster<HERO_CREATION_ITEM>().retrieveEnumConst(HERO_CREATION_ITEM.class, sub.getName());
    }

    @Override
    protected void showSubItemPanel(SelectableItemData item) {
        super.showSubItemPanel(item);
        if (isSelectZerothSubItem(item))
            subItemClicked(item, item.getSubItems()[0]);
    }

    private boolean isSelectZerothSubItem(SelectableItemData item) {
        switch (getSequenceItem(item)) {
            case DEITY:
                return true;
        }
        return false;
    }

    @Override
    public boolean isBlocked(SelectableItemData item) {
        if (HeroCreationMaster.TEST_MODE)
            return false;
        int i =getItems().indexOf(item);
        int i1 = getItems().indexOf(
         HeroCreationMaster.getCurrentItem());

        int dif = i - i1;
        if (dif<=0)
            return false;
        if (dif==1)
        if (HeroCreationMaster.isCurrentItemDone()){
            return false;
        }
        return true;
    }

    //additional controls?
    public enum HERO_CREATION_ITEM {
        INTRODUCTION,
        RACE("Human","Elf","Dwarf","Hybrid" ),
        GENDER("Male","Female" ),
        PORTRAIT,
        PERSONALITY,
        DEITY(Arrays.stream(ASPECT.values()).map(aspect -> aspect.toString())
         .collect(Collectors.toList()).toArray(new String[ASPECT.values().length])),
//        STATS,
        SKILLSET,
        FINALIZE,
        ;

        String[] subItems;

        HERO_CREATION_ITEM(String... subItems) {
            this.subItems = subItems;
        }

        public String[] getSubItems() {
            return subItems;
        }
    }


}
