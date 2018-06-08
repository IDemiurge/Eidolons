package eidolons.game.macro;

/**
 * Created by Giskard on 6/8/2018.
 */
public class Macro {


    private World world;

    Macro(World world){

        this.world = world;

    }

    public void moveMainParty(int dx, int dy){
        world.moveMainParty(dx,dy);
    }
}
