package main.game.module.adventure;

import com.badlogic.gdx.math.Vector2;
import main.ability.effects.Effect;
import main.content.values.parameters.MACRO_PARAMS;
import main.elements.Filter;
import main.elements.conditions.Condition;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.BuffType;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.GameManager;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.module.adventure.entity.MacroActionManager;
import main.game.module.adventure.entity.MacroObj;
import main.game.module.adventure.entity.MacroParty;
import main.game.module.adventure.global.TimeMaster;
import main.game.module.adventure.gui.map.MapComp;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.area.AreaManager;
import main.game.module.adventure.rules.TurnRule;
import main.game.module.adventure.travel.TravelMasterOld;
import main.game.module.adventure.utils.SaveMaster;
import main.system.audio.DC_SoundMaster;
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
        setState(macroGame.getState());
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
        TravelMasterOld.newTurn();
        AreaManager.newTurn();
        refreshAll();
        getMapComp().refresh();
    }

    @Override
    public MacroGame getGame() {
        return (MacroGame) super.getGame();
    }

    public void dataChanged() {
        if (MacroManager.isEditMode()) {
            MacroManager.getEditorView().refresh();
        } else {
            MacroManager.refreshGui();
        }
    }

    @Override
    public void infoSelect(Obj obj) {
        if (MacroManager.isEditMode()) {
            MacroManager.getEditorView().setInfoObj(obj);
        } else {
            MacroManager.getMapView().getMacroInfoPanel().setInfoObj(obj);
        }
        if (getInfoObj() != null) {
            getInfoObj().setInfoSelected(false);
        }
        super.infoSelect(obj);
        if (obj != null) {
            obj.setInfoSelected(true);
        }

        getMapComp().refresh();
    }

    @Override
    public void refreshAll() {
        if (MacroManager.isEditMode()) {
            MacroManager.getEditorView().refresh();
        } else {
            MacroManager.getMapView().refresh();
        }

    }

    public void objClicked(Obj obj) {
        if (isSelecting()) {
            Integer id = null;
            if (getSelectionSet().contains(obj)) {
                id = obj.getId();
                DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_TARGET_SELECTED);
            } else {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
            }
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_MAP_OBJ, id);
            setSelecting(false);
        } else {
            infoSelect(obj);
        }
    }

    public void cancelSelection() {
        WaitMaster.receiveInput(WAIT_OPERATIONS.SELECT_MAP_OBJ, null);
        setSelecting(false);
        MacroActionManager.setActionsBlocked(false);
    }

    public Integer select(Collection<? extends Obj> objects) {
        setSelecting(true);
        selectionSet = new HashSet<>();
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
//        highlight();
        return select(filter.getObjects(ref), ref);
    }

    public void highlight(Set<Obj> set) {
        getMapComp().highlight(set);

    }

    public void highlightsOff() {
        getMapComp().highlightsOff();

    }

    public MapComp getMapComp() {
        if (MacroManager.isEditMode()) {
            return MacroManager.getEditorView().getMapComp();
        }
        return MacroManager.getMapView().getMapComp();
    }

    public Set<Obj> getSelectionSet() {
        return selectionSet;
    }

    public void setSelectionSet(Set<Obj> selectionSet) {
        this.selectionSet = selectionSet;
    }

    @Override
    public boolean endRound() {
        // TODO Auto-generated method stub

        return false;
    }

    public void win(Player winningPlayer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void buffCreated(BuffObj buff, Obj basis) {

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
    protected void checkEventIsGuiHandled(Event event) {

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
    public Integer select(Set<Obj> selectingSet, Ref ref) {
        return null;
    }


    public Place getPlaceForPoint(String point) {
//        Map<String, Place> map = new HashMap<>();
        //get closest?
        float minDistance = Float.MAX_VALUE;
        Coordinates c = getGame().getPointMaster().getCoordinates(point);
        Place place=null ;
        for (Place sub : getGame().getPlaces()) {
            float distance = new Vector2(c.x, c.y).dst(new Vector2(sub.getX(), sub.getY()));
            if (distance < minDistance) {
                minDistance=distance;
                place = sub;
            }
            //can we not attach click listeners to emtiterActors?!
        }
        return place;
    }
}
