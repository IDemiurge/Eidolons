package eidolons.ability.effects.containers.group;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.EmptyCondition;
import main.entity.obj.Obj;
import main.system.entity.FilterMaster;

import java.util.ArrayList;
import java.util.List;

public class PartyEffect extends GroupObjModifyEffect {

    @AE_ConstrArgs(argNames = {" valueName", " amount", "filter conditions",
     "add buff?", "prop?"})
    public PartyEffect(String valueName, String value, Condition c,
                       Boolean buff, Boolean prop) {
        super(null, "", "", valueName, value, buff, prop, c);
    }

    @AE_ConstrArgs(argNames = {"filter prop", "filter value", " valueName",
     " amount", "add buff?", "prop?"})
    public PartyEffect(String filterValueName, String filterValue,
                       String valueName, String value, Boolean buff, Boolean prop) {
        super(null, filterValueName, filterValue, valueName, value, buff, prop);
    }

    @Override
    protected boolean checkApplyInSimulation() {
        return true;
    }

    @Override
    protected List<Obj> getObjectsToModify() {

        DC_Player player = (DC_Player) ref.getSourceObj().getOwner();
        List<Unit> list = new ArrayList<>(player.collectControlledUnits_());

        initFilterConditions();
        FilterMaster.filter(list, conditions);

        List<Obj> objList = new ArrayList<>(list);
        return objList;
    }

    @Override
    protected Condition getDefaultConditions() {
        return new EmptyCondition();
    }

    @Override
    protected List<Obj> getObjectsByName(String objName) {
        return null;
    }

}
