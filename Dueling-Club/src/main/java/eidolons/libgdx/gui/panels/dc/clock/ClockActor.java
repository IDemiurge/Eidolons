package eidolons.libgdx.gui.panels.dc.clock;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.GroupX;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 3/28/2018.
 */
public class ClockActor extends GroupX {
    private static final String BACKGROUND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "2018", "clock", "background.png");
    private static final String SMALL_HAND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "2018", "clock", "SMALL HAND.png");
    private static final String HAND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "2018", "clock", "HAND.png");
    boolean exploreMode;
    boolean paused;
    //TODO gears
    float timeToTurn;
    ImageContainer smallHand;
    ImageContainer hand;
    private float displayedTime=0;

    public ClockActor() {
        addActor(new ImageContainer(BACKGROUND));
        addActor(hand = new ImageContainer(HAND));
        addActor(smallHand = new ImageContainer(SMALL_HAND));

        smallHand.setOrigin(0, smallHand.getHeight() / 2);
        hand.setOrigin(0, smallHand.getHeight() / 2);

        smallHand.setPosition(getWidth() / 2, getHeight() / 2);
        hand.setPosition(getWidth() / 2, getHeight() / 2);

        smallHand.setRotation(90);
        hand.setRotation(90);
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.NEW_ATB_TIME, p -> {
            float time = (float) p.get();
            timeToTurn = time - displayedTime;
            displayedTime=time;
        });
    }

    private void initActions() {
        if (timeToTurn > 0) {
            if (exploreMode) {
                if (timeToTurn < 1)
                    return;
                ActorMaster.addRotateByAction(smallHand, 6 * timeToTurn); //60 sec == 360 degrees
            } else {
                if (timeToTurn < 0.1f)
                    return;
                ActorMaster.addRotateByAction(smallHand, 30 * timeToTurn); //12 sec == 360 degrees
            }
            timeToTurn = 0;
        }
    }

    private void setExploreMode(boolean exploreMode) {
        if (exploreMode) {
            float time = DC_Game.game.getDungeonMaster().getExplorationMaster().
             getTimeMaster().getTime();
            displayedTime=time;

            float minutes = time / 60;
            float seconds = time % 60;

            ActorMaster.addRotateByAction(smallHand, smallHand.getRotation(), seconds * 6);
            ActorMaster.addRotateByAction(hand, hand.getRotation(), minutes * 6);

        } else {
            ActorMaster.addRotateByAction(smallHand, smallHand.getRotation(), 90);
            ActorMaster.addRotateByAction(hand, hand.getRotation(), 90);
            displayedTime=0;
        }
        this.exploreMode = exploreMode;

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        smallHand.setPosition(getWidth() / 2, getHeight() / 2);
        hand.setPosition(getWidth() / 2, getHeight() / 2);
        if (ExplorationMaster.isExplorationOn() != exploreMode) {

            setExploreMode(ExplorationMaster.isExplorationOn());
        }
        if (exploreMode) {
            float time = DC_Game.game.getDungeonMaster().getExplorationMaster().
             getTimeMaster().getTime();
            timeToTurn = time - displayedTime;
            displayedTime = time;//+delta;
        }

        initActions();

    }


}

