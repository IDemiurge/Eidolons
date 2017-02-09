package main.ability.effects.group;

import main.client.cc.logic.party.PartyObj;
import main.data.ability.AE_ConstrArgs;
import main.elements.conditions.Condition;
import main.elements.conditions.standard.EmptyCondition;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.system.FilterMaster;

import java.util.LinkedList;
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
        PartyObj party = (PartyObj) ref.getSourceObj().getRef()
                .getObj(KEYS.PARTY);
        List<DC_HeroObj> list = party.getMembers();

        initFilterConditions();
        FilterMaster.filter(list, conditions);

        List<Obj> objList = new LinkedList<>();
        for (DC_HeroObj j : list) {
            objList.add(j);
        }
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
