package eidolons.client.cc.gui.neo.choice;

import eidolons.client.cc.CharacterCreator;
import eidolons.entity.obj.unit.Unit;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.swing.generic.components.list.ListItem;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;

public class DeityChoiceView extends EntityChoiceView implements
 ListCellRenderer<ObjType> {

    private static final String FAITHLESS = "Faithless";

    public DeityChoiceView(ChoiceSequence choiceSequence, Unit hero) {
        super(choiceSequence, hero);
    }

    @Override
    protected int getPageSize() {
        return 6;
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    public String getInfo() {
        return InfoMaster.CHOOSE_DEITY;
    }

    @Override
    protected void applyChoice() {
        CharacterCreator.getHeroManager().saveHero(hero);
        super.applyChoice();
        hero.initDeity();
    }

    protected boolean isSaveHero() {
        return true;
    }

    public boolean isOkBlocked() {
        if (getSelectedIndex() < 0) {
            return false;
        }
        return checkBlocked((getSelectedIndex()));
    }

    @Override
    public boolean checkBlocked(ObjType e) {
        if (StringMaster.isEmpty(hero.getProperty(G_PROPS.DEITY))) {
            return false;
        }
        if (e == null) {
            return true;
        }
        if (e.getName().equals(FAITHLESS)) {
            if (!StringMaster.compare(hero.getProperty(G_PROPS.DEITY),
             FAITHLESS, false)) {
                return false;
            }
        }
        return !hero.checkContainerProp(G_PROPS.DEITY, e.getName());
    }

    @Override
    protected PagedSelectionPanel<ObjType> createSelectionComponent() {
        PagedSelectionPanel<ObjType> selectionComp = super.createSelectionComponent();
        selectionComp.setCustomRenderer(this);
        return selectionComp;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return DC_TYPE.DEITIES;
    }

    @Override
    public Component getListCellRendererComponent(
     JList<? extends ObjType> list, ObjType value, int index,
     boolean isSelected, boolean cellHasFocus) {
        ListItem<ObjType> item = new ListItem<>(value, isSelected,
         cellHasFocus, getItemSize());
        if (checkBlocked(value)) {
            item.setBorder(BORDER.HIDDEN);
            item.refresh();
        }
        return item;
    }

    @Override
    protected int getItemSize() {
        return ImageManager.LARGE_ICON;
    }

    @Override
    protected PROPERTY getPROP() {
        return G_PROPS.DEITY;
    }

    @Override
    protected String getGroup() {
        return StringMaster.PLAYABLE;
    }

    @Override
    protected VALUE getFilterValue() {
        return G_PROPS.GROUP;
    }

}
