package main.swing.components.panels;

import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.game.MicroGame;
import main.swing.builders.DC_Builder;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.DC_PagedQuickItemPanel;
import main.swing.components.panels.page.DC_PagedSpellPanel;
import main.swing.components.panels.page.small.DC_PagedBuffPanel;
import main.swing.components.panels.page.small.SmallItem;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.graphics.GuiManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Transient;

public class DC_ActiveUnitPanel extends G_Panel implements MouseListener {
    private static final String LBL_HEIGHT = "28";
    DC_PortraitPanel pp;
    DC_ItemPanel ip;
    DC_PagedBuffPanel buffs;
    DC_BarPanel bp;
    ValueOrbPanel vop;

    DC_PagedSpellPanel sbp;
    DC_PagedQuickItemPanel qip;
    // private JLabel lbl;
    NameComponent activeUnitNameComp;
    private DC_Game game;
    private DC_HeroObj obj;
    private CustomButton sbButton;
    private CustomButton qiButton;
    private CustomButton sbButton2;
    private CustomButton qiButton2;
    private DC_HeroObj prevObj;

    // UsableItemPanel
    // SpellBookPanel

    public DC_ActiveUnitPanel(MicroGame game) {
        this.game = (DC_Game) game;
        obj = (DC_HeroObj) game.getPlayer(game.isHost() || game.isOffline()).getHeroObj();
        sbp = new DC_PagedSpellPanel((DC_Game) game);
        qip = new DC_PagedQuickItemPanel((DC_Game) game);

        sbButton = new CustomButton(VISUALS.SPELLBOOK) {
            protected void playClickSound() {
                SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOOK_OPEN);
            }

            public void handleClick() {
            }
        };
        qiButton = new CustomButton(VISUALS.INV) {
            public void handleClick() {

            }

            protected void playClickSound() {
                SoundMaster.playStandardSound(STD_SOUNDS.DIS__COINS);
            }
        };
        sbButton2 = new CustomButton(VISUALS.HAND) {
            public void handleClick() {
            }

            protected void playClickSound() {
                SoundMaster.playStandardSound(STD_SOUNDS.SKILL_LEARNED);
            }
        };
        qiButton2 = new CustomButton(VISUALS.HAMMER) {
            public void handleClick() {
            }

            protected void playClickSound() {
                SoundMaster.playStandardSound(STD_SOUNDS.DIS__KNIFE);
            }
        };

        // set
        // setPanelSize(new Dimension(width, GuiManager.getScreenHeightInt()));

        // TODO visuals for boss units!
        activeUnitNameComp = new NameComponent();
        // activeUnitNameComp.setPanelSize(new
        // Dimension(VISUALS.PORTRAIT_BORDER.getWidth(), 60));
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {//
        return new Dimension(550, GuiManager.getScreenHeightInt());
    }

    @Override
    public Dimension getSize() {
        return getPreferredSize();
    }

    @Override
    @Transient
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public void setPanelSize(Dimension size) {
        super.setPanelSize(new Dimension(size.width, GuiManager.getScreenHeightInt()));
    }

    private void addComponents() {
        add(pp, "id pp , pos " + DC_Builder.OFFSET_X + " 0");
        // add(lbl, "id lbl, pos pp.x2 0");
        // new TextComp(null, text);
        add(activeUnitNameComp, "id aunc , pos " + DC_Builder.OFFSET_X + " pp.y2");
        add(buffs, "id buffs , pos pp.x2 lbl.y2");
        add(ip, "id ip , pos pp.x2 buffs.y2");
        // add(bp.build(), "id bp , pos ip.x2 0");
        add(vop, "id vop , pos ip.x2 0");
        add(sbButton, "id sbButton, pos sbp.x-4 sbp.y-14");
        add(qiButton, "id qiButton, pos qip.x-4 qip.y-14");
        add(sbButton2, "id sbButton2, pos sbp.x2+4-" + sbButton2.getWidth() + " sbp.y-14");
        add(qiButton2, "id qiButton2, pos qip.x2+4-" + qiButton2.getWidth() + " qip.y-14");
        add(qip, "id qip , pos "
                + (DC_Builder.OFFSET_X + (VISUALS.PORTRAIT_BORDER.getWidth() - 128) / 2)
                + " sbp.y2+" + GuiManager.getSmallObjSize() / 4 + "");
        add(sbp, "id sbp , pos "
                + (DC_Builder.OFFSET_X + (VISUALS.PORTRAIT_BORDER.getWidth() - 128) / 2)
                + " aunc.y2+16");

        // TODO add SPELLBOOK / ITEMS icon-buttons!
    }

    @Override
    protected boolean isAutoZOrder() {
        return true;
    }

    public void initComponents() {
        // lbl = new JLabel(obj.getName());
        // lbl.setForeground(Color.white);
        ip = (new DC_ItemPanel(game, false));
        ip.setObj(obj);
        pp = new DC_PortraitPanel(game, false);
        pp.addMouseListener(this);
        buffs = new DC_PagedBuffPanel();
        buffs.addMouseListener(this);
        buffs.setPageMouseListener(this);
        bp = new DC_BarPanel(game.getPlayer(true).getHeroObj(), false);
        vop = new ValueOrbPanel(false);
        vop.setObj(game.getPlayer(true).getHeroObj());

        // new DC_SpellPanel(game.getState());
    }

    @Override
    public void refresh() {
        this.prevObj = this.obj;
        this.obj = game.getManager().getActiveObj();

        if (!initialized) {
            initComponents();
            addComponents();
            initialized = true;
        }

        if (obj != null) {
            if (obj.getOwner().isMe() && !obj.isAiControlled()) {
                // lbl.setText(obj.getName());
                pp.setEntity(obj);
                ip.setObj(obj);
                vop.setObj(obj);
                bp.setObj(obj);
                buffs.setObj(obj);
                sbp.setObj(obj);
                qip.setObj(obj);
                sbp.refresh();
                qip.refresh();
                activeUnitNameComp.setObj(getObj());
            } else {
                // if (prevObj == null || // TODO dead?

                sbp.refresh();
                qip.refresh();

            }
        }
        activeUnitNameComp.refresh();
        vop.refresh();
        bp.refresh();
        buffs.refresh();
        ip.refresh();
        pp.refresh();
    }

    public DC_PortraitPanel getPp() {
        return pp;
    }

    public void setPp(DC_PortraitPanel pp) {
        this.pp = pp;
    }

    public DC_ItemPanel getIp() {
        return ip;
    }

    public void setIp(DC_ItemPanel ip) {
        this.ip = ip;
    }

    public DC_BarPanel getBp() {
        return bp;
    }

    public void setBp(DC_BarPanel bp) {
        this.bp = bp;
    }

    public DC_PagedSpellPanel getSbp() {
        return sbp;
    }

    public DC_PagedQuickItemPanel getQip() {
        return qip;
    }

    public DC_PagedBuffPanel getBuffPanel() {
        return buffs;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // left click -> select?
        if (getBuffPanel().contains(e.getPoint())) {
            SmallItem buff = (SmallItem) ((G_ListPanel) getBuffPanel().getCurrentComponent())
                    .getList().locationToItem(e.getPoint());
            game.getToolTipMaster().initBuffTooltip(buff, false);
        } else if (getPp().contains(e.getPoint())) {
            DC_Game.game.getBattleField().centerCameraOn(obj);
        }
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

    public Obj getObj() {
        return obj;
    }

}
