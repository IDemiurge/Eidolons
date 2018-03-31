package main.libgdx.gui.panels.dc.clock;

import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.gui.panels.GroupX;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 3/28/2018.
 */
public class ClockActor extends GroupX{
    boolean exploreMode;
    boolean paused;

    float timeToTurn;
    float speed; //
//    ClockHand smallHand;
//    ClockHand hand;

    public void bindEvents(){
        GuiEventManager.bind(GuiEventType.TIME_PASSED, p->{
            float time = (float) p.get();
            timeToTurn= time;
        });
    }

    private void initActions() {

    }
    @Override
    public void act(float delta) {
        super.act(delta);
        initActions();
        if (ExplorationMaster.isExplorationOn() != exploreMode) {

        }


    }



}

