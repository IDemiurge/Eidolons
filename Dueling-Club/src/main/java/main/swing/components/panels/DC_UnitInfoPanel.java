package main.swing.components.panels;

import main.content.enums.rules.VisionEnums;
import main.entity.Entity;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.core.game.DC_Game;
import main.swing.components.panels.page.DC_PagedPriorityPanel;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.components.panels.page.log.DC_PagedLogPanel;
import main.swing.components.panels.page.small.DC_PagedBuffPanel;
import main.swing.components.panels.page.small.SmallItem;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DC_UnitInfoPanel extends G_Panel implements MouseListener {
    DC_PortraitPanel pp;
    DC_ItemPanel ip;
    DC_PagedBuffPanel buffPanel;
    DC_BarPanel bp;

    // A different set for Terrain - no need for portrait, bars - just
    // items/corpses and info (?)
    DC_PagedInfoPanel info;

    DC_PagedPriorityPanel plp;

    ValueIconPanel valueIconPanel;

    private DC_Game game;
    private Obj obj;
    private DC_PagedLogPanel log;

    private GameControlPanel minimapButtonPanel;
    private G_Panel holder;
    private ValueOrbPanel valueOrbPanel;

    public DC_UnitInfoPanel(G_Panel holder, DC_Game game) {
        this.holder = holder;
        this.game = game;
        initComponents();
        addComponents();

        panelSize = new Dimension(542, 942);
    }

    private void addComponents() {
        // add(bp.build(), "id bp , pos 0 0 " + GuiManager.getSquareCellSize() +
        // " pp.y2");

        add(pp, "id pp , pos bp.x2 0");
        add(buffPanel, "id buffs , pos pp.x2 0");
        add(ip, "id ip , pos pp.x2 buffs.y2");
        add(plp, "id plp , pos pp.x pp.y2");
        add(info, "id info , pos plp.x2 pp.y2");
        add(valueIconPanel, "id vip , pos plp.x2 info.y2");
        add(log, "id log , pos plp.x2 vip.y2");
        add(minimapButtonPanel, "id mbp , pos plp.x2 log.y2");

        add(valueOrbPanel, "id bp , pos 0 0 " + GuiManager.getSquareCellSize() + " pp.y2");
        // panelSize = new Dimension(VISUALS.INFO_PANEL.getWidth()
        // + GuiManager.getSmallObjSize(), VISUALS.BF_GRID.getHeight()
        // + GuiManager.getCellSize());
    }

    public void initComponents() {
        valueOrbPanel = new ValueOrbPanel(true);
        ip = (new DC_ItemPanel(game, true));
        ip.setBorder(null);
        pp = new DC_PortraitPanel(game, true);
        buffPanel = new DC_PagedBuffPanel();
        buffPanel.setPageMouseListener(this);
        bp = new DC_BarPanel(game.getPlayer(false).getHeroObj(), true);
        info = new DC_PagedInfoPanel(null);
        plp = new DC_PagedPriorityPanel(game);
        valueIconPanel = new ValueIconPanel(game);
        log = new DC_PagedLogPanel(game);
        minimapButtonPanel = new GameControlPanel(game.getBattleField().getBuilder());
    }

    @Override
    public void refresh() {
        // panelSize = new Dimension(VISUALS.INFO_PANEL.getWidth()
        // + GuiManager.getSmallObjSize(), VISUALS.BF_GRID.getHeight()
        // + GuiManager.getCellSize());
        plp.refresh();
        this.obj = game.getManager().getInfoObj();
        if (obj == null) {
            if (game.getManager().getInfoEntity() != null) {
                entitySelected(game.getManager().getInfoEntity());
            }
            return;
        } else {
            objSelected(obj);
        }

        valueOrbPanel.refresh();
        bp.refresh();
        buffPanel.refresh();
        ip.refresh();
        valueIconPanel.refresh();
        pp.refresh();
        info.refresh();

        log.refresh();

    }

    private void entitySelected(Entity infoEntity) {
        bp.setObj(null);
        pp.setEntity(obj);
        info.setEntity(infoEntity);
        buffPanel.setObj(null);
        ip.setObj(null);
        valueIconPanel.setEntity(infoEntity);
    }

    private void objSelected(Obj obj) {
        valueOrbPanel.setObj(obj);
        bp.setObj(obj);
        pp.setEntity(obj);
        info.setEntity(obj);
        buffPanel.setObj(obj);
        ip.setObj(obj);
        valueIconPanel.setEntity(obj);

        if (obj instanceof DC_Obj) {
            DC_Obj dcObj = (DC_Obj) obj;
            if (!dcObj.isMine()) {
                if (dcObj.getVisibilityLevel() != VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT) {
                    // if (dcObj.getVisibilityLevel() ==
                    // VISIBILITY_LEVEL.OUTLINE) {
                    bp.setObj(null);
                    valueOrbPanel.setObj(null);
                    if (dcObj.getOutlineType() != null) {
                        // VisibilityMaster.getOutlineType(unit, activeUnit)

                        if (dcObj.getOutlineType().getImage128() == null) {
                            pp.getPortrait().setIcon(
                                    new ImageIcon(dcObj.getOutlineType().getImage()));
                        } else {
                            pp.getPortrait().setIcon(
                                    new ImageIcon(dcObj.getOutlineType().getImage128()));
                        }

                    }
                    pp.setEntity(null);
                    info.setEntity(dcObj);
                    buffPanel.setObj(null);
                    ip.setObj(null);
                    valueIconPanel.setEntity(null);
                    // }
                }
            }
            // if (!VisionManager.checkKnown((DC_Obj) obj)) {
            // // && !game.isDebugMode()
            // bp.setObj(null);
            // pp.setEntity(null);
            // info.setEntity(null);
            // buffs.setObj(null);
            // ip.setObj(null);
            // vip.setEntity(null);
            // } else if ((obj instanceof DC_HeroObj) &&
            // !VisionManager.checkDetected((DC_Obj) obj)) {
            // bp.setObj(obj.getType());
            // pp.setEntity(obj.getType());
            // info.setEntity(obj.getType());
            // buffs.setObj(null);
            // ip.setObj(null);
            // vip.setEntity(obj.getType());
            //
            // } else

            // if (obj instanceof DC_Cell) {
            // holder.remove(this);
            // }
        }
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

    public DC_PagedInfoPanel getInfo() {
        return info;
    }

    public DC_PagedPriorityPanel getPriorityListPanel() {
        return plp;
    }

    public DC_PagedBuffPanel getBuffPanel() {
        return buffPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // left click -> select?
        if (getBuffPanel().contains(e.getPoint())) {
            SmallItem buff = (SmallItem) ((G_ListPanel) getBuffPanel().getCurrentComponent())
                    .getList().locationToItem(e.getPoint());
            game.getToolTipMaster().initBuffTooltip(buff, false);
        } else if (getPp().contains(e.getPoint())) {
            // DC_Game.game.getBattleField().centerCameraOn(obj);
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

    public DC_PagedPriorityPanel getPlp() {
        return plp;
    }

    public ValueIconPanel getValueIconPanel() {
        return valueIconPanel;
    }

    public DC_Game getGame() {
        return game;
    }

    public Obj getObj() {
        return obj;
    }

    public DC_PagedLogPanel getLog() {
        return log;
    }

    public GameControlPanel getMinimapButtonPanel() {
        return minimapButtonPanel;
    }

    public G_Panel getHolder() {
        return holder;
    }

    public ValueOrbPanel getValueOrbPanel() {
        return valueOrbPanel;
    }

}
