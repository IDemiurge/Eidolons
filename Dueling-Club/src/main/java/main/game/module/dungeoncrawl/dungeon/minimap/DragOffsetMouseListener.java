package main.game.module.dungeoncrawl.dungeon.minimap;

import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class DragOffsetMouseListener implements MouseListener, AWTEventListener,
        MouseMotionListener {

    private static final int factor = 100;
    private Point pressPoint;
    private Point releasePoint;
    private MiniGrid grid;
    private int max_offset;

    // TODO support drag-n-drop move() for Editor
    public DragOffsetMouseListener(MiniGrid grid) {
        this.grid = grid;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // mapping
        e.getPoint();

        // SwingMaster.findComponentUnderGlassPaneAt(portrait, top);
        // grid.getComp().grid.getCellWidth();
        // overlaying?
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressPoint = e.getPoint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        releasePoint = e.getPoint();
        offset();
    }

    private void offset() {
        SoundMaster.playStandardSound(STD_SOUNDS.MOVE);
        int diff_x = pressPoint.x - releasePoint.x;
        int diff_y = pressPoint.y - releasePoint.y;
        int yOffset = diff_y / factor;
        int xOffset = diff_x / factor;
        yOffset = Math.min(max_offset, yOffset);
        xOffset = Math.min(max_offset, xOffset);
        grid.offset(false, yOffset);
        grid.offset(true, xOffset);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void eventDispatched(AWTEvent event) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        releasePoint = e.getPoint();
        LogMaster.log(1, "DRAGGED " + releasePoint);
        // e.getOrCreate
        // e.getComponent();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        LogMaster.log(1, "mouseMoved " + e.getPoint());

    }

}
