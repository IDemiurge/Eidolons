package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.points.HC_CustomInfoPanel;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.obj.unit.DC_HeroObj;
import main.swing.components.panels.page.info.element.ListTextItem;
import main.system.auxiliary.secondary.InfoMaster;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public class PrincipleChoiceView extends ChoiceView<PRINCIPLES> implements
        ListCellRenderer<PRINCIPLES> {
    private static final int X_OFFSET = -200;
    boolean secondPrinciple;
    private HC_CustomInfoPanel customIp;
    private boolean blocked = false;

    public PrincipleChoiceView(ChoiceSequence choiceSequence, DC_HeroObj hero,
                               boolean secondPrinciple) {
        super(choiceSequence, hero);
        this.secondPrinciple = secondPrinciple;
    }

    public PrincipleChoiceView(ChoiceSequence choiceSequence, DC_HeroObj hero) {
        this(choiceSequence, hero, false);
    }

    protected int getPageSize() {
        return PRINCIPLES.values().length;
    }

    @Override
    protected VISUALS getBackgroundVisuals() {
        return null;
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    public String getInfo() {
        return (secondPrinciple) ? InfoMaster.CHOOSE_DEITY_PRINCIPLE : InfoMaster.CHOOSE_PRINCIPLE;
    }

    @Override
    public void itemSelected(PRINCIPLES i) {
        super.itemSelected(i);
        PRINCIPLES principles = data.get(getSelectedIndex());
        blocked = (checkBlocked(principles));
        String text = principles.getDescription();
        customIp.setText(text);
        customIp.refresh();
    }

    @Override
    protected int getPagePosX() {
        return super.getPagePosX() + X_OFFSET;
    }

    public boolean isOkBlocked() {
        if (blocked) {
            return true;
        }
        return super.isOkBlocked();
    }

    protected void addInfoPanels() {
        customIp = new HC_CustomInfoPanel(VISUALS.INFO_PANEL);
        add(customIp, IP_POS);
    }

    @Override
    protected PagedSelectionPanel<PRINCIPLES> createSelectionComponent() {
        PagedSelectionPanel<PRINCIPLES> selectionComp = new PagedSelectionPanel<PRINCIPLES>(this,
                getPageSize(), getItemSize(), getColumnsCount()) {

            public int getPanelHeight() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * pageSize / wrap;
            }

            public int getPanelWidth() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * wrap;
            }
        };
        selectionComp.setCustomRenderer(this);
        return selectionComp;
    }

    @Override
    protected void initData() {
        data = new LinkedList<>(Arrays.asList(PRINCIPLES.values()));
    }

    protected boolean isSaveHero() {
        return true;
    }

    @Override
    protected void applyChoice() {
        CharacterCreator.getHeroManager().saveHero(hero);

        if (secondPrinciple) { // sequence order changed!
            hero.addProperty(G_PROPS.PRINCIPLES, data.get(getSelectedIndex()).toString());
            hero.getType().addProperty(G_PROPS.PRINCIPLES, data.get(getSelectedIndex()).toString());
        } else {
            hero.setProperty(G_PROPS.PRINCIPLES, data.get(getSelectedIndex()).toString(), true);
        }
        modifyIdentity();
    }

    private void modifyIdentity() {
        int percentage = getFirstPrincipleIdentityPercentage();
        int amount = hero.getIntParam(PARAMS.IDENTITY_POINTS) * percentage / 100;
        if (secondPrinciple) // remainder!
        {
            amount = hero.getIntParam(PARAMS.IDENTITY_POINTS);
        }
        hero.modifyParameter(DC_ContentManager.getIdentityParamForPrinciple(getSelectedItem()),
                amount);
        hero.modifyParameter(PARAMS.IDENTITY_POINTS, -amount);
    }

    private int getFirstPrincipleIdentityPercentage() {
        return 66;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends PRINCIPLES> list,
                                                  PRINCIPLES value, int index, boolean isSelected, boolean cellHasFocus) {
        boolean blocked = checkBlocked(value);

        VISUALS V = (isSelected) ? VISUALS.ENUM_CHOICE_COMP_SELECTED : VISUALS.ENUM_CHOICE_COMP;

        return new ListTextItem<>(V, value, isSelected, blocked);

    }

    public boolean checkBlocked(PRINCIPLES value) {
        // prevent from picking twice...
        if (secondPrinciple) {
            return hero.checkProperty(G_PROPS.PRINCIPLES, value.toString());
            // return
            // StringMaster.contains(hero.getProperty(G_PROPS.PRINCIPLES),
            // value.toString());
        }

        return false;
        // if (secondPrinciple)
        // return false; // NEW !!!
        //
        // String prop = (secondPrinciple) ?
        // hero.getDeity().getType().getProperty(G_PROPS.PRINCIPLES)
        // : hero.getProperty(G_PROPS.PRINCIPLES);
        //
        // if (!secondPrinciple) {
        // if (StringMaster.contains(hero.getProperty(G_PROPS.PRINCIPLES),
        // value.toString()))
        // return true;
        // }
        // if (StringMaster.isEmpty(prop))
        // return false;
        // return !StringMaster.contains(prop, value.toString());
    }

}
