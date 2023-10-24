package combat.turns;

import combat.BattleHandler;
import combat.sub.BattleManager;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.field.Unit;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 10/21/2023
 */
public class TurnHandler extends BattleHandler {

    private List<InitiativeGroup> groups;
    private InitiativeGroup current;

    public TurnHandler(BattleManager battleManager) {
        super(battleManager);
    }

    public static int calcInitiative(Unit unit) {
        return unit.getInt(UnitParam.AP) * 2 + getPositionModifier(unit);
    }

    private static int getPositionModifier(Unit unit) {
        return switch(unit.getPos().getCell().type){
            case Flank -> -2;
            case Van -> 1;
            case Front -> 0;
            case Back -> -1;
            case Rear -> -3;
            case Reserve -> -999;
        };
    }

    @Override
    public void newRound() {
        forEach(unit -> unit.setValue(UnitParam.Initiative, calcInitiative(unit)));
        groups = createInitiativeGroups();
        groupFinished();
    }

    public List<InitiativeGroup> getInitiativeGroups() {
        return groups;
    }

    public void groupFinished() {
        current = nextGroup();
        current.units.forEach(unit -> unit.setProp(UnitProp.Active, true));
    }

    public InitiativeGroup getCurrent() {
        return current;
    }


    public InitiativeGroup nextGroup(){
        return groups.get(0) ;
    }

    @Override
    public void reset() {
        //what should be done after each ACTION? same init recalc?

        //ONLY AFTER GROUP IS FINISHED! But we can displayed some future precalc
        // groups = createInitiativeGroups();


        //how to check if any initiative has changed and trigger group re-build? some hashcode for the sum?
        //some groups are already DONE - then their initiative is what?
        //use some history manager? with all actions etc?
        //some units will PASS their turn without spending all AP! So - 'over' status, as well as WAIT?

    }

    public List<Unit> sorted(List<Unit> list) {
        return list.stream().sorted(getComparator()).collect(Collectors.toList());
    }

    private Comparator<Unit> getComparator() {
        return (o1, o2) -> {
            if (o1.initiative() == o2.initiative())
                return 0;
            if (o1.initiative() > o2.initiative())
                return 1;
            return -1;
        };
    }

    private Comparator<InitiativeGroup> getGroupComparator() {
        return (o1, o2) -> {
            if (o1.maxInitiative == o2.maxInitiative)
                return o1.ally ? 1 : -1;
            if (o1.maxInitiative > o2.maxInitiative)
                return 1;
            return -1;
        };
    }

    public List<InitiativeGroup> createInitiativeGroups() {
        List<InitiativeGroup> ally_groups = createInitiativeGroups(true);
        List<InitiativeGroup> enemy_groups = createInitiativeGroups(false);
        List<InitiativeGroup> sortedGroups = new ArrayList<>();
        sortedGroups.addAll(ally_groups);
        sortedGroups.addAll(enemy_groups);
        Collections.sort(sortedGroups, getGroupComparator());
        return sortedGroups;
    }

    public List<InitiativeGroup> createInitiativeGroups(boolean ally) {
        List<Unit> units = sorted(getEntities().getUnitsFiltered(
                u -> u.isAlly() == ally &&
                        !u.isTrue(UnitProp.FinishedTurn) &&
                        !u.isTrue(UnitProp.Dead)));
        List<InitiativeGroup> groups = new ArrayList<>();
        int max = 3;
        int maxDiff = 1;
        Integer prevInitiative = null;
        ListIterator<Unit> iterator = units.listIterator();

        Unit nextUnit = null;
        loop:
        while (true) {
            List<Unit> sub = new ArrayList<>();
            if (nextUnit != null)
                sub.add(nextUnit);
            int maxInitiative = 0;
            for (int i = 0; i < max; i++) {
                if (!iterator.hasNext()) {
                    groups.add(new InitiativeGroup(sub, prevInitiative, ally));
                    break loop;
                }
                maxInitiative = units.get(iterator.nextIndex()).initiative();
                nextUnit = iterator.next();
                //if the closest next unit has 2+ initiative behind, they cannot be in this group
                if (prevInitiative != null && nextUnit.initiative() - prevInitiative > maxDiff) {
                    prevInitiative = nextUnit.initiative();
                    break;
                }
                prevInitiative = nextUnit.initiative();
                sub.add(nextUnit);
                nextUnit = null;
            }
            if (nextUnit == null) //exited normally
                groups.add(new InitiativeGroup(sub, maxInitiative, ally));

        }
        return groups;

    }
}
