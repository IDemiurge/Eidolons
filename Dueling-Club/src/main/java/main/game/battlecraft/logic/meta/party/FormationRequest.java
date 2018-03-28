package main.game.battlecraft.logic.meta.party;

/*
    hero requests:
    parameters like "battle thirst", "battle weariness" (accumulating)
    against "battle trepidation" (specific for each hero, calc based on some prefs)
     */
public class FormationRequest {
    boolean vanOrRear;
    String actorName;
    Integer lineId; //random == null

    public FormationRequest(boolean vanOrRear, String actorName, Integer lineId) {
        this.vanOrRear = vanOrRear;
        this.actorName = actorName;
        this.lineId = lineId;
    }

    public void refused() {
        //how to calc effects?
    }

    public void accepted() {
        //set formation, add Trust/...
    }

    public String getText() {
        return super.toString();
    }

}
