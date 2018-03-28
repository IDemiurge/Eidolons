package main.client.dc;

import main.client.dc.MainManager.MAIN_MENU_ITEMS;
import main.swing.generic.components.G_Panel;
import main.system.graphics.GuiManager;
import main.system.graphics.MigMaster;

import java.awt.*;

/**
 * Re-usable inside HC/DC?
 *
 * @author JustMe
 */
public class MainMenu extends G_Panel {
    private static final VISUALS FRAME = VISUALS.FRAME_MENU;
    private static final int Y_GAP = 10;
    private static final int Y_OFFSET = -100;
    G_Panel itemPanel;
    private int y;
    private double X;
    private int Y;
    private MAIN_MENU_ITEMS[] items;
    private MainManager manager;

    public MainMenu() {
        init();
    }

    public void setItems(MAIN_MENU_ITEMS[] items) {
        this.items = items;
        refresh();

    }

    private void init() {
        panelSize = (GuiManager.DEF_DIMENSION);
        setBackground(Color.black);
        setOpaque(true);
        itemPanel = new G_Panel(FRAME);
        X = (GuiManager.DEF_DIMENSION.getWidth() - VISUALS.MENU_ITEM.getWidth()) / 2;
        Y = (int) ((GuiManager.DEF_DIMENSION.getHeight() - FRAME.getHeight()) / 2);

        itemPanel.setPanelSize(FRAME.getSize());

        add(itemPanel, "pos " + X + " " + Y);
    }

    @Override
    public void refresh() {
        y = calculateY(items.length);
        itemPanel.removeAll();
        for (MAIN_MENU_ITEMS item : items) {
            addItem(item);
        }
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private int calculateY(int length) {

        int height = (VISUALS.MENU_ITEM.getHeight() + Y_GAP) * length;
        int centeredHeight = MigMaster.getCenteredPosition((int) GuiManager.DEF_DIMENSION
         .getHeight(), height);
        return centeredHeight + Y_OFFSET;
    }

    private void addItem(MAIN_MENU_ITEMS item) {
        String pos = "pos "
         + MigMaster.getCenteredPosition(FRAME.getWidth(), VISUALS.MENU_ITEM.getWidth())
         + " " + y;
        itemPanel.add(getItemComp(item), pos);
        y += VISUALS.MENU_ITEM.getHeight() + Y_GAP;

    }

    private Component getItemComp(MAIN_MENU_ITEMS item) {
        return new MenuItem(item) {
            @Override
            public void handleAltClick() {
                manager.itemClicked(getItem(), true);
            }

            @Override
            public void handleClick() {
                itemClicked(getItem());
            }
        };
    }

    public void itemClicked(MAIN_MENU_ITEMS item) {
        manager.itemClicked(item);
    }

    public void setManager(MainManager mmm) {
        this.manager = mmm;
    }

}
