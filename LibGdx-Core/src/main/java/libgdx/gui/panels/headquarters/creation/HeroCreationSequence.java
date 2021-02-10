package libgdx.gui.panels.headquarters.creation;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import libgdx.GdxMaster;
import main.content.enums.GenericEnums.ASPECT;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/5/2018.
 */
public class HeroCreationSequence extends ItemListPanel {

    public HeroCreationSequence() {
        super();
        HcControlPanel controlPanel;
        add(controlPanel = new HcControlPanel()).center().row();
    }

    @Override
    protected boolean clicked(TextButton textButton, SelectableItemData sub) {
        return clicked(textButton, sub, false);
    }
    public boolean clicked(int i, boolean back) {
        return clicked(buttons.get(i), items.get(i), back);
    }
        public boolean clicked(TextButton textButton, SelectableItemData sub, boolean back) {
       if (isBlocked(sub))
            return false;
       HERO_CREATION_ITEM item = getSequenceItem(sub);
       if (!back)
       {
           HeroCreationMaster.setCurrentItem(item);
           if (HeroCreationMaster.HUMAN_ONLY) {
               infoPanel.subItemClicked(sub, "Human");
           }
       }
        return super.clicked(textButton, sub);
    }

    private HERO_CREATION_ITEM getSequenceItem(SelectableItemData sub) {
        return new EnumMaster<HERO_CREATION_ITEM>().retrieveEnumConst(HERO_CREATION_ITEM.class, sub.getName());
    }

    protected int getDefaultHeight() {
        return GdxMaster.getHeight();
    }

    @Override
    public void updateAct(float delta) {
        if (buttons.isEmpty()) {
            addButtons();
        }
        int i = 0;
        for (SelectableItemData item : items) {
            TextButton button = buttons.get(i++);
            if (i > 1)
                if (isBlocked(item)) {
                    button.setDisabled(true);
                    continue;
                }
            boolean done = HeroCreationMaster.checkItemIsDone(
             new EnumMaster<HERO_CREATION_ITEM>().
              retrieveEnumConst(HERO_CREATION_ITEM.class, item.getName()));

            button.setDisabled(false);
            button.setChecked(done);
            if (i > 0)
                if (!done)
                    if (buttons.get(i - 2).isChecked())
                        if (HeroCreationMaster.FAST_MODE) {
//                            Eidolons.onThisOrNonGdxThread(() ->
//                            {
//                                WaitMaster.WAIT(250);
//                                Gdx.app.postRunnable(() -> {
                                    button.getClickListener().clicked(null, 0, 0 );
//                                });
//                            });

                        }
        }
    }

    @Override
    protected void showSubItemPanel(SelectableItemData item) {
        super.showSubItemPanel(item);
        if (isSelectZerothSubItem(item))
            subItemClicked(item, item.getSubItems()[0]);
    }

    private boolean isSelectZerothSubItem(SelectableItemData item) {
        return false;
    }

    @Override
    public boolean isBlocked(SelectableItemData item) {
        if (HeroCreationMaster.TEST_MODE)
            return false;
        int i = getItems().indexOf(item);
        int i1 = getItems().stream().map(this::getSequenceItem).collect(Collectors.toList())
         .indexOf(
          HeroCreationMaster.getCurrentItem());

        int dif = i - i1;
        if (dif <= 0)
            return false;
        if (dif == 1)
            return !HeroCreationMaster.isCurrentItemDone();
        return true;
    }

    //additional controls?
    public enum HERO_CREATION_ITEM {
        INTRODUCTION,
        RACE("Human", "Elf", "Dwarf", "Goblinoid", "Demon", "Vampire"){
            @Override
            public String[] getSubItems() {
                if (HeroCreationMaster.HUMAN_ONLY){
                    return null;
                }
                return super.getSubItems();
            }
        },
        GENDER(), //"Male", "Female" now w/o subpanel
        PORTRAIT,
        PERSONALITY,
        DEITY(Arrays.stream(ASPECT.values()).map(Enum::toString)
         .collect(Collectors.toList()).toArray(new String[ASPECT.values().length])),
        //        STATS,
        SKILLSET,
        FINALIZE,;

        String[] subItems;

        HERO_CREATION_ITEM(String... subItems) {
            this.subItems = subItems;
        }

        @Override
        public String toString() {
            return StringMaster.format(name());
        }

        public String[] getSubItems() {
            return subItems;
        }
    }


}
