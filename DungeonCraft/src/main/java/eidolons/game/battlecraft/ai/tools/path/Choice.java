package eidolons.game.battlecraft.ai.tools.path;

import eidolons.ability.effects.oneshot.mechanic.ChangeFacingEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
import eidolons.game.core.Eidolons;
import main.ability.effects.Effect;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 4/13/2017.
 */
public class Choice {
    private final Coordinates coordinates;
    private Coordinates prevCoordinates;
    private final List<Action> actions;
    private Boolean[] turns;
    int maxUnitsInStack;

    public Choice(Coordinates targetCoordinate, Action... actions) {
        this(targetCoordinate, null, actions);
    }

    public Choice(Coordinates targetCoordinate, Coordinates prevCoordinates, Action... actions) {
        this.coordinates = targetCoordinate;
        this.prevCoordinates = prevCoordinates;
        this.actions = new ArrayList<>(Arrays.asList(actions));

        maxUnitsInStack=
        Eidolons.getGame().getRules().getStackingRule().getStackFactor(coordinates,
                actions[0].getSource(), false);

    }

    public boolean equals(Object obj) {
        if (obj instanceof Choice) {
            Choice choice = (Choice) obj;
            if (choice.getCoordinates().equals(getCoordinates())) {
                return choice.getActions().equals(getActions());
            }
        }
        return false;
    }

    public Boolean[] getTurns() {
        if (actions.size() == 1 || turns != null) {
            return turns;
        }
        try {
            List<Boolean> list = new ArrayList<>();
            for (Action a : actions) {
                DC_ActiveObj active = a.getActive();
                if (active.getName().contains("lockwise")) {
                    if (!active.isConstructed()) {
                        active.construct();
                    }
                    for (Effect e : active.getAbilities().getEffects()) {
                        if (e instanceof ChangeFacingEffect) {
                            list.add(((ChangeFacingEffect) e).isClockwise());
                        }
                    }

                }
            }
            turns = list.toArray(new Boolean[0]);
            return turns;
        } catch (Exception e) {

        }
        return null;
    }

    public String toString() {
        if (actions.size() == 1) {
            return actions.get(0).getActive().getName() + " to " + coordinates;
        }
        return ContainerUtils.joinStringList(ContainerUtils.toNameList(AiUnitActionMaster
         .getActionObjectList(actions)), ", ")
         + " to " + coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<Action> getActions() {
        return actions;
    }

    public Coordinates getPrevCoordinates() {
        return prevCoordinates;
    }

    public void setPrevCoordinates(Coordinates prevCoordinates) {
        this.prevCoordinates = prevCoordinates;
    }

}
