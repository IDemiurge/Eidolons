package eidolons.game.module.dungeoncrawl.objects.shrine;

import eidolons.libgdx.gui.overlay.choice.VC_DataSource;

public class ShrineHandler {
    /*
    interactive obj like any other
    add CURSOR to the general logic

    specials:
    - onetime or not
    - respawn
    - confirms and choices
    -

    animating shrine
     */

    public void interact(Shrine shrine){
        if (shrine.getCharges()>0) {
        }
    }
        public void activatePoint(Shrine shrine){

    }
    public void handleChoice(VC_DataSource.VC_OPTION choice){
        switch (choice) {

            case burn_item:
                break;
            case ash_shape:
                break;
            case drink_blood:
                break;
            case sacrifice_blood:
                break;
            case travel_forward:
                break;
            case travel_back:
                break;
            case eidolon_vision:
                break;

                //SOUL SHRINE
            case imbue_soul:
                break;
            case fracture_soul:
                break;
        }
    }
}
