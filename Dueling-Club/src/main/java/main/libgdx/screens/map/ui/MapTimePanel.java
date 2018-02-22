package main.libgdx.screens.map.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import main.game.module.adventure.global.GameDate;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.DynamicTooltip;

/**
 * Created by JustMe on 2/9/2018.
 * rotate moon circle?
 * control the middle-'sun' brightness
 * cut the circle in two, don't show the upper part...
 * tooltip
 *
 * Month
 * where to show date?
 * perhaps I will now display what later will become a tooltip?
 * date could be displayed in a corner, classic fashion
 *
 * dawn/noon/dusk/night
 */
public class MapTimePanel extends Group{

    ImageContainer sun;
    ImageContainer activeMoon;
    ImageContainer moonCircle;
    ValueContainer dateContainer;

    @Override
    public void act(float delta) {
        super.act(delta);
       /*
       elapsed+=delta*mod;
        DAY_TIME time;
        if (time!=this.time){
            update();
        }



        moveSun();
        moveMoons();
        adjustSun();
        adjustMoons();
        rotate();
        */
    }

    public void init(){
        addListener(new DynamicTooltip(()-> getDateString()).getController());
    }

    private String getDateString() {
        //show exact time on tooltip?

        GameDate date;
//        getPhase();
        String string="";
        return string;
    }
}
