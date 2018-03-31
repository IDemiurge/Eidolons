package eidolons.client.cc.gui.neo.choice;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.swing.generic.components.list.G_List;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.GuiManager;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;

public class PositionChoiceView extends ChoiceView<Unit> implements MouseListener {

    private static final int DEFAULT_SIZE = 9;
    private static final int DEFAULT_COLUMNS_COUNT = 3;
    G_List<Unit> list;
    FACING_DIRECTION side;
    private Map<Unit, Coordinates> partyCoordinates;
    private int size;
    private int columnsCount;
    private int rowCount;

    public PositionChoiceView(ChoiceSequence sequence, Unit hero) {
        super(sequence, hero);
    }

    @Override
    protected VISUALS getBackgroundVisuals() {
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        Unit obj = list.locationToItem(e.getPoint()); // list.getSelectedValue();
        Coordinates c = partyCoordinates.get(obj);

        int x = c.x;
        int y = c.y;
        boolean valid;
        if (SwingUtilities.isRightMouseButton(e)) {
            if (e.isAltDown()) {
                x--;
            } else {
                x++;
            }
            valid = checkValidX(x);
            if (!valid) {
                x -= 2; // 'flip over'
                valid = checkValidX(x);
                if (!valid) {
                    x += 4;
                    valid = checkValidX(x);
                }
            }
        } else {
            if (e.isAltDown()) {
                y--;
            } else {
                y++;
            }
            valid = checkValidY(y);
            if (!valid) {
                y -= 2;
                valid = checkValidY(y);
                if (!valid) {
                    y += 4;
                    valid = checkValidY(y);
                }
            }
        }
        Coordinates newCoordinates = new Coordinates(x, y);
        if (partyCoordinates.get(newCoordinates) != null) {
            Unit swappedHero = new MapMaster<Unit, Coordinates>().getKeyForValue(
             partyCoordinates, newCoordinates);
            partyCoordinates.put(swappedHero, c);
        }
        if (!valid) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
            return;
        }
        if (e.isAltDown()) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ACTIVATE);
        } else {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK);
        }

        int i = transformCoordinateIntoIndex(newCoordinates);
        ((DefaultListModel<Unit>) list.getModel()).set(i, obj);
        // transformIndexIntoCoordinate(list.getSelectedIndex());
        partyCoordinates.remove(obj);
        partyCoordinates.put(obj, newCoordinates);
        initData();
        init();
        setIndex(i);
        // swap(x, y);

    }

    // @Override
    // public int getSelectedIndex() {
    // return 0;
    // }

    @Override
    protected void addInfoPanels() {

    }

    private boolean checkValidY(int y) {
        if (y < getBaseY()) {
            return false;
        }
        return y <= getBaseY() + rowCount;
    }

    private boolean checkValidX(int x) {
        if (x < getBaseX()) {
            return false;
        }
        return x <= getBaseX() + columnsCount;
    }

    public int getPageSize() {
        return size;
    }

    @Override
    protected void init() {
        columnsCount = DEFAULT_COLUMNS_COUNT;
        size = DEFAULT_SIZE;
        rowCount = size / columnsCount;
        if (partyCoordinates == null) {
            partyCoordinates = PartyHelper.getParty().getPartyCoordinates();
            if (partyCoordinates == null) {
                partyCoordinates = DC_Game.game.getDungeonMaster().getPositioner().getPartyCoordinates(PartyHelper.getParty()
                 .getMembers());
                PartyHelper.getParty().setPartyCoordinates(partyCoordinates);

            }
            for (Unit hero : partyCoordinates.keySet()) {
                hero.setCoordinates(partyCoordinates.get(hero));
            }
        }
        super.init();
        list = ((G_ListPanel) pages.getCurrentComponent()).getList();
        list.addMouseListener(this);
    }

    @Override
    public boolean isOkBlocked() {
        return false;
    }

    @Override
    protected void initData() {
        // Positioner()
        // default positions?
        data = new ArrayList<>();
        ListMaster.fillWithNullElements(data, size);
        if (partyCoordinates != null) {
            for (Unit hero : partyCoordinates.keySet()) {
                if (hero == null) {
                    continue;
                }
                Coordinates c = partyCoordinates.get(hero);
                hero.setCoordinates(c);
                int i = transformCoordinateIntoIndex(c);
                data.set(i, hero);
            }
        }
    }

    private Coordinates transformIndexIntoCoordinate(int index) {
        int x = getBaseX();
        int y = getBaseY();

        y += index / columnsCount;
        x += index % columnsCount;

        return new Coordinates(x, y);
    }

    private int getBaseY() {
        return PositionMaster.getMiddleIndex(true) - (GuiManager.getBF_CompDisplayedCellsY() - 5);
    }

    private int getBaseX() {
        return PositionMaster.getMiddleIndex(false) - 2;
    }

    private int transformCoordinateIntoIndex(Coordinates c) {
        int i = 0;
        if (side == null) {
            // BASE NOT MAX!
            // i+=c.y

            i += columnsCount * (1 + c.x - PositionMaster.getMiddleIndex(true));
            i += 1 + c.y - PositionMaster.getMiddleIndex(false);
        }
        return i;
    }

    @Override
    protected int getColumnsCount() {
        return columnsCount;
    }

    @Override
    protected void applyChoice() {
        if (PartyHelper.getParty() != null) {
            PartyHelper.getParty().setPartyCoordinates(partyCoordinates);
        }
        for (Unit hero : data) {
            if (hero != null) {
                hero.setCoordinates(partyCoordinates.get(hero));
                hero.setFacing(getFacing(hero.getCoordinates()));
            }
        }
    }

    private FACING_DIRECTION getFacing(Coordinates coordinates) {
        if (coordinates.x == getBaseX()) {
            return FACING_DIRECTION.WEST;
        }
        if (coordinates.x == getBaseX() + rowCount) {
            return FACING_DIRECTION.EAST;
        }
        // if (coordinates.y== getBaseY()+columnsCount)
        // return FACING_DIRECTION.SOUTH;
        return FACING_DIRECTION.NORTH;
    }

    @Override
    public String getInfo() {

        return "Positioning: use normal, alt and right click";
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public Map<Unit, Coordinates> getPartyCoordinates() {
        return partyCoordinates;
    }

    public void setPartyCoordinates(Map<Unit, Coordinates> partyCoordinates) {
        this.partyCoordinates = partyCoordinates;
        for (Unit hero : partyCoordinates.keySet()) {
            hero.setCoordinates(partyCoordinates.get(hero));
        }
    }

}