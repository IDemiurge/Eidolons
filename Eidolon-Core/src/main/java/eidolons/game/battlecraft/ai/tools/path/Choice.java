package eidolons.game.battlecraft.ai.tools.path;

import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import eidolons.game.battlecraft.ai.elements.actions.AiUnitActionMaster;
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
    private final List<AiAction> aiActions;

    public Choice(Coordinates targetCoordinate, AiAction... aiActions) {
        this(targetCoordinate, null, aiActions);
    }

    public Choice(Coordinates targetCoordinate, Coordinates prevCoordinates, AiAction... aiActions) {
        this.coordinates = targetCoordinate;
        this.prevCoordinates = prevCoordinates;
        this.aiActions = new ArrayList<>(Arrays.asList(aiActions));


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

    public String toString() {
        if (aiActions.size() == 1) {
            return aiActions.get(0).getActive().getName() + " to " + coordinates;
        }
        return ContainerUtils.joinStringList(ContainerUtils.toNameList(AiUnitActionMaster
         .getActionObjectList(aiActions)), ", ")
         + " to " + coordinates;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public List<AiAction> getActions() {
        return aiActions;
    }

    public Coordinates getPrevCoordinates() {
        return prevCoordinates;
    }

    public void setPrevCoordinates(Coordinates prevCoordinates) {
        this.prevCoordinates = prevCoordinates;
    }

}
