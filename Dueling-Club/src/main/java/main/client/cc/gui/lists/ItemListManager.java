package main.client.cc.gui.lists;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.pages.HC_PagedListPanel;
import main.client.cc.gui.views.HeroItemView;
import main.client.cc.logic.HeroCreator;
import main.content.C_OBJ_TYPE;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class ItemListManager implements MouseListener, ListSelectionListener {

    protected Map<HC_PagedListPanel, List<HeroListPanel>> listMap = new HashMap<>();
    protected List<HC_PagedListPanel> removeLists = new LinkedList<>();
    protected HC_PagedListPanel heroList;
    protected OBJ_TYPE TYPE;
    protected PROPERTY PROP;
    protected HeroItemView view;
    protected HC_PagedListPanel quickItemsList;
    protected PROPERTY prop2;
    private DC_HeroObj hero;
    private DC_PagedInfoPanel itemInfoPanel;
    private DC_PagedInfoPanel heroInfoPanel;

    private boolean infoPanelSwitch = false;

    public ItemListManager(DC_HeroObj hero, OBJ_TYPE TYPE, PROPERTY PROP) {

        this.setHero(hero);
        this.TYPE = TYPE;
        this.PROP = PROP;
    }

    public void remove(HeroListPanel list) {
        listMap.remove(list);
    }

    public void add(HC_PagedListPanel list) {
        List<HeroListPanel> lists = list.getLists();
        this.listMap.put(list, lists);
        for (HeroListPanel page : lists) {
            page.getList().addListSelectionListener(this);
            if (!Arrays.asList(page.getList().getMouseListeners()).contains(this))
                page.getList().addMouseListener(this);
        }

    }

    public boolean addType(ObjType type) {
        return addType(type, null, false);
    }

    protected boolean addType(ObjType type, HeroListPanel hlp, boolean alt) {
        if (isQuickItemList(hlp, type.getOBJ_TYPE_ENUM() == OBJ_TYPES.ITEMS ? PROPS.QUICK_ITEMS
                : null))
            return false;
        if (hlp != null) {
            if (isHeroList(hlp)) {
                if (TYPE == OBJ_TYPES.JEWELRY)
                    return false;
                if (TYPE == OBJ_TYPES.SKILLS)
                    return false;
                if (C_OBJ_TYPE.ITEMS.equals(TYPE))

                    return CharacterCreator.getHeroManager().addSlotItem(getHero(), type, alt) > 0;

                if (TYPE == OBJ_TYPES.SPELLS) {
                    if (!alt) {
                        return CharacterCreator.getHeroManager().addItem(getHero(), type, TYPE,
                                PROPS.VERBATIM_SPELLS);

                    } else
                        return CharacterCreator.getHeroManager().addMemorizedSpell(getHero(), type);

                }
            }

            if (hlp.getParent() instanceof HC_PagedListPanel)
                if (isRemovable((HC_PagedListPanel) hlp.getParent()))
                    return false;

        }
        // if (!lists.getOrCreate(list).isResponsive())
        // return;

        if (TYPE == OBJ_TYPES.CLASSES || TYPE == OBJ_TYPES.SKILLS) {
            if (hero.getFeat(type) != null)
                return CharacterCreator.getHeroManager().tryIncrementRank(hero, type);
        }

        return CharacterCreator.getHeroManager().addItem(getHero(), type, TYPE, PROP);
    }

    protected boolean isHeroList(HeroListPanel hlp) {
        if (getHeroList() == null)
            return false;
        return getHeroList().getLists().contains(hlp);
    }

    public void typeSelected(Entity selected, HeroListPanel hlp) {
        // boolean isHeroList = isHeroList(hlp);
        // if (hlp == null) {
        // isHeroList = infoPanelSwitch;
        // infoPanelSwitch = !infoPanelSwitch;
        // }

        if (selected instanceof ObjType) {
            // if (hlp.isResponsive())
            // if (isHeroList(hlp))
            if (HeroCreator.getObjForType(hero, (ObjType) selected) != null)
                // if (checkObjConversion(type.getOBJ_TYPE_ENUM()))
                selected = HeroCreator.getObjForType(hero, (ObjType) selected);
        }

        if (infoPanelSwitch) {
            if (heroInfoPanel.getEntity() != null)
                if (heroInfoPanel.getEntity().equals(selected))
                    return;
            Entity entity = heroInfoPanel.getEntity();
            heroInfoPanel.setEntity(selected);
            heroInfoPanel.refresh();
            itemInfoPanel.setEntity(entity);
            itemInfoPanel.refresh();
        } else {
            if (itemInfoPanel.getEntity() != null)
                if (itemInfoPanel.getEntity().equals(selected))
                    return;
            Entity entity = itemInfoPanel.getEntity();
            itemInfoPanel.setEntity(selected);
            itemInfoPanel.refresh();
            heroInfoPanel.setEntity(entity);
            heroInfoPanel.refresh();
        }
        CharacterCreator.getHeroPanel().getMiddlePanel().getArc().refresh();
        try {
            updateToolTip(selected, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (List<HeroListPanel> pages : listMap.values()) {
            for (HeroListPanel page : pages) {
                if (page != hlp) {
                    page.getList().clearSelection();
                }
            }

        }
        // final Entity type = selected;
        // new Thread(new Runnable() {
        // public void run() {
        //
        // CharacterCreator.getHeroPanel().typeSelected(type);
        //
        // }
        // }).start();
    }

    private void updateToolTip(Entity type, boolean prompted) {
        if (getHero().getGame().isSimulation())
            if (CharacterCreator.getHeroPanel().getMiddlePanel().isTooltipPanelDisplayed()) {

                CharacterCreator.getHeroPanel().getMiddlePanel().getToolTipPanel().setHero(hero);
                CharacterCreator.getHeroPanel().getMiddlePanel().getToolTipPanel().setItem(type);
                CharacterCreator.getHeroPanel().getMiddlePanel().getToolTipPanel().setPrompted(
                        prompted);
                CharacterCreator.getHeroPanel().getMiddlePanel().getToolTipPanel().refresh();
            }
    }

    public void removeType(Entity entity) {
        removeType(entity, heroList.getCurrentList(), PROP);

    }

    protected void removeType(Entity type, HeroListPanel hlp, PROPERTY p) {
        boolean free = !heroList.getLists().contains(hlp);

        if (isQuickItemList(hlp, p))
            CharacterCreator.getHeroManager().removeQuickSlotItem(getHero(), type);

        else if (free && type.getOBJ_TYPE_ENUM() == OBJ_TYPES.JEWELRY)
            CharacterCreator.getHeroManager().removeJewelryItem(getHero(), type);
        else {
            CharacterCreator.getHeroManager().removeItem(getHero(), type, p, TYPE, free);
        }

    }

    private boolean isQuickItemList(HeroListPanel hlp, PROPERTY p) {
        if (quickItemsList != null)
            return quickItemsList.getLists().contains(hlp);
        return p == PROPS.QUICK_ITEMS;
    }

    protected boolean isRemovable(HC_PagedListPanel list) {
        return removeLists.contains(list); // TODO!!!
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        boolean alt = e.isAltDown();
        boolean right = SwingUtilities.isRightMouseButton(e);
        JList<ObjType> list = (JList<ObjType>) e.getSource();
        HeroListPanel hlp = (HeroListPanel) list.getParent();
        ObjType type = (ObjType) list.getSelectedValue();
        if (right && e.getClickCount() == 1)
            if (isRemovable((HC_PagedListPanel) hlp.getParent())
                    || (!getHero().getGame().isSimulation() && PROP == PROPS.INVENTORY)) {
                removeType(type, hlp, isAltProp(hlp) ? prop2 : PROP);
                SoundMaster.playStandardSound(getRemoveSound(type.getOBJ_TYPE_ENUM()));
                // coins clink! failed to parse xml
                return;
            }
        // double right click won't work for ITEMS!
        if (right && e.getClickCount() > 1)
            alt = true;

        if (alt || e.getClickCount() > 1) {
            if (addType(type, hlp, alt)) {
                SoundMaster.playStandardSound(getAddSound(type.getOBJ_TYPE_ENUM()));
            } else {
                failedAddType(type);
            }

        } else {
            typeSelected(type, hlp);
            SoundMaster.playStandardSound(STD_SOUNDS.MODE);
        }
    }

    private void failedAddType(ObjType type) {
        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);

        updateToolTip(type, true);
    }

    private STD_SOUNDS getRemoveSound(OBJ_TYPE TYPE) {
        if (C_OBJ_TYPE.ITEMS.equals(TYPE))
            return STD_SOUNDS.DIS__COINS;
        if (OBJ_TYPES.SPELLS.equals(TYPE))
            return STD_SOUNDS.DIS__BOOK_CLOSE;

        return STD_SOUNDS.ACTION_CANCELLED;

    }

    private STD_SOUNDS getAddSound(OBJ_TYPE TYPE) {
        if (C_OBJ_TYPE.ITEMS.equals(TYPE))
            return STD_SOUNDS.DIS__COINS;
        if (OBJ_TYPES.SPELLS.equals(TYPE))
            return STD_SOUNDS.DIS__BOOK_OPEN;
        if (OBJ_TYPES.SKILLS.equals(TYPE)) {// TODO
            return STD_SOUNDS.FIGHT;
        }
        if (OBJ_TYPES.CLASSES.equals(TYPE))
            return STD_SOUNDS.DIS__KNIFE;
        return STD_SOUNDS.CLICK_ACTIVATE;
    }

    private boolean isAltProp(HeroListPanel hlp) {
        if (TYPE == C_OBJ_TYPE.ITEMS) {
            return (quickItemsList == hlp.getParent());
        }
        return removeLists.contains(hlp.getParent());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList<ObjType> list = (JList<ObjType>) e.getSource();
        ObjType type = (ObjType) list.getSelectedValue();
        if (type != null)
            typeSelected(type, (HeroListPanel) list.getParent());
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

    public HC_PagedListPanel getHeroList() {
        return heroList;
    }

    public void setHeroList(HC_PagedListPanel list) {
        this.heroList = list;
    }

    public HeroItemView getView() {
        return view;
    }

    public void setView(HeroItemView view) {
        this.view = view;
    }

    public HC_PagedListPanel getQuickItemsList() {
        return quickItemsList;
    }

    public void setQuickItemsList(HC_PagedListPanel list) {
        this.quickItemsList = list;
    }

    public void setPROP2(PROPERTY prop2) {
        this.prop2 = prop2;
    }

    public void addRemoveList(HC_PagedListPanel list) {
        if (!removeLists.contains(list))
            removeLists.add(list);

    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public void setItemInfoPanel(DC_PagedInfoPanel upperPanel) {
        this.itemInfoPanel = upperPanel;

    }

    public void setHeroInfoPanel(DC_PagedInfoPanel lowerPanel) {
        this.heroInfoPanel = lowerPanel;

    }

}
