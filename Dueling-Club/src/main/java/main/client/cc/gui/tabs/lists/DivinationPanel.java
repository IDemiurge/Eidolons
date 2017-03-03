package main.client.cc.gui.tabs.lists;

import main.client.cc.gui.lists.ItemListManager;
import main.client.cc.gui.pages.HC_PagedListPanel.HC_LISTS;
import main.client.cc.logic.spells.DivinationMaster;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.unit.Unit;
import main.swing.components.buttons.CustomButton;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

// display max sd, total sd... 

public class DivinationPanel extends SecondaryItemList {
    public DivinationPanel(Unit hero, ItemListManager itemManager) {
        super(hero, itemManager);

    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    protected boolean isRemovable() {
        return false;
    }

    private void addButton() {
        CustomButton divinationButton = new CustomButton(VISUALS.DIVINATION, "") {
            @Override
            public void handleClick() {
                divine();
            }

            @Override
            protected void playClickSound() {
                SoundMaster.playStandardSound(STD_SOUNDS.SPELL_ACTIVATE);
            }
        };
        add(divinationButton, "@pos center_x-4 0");

        setComponentZOrder(divinationButton, 0);
        setComponentZOrder(list, 1);

    }

    @Override
    protected void addComps() {
        super.addComps();
        addButton();
    }

    private void divine() {
        hero.resetCurrentValues();
        DivinationMaster.removeDivination(hero);
        DivinationMaster.divine(hero);
        refresh();
        // then I have to "clean this up" before DC!
        // perhaps I should give heroes a full divination before combat?
    }

    @Override
    protected PROPERTY getPROP() {
        return PROPS.DIVINED_SPELLS;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.SPELLS;
    }

    @Override
    protected String getTitle() {
        return "Divination";
    }

    @Override
    protected String getPoolText() {
        return null;// DIVINATION CAP?
    }

    @Override
    protected HC_LISTS getTemplate() {
        return HC_LISTS.QUICK_ITEMS;
    }

}
