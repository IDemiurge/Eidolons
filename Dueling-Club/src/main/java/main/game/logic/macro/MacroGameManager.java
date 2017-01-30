package main.game.logic.macro;

import main.ability.effects.Effect;
import main.content.parameters.MACRO_PARAMS;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.GameManager;
import main.game.logic.macro.entity.MacroActionManager;
import main.game.logic.macro.entity.MacroObj;
import main.game.logic.macro.global.TimeMaster;
import main.game.logic.macro.gui.map.MapComp;
import main.game.logic.macro.rules.TurnRule;
import main.game.logic.macro.travel.AreaManager;
import main.game.logic.macro.travel.MacroParty;
import main.game.logic.macro.travel.TravelMaster;
import main.game.logic.macro.utils.SaveMaster;
import main.game.player.Player;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MacroGameManager extends GameManager {

    Set<Obj> selectionSet;

    public MacroGameManager(MacroGame macroGame) {
        this.game = macroGame;
    }

    public void newTurn() {
        // factionsTurn();

        SaveMaster.saveInNewThread();

        getGame().getCampaign().modifyParameter(MACRO_PARAMS.HOURS_ELAPSED,
                TimeMaster.getHoursPerTurn());
        for (MacroParty p : getGame().getParties()) {
            p.newTurn();
        }
        for (MacroObj p : getGame().getPlaces()) {
            p.newTurn();
        }
        for (MacroObj p : getGame().getFactions()) {
            p.newTurn();
        }
        for (MacroObj p : getGame().getRoutes()) {
            p.newTurn();
        }
        for (TurnRule r : getGame().getTurnRules()) {
            r.newTurn();
        }
        TravelMaster.newTurn();
        AreaManager.newTurn();
        refreshAll();
        getMapComp().refresh();
    }

    @Override
    public MacroGame getGame() {
        return (MacroGame) super.getGame();
    }

    public void dataChanged() {
        if (MacroManager.isEditMode())
            MacroManager.getEditorView().refresh();
        else
            MacroManager.refreshGui();
    }

    @Override
    public void infoSelect(Obj obj) {
        if (MacroManager.isEditMode())
            MacroManager.getEditorView().setInfoObj(obj);
        else
            MacroManager.getMapView().getMacroInfoPanel().setInfoObj(obj);
        if (getInfoObj() != null)
            getInfoObj().setInfoSelected(false);
        super.infoSelect(obj);
        if (obj != null)
            obj.setInfoSelected(true);

        getMapComp().refresh();
    }

    @Override
    public void refreshAll() {
        if (MacroManager.isEditMode())
            MacroManager.getEditorView().refresh();
        else
            MacroManager.getMapView().refresh();

    }

    public void objClicked(Obj obj) {
        if (isSelecting()) {
            Integer id = null;
            if (getSelectionSet().contains(obj)) {
                id = obj.getId();
                SoundMaster.playStandardSound(STD_SOUNDS.CLICK_TARGET_SELECTED);
            } else {
                SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
            }
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_MAP_OBJ, id);
            setSelecting(false);
        } else
            infoSelect(obj);
    }

    public void cancelSelection() {
        WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_MAP_OBJ, null);
        setSelecting(false);
        MacroActionManager.setActionsBlocked(false);
    }

    public Integer select(Collection<? extends Obj> objects) {
        setSelecting(true);
        selectionSet = new HashSet<Obj>();
        for (Obj o : objects) {
            selectionSet.add(o);
        }
        highlight(selectionSet);
        Integer id = (Integer) WaitMaster
                .waitForInput(WAIT_OPERATIONS.SELECT_MAP_OBJ);
        setSelecting(false);
        highlightsOff();
        return id;
    }

    @Override
    public Integer select(Filter<Obj> filter, Ref ref) {
        return select(filter.getObjects(ref));
    }

    @Override
    public void highlight(Set<Obj> set) {
        getMapComp().highlight(set);

    }

    @Override
    public void highlightsOff() {
        getMapComp().highlightsOff();

    }

    public MapComp getMapComp() {
        if (MacroManager.isEditMode())
            return MacroManager.getEditorView().getMapComp();
        return MacroManager.getMapView().getMapComp();
    }

    public Set<Obj> getSelectionSet() {
        return selectionSet;
    }

    public void setSelectionSet(Set<Obj> selectionSet) {
        this.selectionSet = selectionSet;
    }

    @Override
    public void endTurn() {
        // TODO Auto-generated method stub

    }

    public void win(Player winningPlayer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetValues(Player owner) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public MicroObj createSpell(ObjType type, MicroObj owner, Ref ref) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MicroObj createUnit(ObjType type, int x, int y, Player owner, Ref ref) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public MicroObj createSpell(ObjType type, Player player, Ref ref) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BuffObj createBuff(BuffType type, Obj active, Player player,
                              Ref ref, Effect effect, int duration, Condition retainCondition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer select(Set<Obj> selectingSet) {
        return null;
    }

}
