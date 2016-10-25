package main.swing.components.panels;

import main.game.logic.dungeon.Dungeon;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DungeonComponent extends G_Panel implements MouseListener {

    private Dungeon dungeon;

    public DungeonComponent(Dungeon dungeon) {
        super(
                dungeon.getGame().getDungeonMaster().getRootDungeon() == dungeon ? VISUALS.PORTRAIT_BORDER
                        : null);
        this.dungeon = dungeon;
        add(new GraphicComponent(dungeon.getIcon().getImage()));

        addMouseListener(this);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            dungeon.getGame().getDungeonMaster().goToDungeon(dungeon);
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

}
