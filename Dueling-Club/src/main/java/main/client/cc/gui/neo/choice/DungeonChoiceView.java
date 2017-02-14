package main.client.cc.gui.neo.choice;

import main.client.cc.logic.party.PartyObj;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.content.PROPS;
import main.content.VALUE;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.swing.generic.components.CompVisuals;
import main.system.graphics.GuiManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.InfoMaster;

/*
 * ideally, should display location on the map as well!
 */

public class DungeonChoiceView extends EntityChoiceView {
    private static final int MAX_SIZE = 6;
    private PartyObj party;

    public DungeonChoiceView(ChoiceSequence choiceSequence, PartyObj party) {
        super(choiceSequence, party.getLeader());
        this.party = party;
    }

    protected int getPageSize() {
        return Math.min(MAX_SIZE, data.size());
    }

    protected int getColumnsCount() {
        return Math.min(MAX_SIZE, data.size());
    }

    @Override
    protected void initData() {
        data = DataManager.toTypeList(party.getProperty(G_PROPS.DUNGEONS_PENDING),
                OBJ_TYPES.DUNGEONS);
        // data = DataManager.getTypesGroup(OBJ_TYPES.DUNGEONS,
        // party.getValue(getFilterValue()));

    }

    @Override
    protected VISUALS getBackgroundVisuals() {
        return null;
    }

    @Override
    protected void ok() {
        super.ok();
    }

    @Override
    public void itemSelected(ObjType i) {
        super.itemSelected(i);
        setVisuals(new CompVisuals(GuiManager.DEF_DIMENSION, getSelectedItem().getProperty(
                PROPS.MAP_BACKGROUND)));
        init();
        refresh();
        // background
    }

    @Override
    protected void addControls() {
        // TODO Auto-generated method stub

        super.addControls();
    }

    protected boolean isReady() {
        return false;
    }

    protected void addInfoPanels() {
        return;
    }

    ;

    @Override
    protected void applyChoice() {
        hero.getGame().getDungeonMaster().initDungeon(getSelectedItem());
        party.setProperty(getPROP(), getSelectedItem().getName());
    }

    @Override
    public boolean checkBlocked(ObjType e) {
        if (party.checkProperty(G_PROPS.DUNGEONS_COMPLETED, e.getName())) {
            return true;
        }
        return super.checkBlocked(e);
    }

    @Override
    protected PROPERTY getPROP() {
        return G_PROPS.LAST_DUNGEON;
    }

    @Override
    protected VALUE getFilterValue() {
        return G_PROPS.ARCADE_REGION;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return OBJ_TYPES.DUNGEONS;
    }

    @Override
    public String getInfo() {
        if (ListMaster.isNotEmpty(data)) {
            if (getSelectedIndex() >= 0) {
                return getSelectedItem().getName();
            }
        }
        return InfoMaster.CHOOSE_DUNGEON;
    }

}
