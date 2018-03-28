package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.HeroAnalyzer;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.HERO_SOUNDSET;
import main.content.values.properties.G_PROPS;
import main.entity.obj.unit.Unit;
import main.swing.components.panels.page.info.element.ListTextItem;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.sound.SoundMaster.SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class SoundsetChoiceView extends ChoiceView<HERO_SOUNDSET> implements
 ListCellRenderer<HERO_SOUNDSET>, MouseListener {
    private static final int X_OFFSET = -200;
    private static final Integer FONT_SIZE = 40;

    public SoundsetChoiceView(ChoiceSequence sequence, Unit hero) {
        super(sequence, hero);
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    public boolean isInfoPanelNeeded() {
        return false;
    }

    @Override
    protected int getPageSize() {
        return 10;
    }

    @Override
    protected VISUALS getBackgroundVisuals() {
        return null;
    }

    @Override
    public String getInfo() {
        return InfoMaster.CHOOSE_SOUNDSET;
    }

    protected void addInfoPanels() {
    }

    @Override
    protected void initData() {
        data = new ArrayList<>();
        for (HERO_SOUNDSET s : HeroEnums.HERO_SOUNDSET.values()) {
            if ((s.isFemale() != HeroAnalyzer.isFemale(hero))) {
                continue;
            }
            data.add(s);
        }
        data.toString();
    }

    @Override
    public void activate() {
        initData();
        init();
        super.activate();
    }

    @Override
    protected int getPagePosX() {
        return super.getPagePosX() + X_OFFSET;
    }

    @Override
    protected PagedSelectionPanel<HERO_SOUNDSET> createSelectionComponent() {
        PagedSelectionPanel<HERO_SOUNDSET> selectionComp = new PagedSelectionPanel<HERO_SOUNDSET>(
         this, getPageSize(), getItemSize(), getColumnsCount()) {

            protected boolean isFillWithNullElements() {
                return false;
            }

            public int getPanelHeight() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * pageSize / wrap;
            }

            public int getPanelWidth() {
                return VISUALS.ENUM_CHOICE_COMP.getHeight() * wrap;
            }
        };
        selectionComp.setCustomRenderer(this);
        selectionComp.setPageMouseListener(this);
        return selectionComp;
    }

    @Override
    public void itemIndexSelected(int i) {
        super.itemIndexSelected(i);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HERO_SOUNDSET> list,
                                                  HERO_SOUNDSET value, int index, boolean isSelected, boolean cellHasFocus) {
        VISUALS V = (isSelected) ? VISUALS.ENUM_CHOICE_COMP_SELECTED : VISUALS.ENUM_CHOICE_COMP;
        return new ListTextItem<>(V, value, isSelected, false, FONT_SIZE);

    }

    @Override
    protected void applyChoice() {

        CharacterCreator.getHeroManager().saveHero(hero);
        hero.setProperty(G_PROPS.SOUNDSET, getSelectedItem().getPropValue(), true);
    }

    @Override
    protected void ok() {
        super.ok();
        DC_SoundMaster.playEffectSound(SOUNDS.READY, hero);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        DC_SoundMaster.playRandomSound(data.get(getSelectedIndex()));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
