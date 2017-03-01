package main.libgdx.gui.panels.dc.unitinfo.dto;

public class InitiativeAndActionPointsDTO {
    private String initiative;
    private String actionPoints;

    public InitiativeAndActionPointsDTO(String initiative, String actionPoints) {
        this.initiative = initiative;
        this.actionPoints = actionPoints;
    }

    public String getInitiative() {
        return initiative;
    }

    public String getActionPoints() {
        return actionPoints;
    }
}
