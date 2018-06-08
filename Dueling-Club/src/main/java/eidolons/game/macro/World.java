package eidolons.game.macro;

import java.util.List;

/**
 * Created by Giskard on 6/8/2018.
 */
public class World {

    private final int width = 2988;
    private final int height = 2988;

    private Party mainParty;


    private List<Place> places;

    World(List<Place> places, Party mainParty){
        this.mainParty = mainParty;
        this.places = places;
    }

    public void moveMainParty(int dx, int dy){
                        
    }



}
