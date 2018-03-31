package eidolons.swing.components.panels;

import eidolons.game.core.game.DC_Game;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import eidolons.entity.obj.attach.DC_BuffObj;
import main.game.core.state.MicroGameState;
import main.game.logic.battle.player.Player;
import main.swing.generic.components.panels.G_ListPanel;
import main.system.datatypes.DequeImpl;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;

public class DC_BuffPanel extends G_ListPanel<DC_BuffObj> {

    private static final int DEFAULT_PAGE_SIZE = 6;
    private static final int OBJ_SIZE = 32;
    public final int ALLY_HERO_PANEL = 0;
    public final int ENEMY_HERO_PANEL = 1;
    public final int INFO_PANEL = 2;
    private Player player;
    private Boolean info;

    public DC_BuffPanel(int size) {
        this();
        minItems = size;
    }

    public DC_BuffPanel() {
        this(DC_Game.game.getState(), null);
    }

    public DC_BuffPanel(MicroGameState state, Boolean info) {
        super(state, OBJ_SIZE);
        this.info = info;
    }

    @Override
    public Collection<DC_BuffObj> getData() {
        if (obj == null) {
            return getEmptyData();
        }

        DequeImpl<BuffObj> attachments = obj.getBuffs();

        if (attachments == null) {
            return getEmptyData();
        }
        if (attachments.isEmpty()) {
            return getEmptyData();
        }

        data = new ArrayList<>();
        for (Attachment attachment : attachments) {
            if (attachment instanceof DC_BuffObj) {
                data.add((DC_BuffObj) attachment);
            }
        }

        return data;
    }

    @Override
    protected void resetData() {
        if (info != null) {
            if (info) {
                this.obj = state.getGame().getManager().getInfoObj();
            } else {
                this.obj = state.getGame().getManager().getActiveObj();

            }
        }
        super.resetData();
    }

    @Override
    public void setInts() {

        sizeInfo = "w " + 1.5f * GuiManager.getSquareCellSize() + ", h "
         + GuiManager.getSmallObjSize() / 2 + "+" + GuiManager.SCROLL_BAR_WIDTH * 5 / 3;
        vpolicy = JScrollPane.VERTICAL_SCROLLBAR_NEVER;
        hpolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        if (minItems == 0) {
            minItems = DEFAULT_PAGE_SIZE;
        }
        layoutOrientation = JList.HORIZONTAL_WRAP;
    }

}
