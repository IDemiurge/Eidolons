package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationSequence extends ItemListPanel{


    //additional controls?
    public enum HERO_CREATION_ITEM {
        INTRODUCTION,
        RACE("Human","Elf","Dwarf","Hybrid" ),
        GENDER("Male","Female" ),
        PORTRAIT,
        PERSONALITY,
        DEITY,
        SKILLSET,;

        String[] subItems;

        HERO_CREATION_ITEM(String... subItems) {
            this.subItems = subItems;
        }

        public String[] getSubItems() {
            return subItems;
        }
    }


}
