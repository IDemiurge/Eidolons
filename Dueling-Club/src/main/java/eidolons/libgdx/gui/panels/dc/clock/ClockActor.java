package eidolons.libgdx.gui.panels.dc.clock;

import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 3/28/2018.
 */
public class ClockActor extends GroupX {
    private static final String BACKGROUND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "dc", "clock", "background.png");
    private static final String SMALL_HAND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "dc", "clock", "SMALL HAND.png");
    private static final String HAND = StrPathBuilder.build(PathFinder.getComponentsPath(),
     "dc", "clock", "HAND.png");
    private final ImageContainer background;

    GearCluster centerGears;
    GearCluster underGears;
    boolean exploreMode;
    boolean paused;
    //TODO gears
    float timeToTurn;
    ImageContainer smallHand;
    ImageContainer hand;
    private float displayedTime = 0;

    public ClockActor() {
//        addActor(
        underGears = new GearCluster(5, 1f, false);
//        addActor(new GearCluster(5, 1f, true));
        addActor(centerGears = new GearCluster(5, 0.65f, true));

        addActor(background = new ImageContainer(BACKGROUND));
        GdxMaster.center(centerGears);
        centerGears.setSize(background.getWidth()/2, background.getHeight()/2);

        addActor(hand = new ImageContainer(HAND));
        addActor(smallHand = new ImageContainer(SMALL_HAND));
        setSize(background.getWidth(), background.getHeight());

//        centerGears.setPosition(GdxMaster.centerWidth(centerGears),
//         GdxMaster.centerHeight(centerGears));

        smallHand.setOrigin(0, smallHand.getHeight() / 2);
        hand.setOrigin(0, smallHand.getHeight() / 2);

        smallHand.setPosition(background.getWidth() / 2,
         background.getHeight() / 2 - smallHand.getHeight() / 2
        );
        hand.setPosition(background.getWidth() / 2,
         background.getHeight() / 2 - hand.getHeight() / 2);

//        centerGears.setPosition(smallHand.getX() - 30,
//         smallHand.getY() - 30);
        underGears.setPosition(smallHand.getX() + 30,
         smallHand.getY() + 30);

        smallHand.setRotation(90);
        hand.setRotation(90);
        bindEvents();
    }

    public void bindEvents() {
        GuiEventManager.bind(GuiEventType.NEW_ATB_TIME, p -> {
            float time = (float) p.get();
            timeToTurn = time - displayedTime;
            displayedTime = time;
        });
    }

    private void initActions() {
        if (timeToTurn > 0) {
            if (exploreMode) {
                if (timeToTurn < 1)
                    return;
                displayedTime += timeToTurn;//+delta;
                ActionMaster.addRotateByAction(smallHand, -6 * timeToTurn); //60 sec == 360 degrees
            } else {
                if (timeToTurn < 0.1f)
                    return;
                ActionMaster.addRotateByAction(smallHand, -30 * timeToTurn); //12 sec == 360 degrees
                centerGears.activeWork(0.25f, timeToTurn);
            }
            timeToTurn = 0;
        }
    }

    private void setExploreMode(boolean exploreMode) {
        if (exploreMode) {
            float time = DC_Game.game.getDungeonMaster().getExplorationMaster().
             getTimeMaster().getTime();
            displayedTime = time;

            float minutes = time / 60;
            float seconds = time % 60;

            ActionMaster.addRotateByAction(smallHand, smallHand.getRotation(), seconds * 6);
            ActionMaster.addRotateByAction(hand, hand.getRotation(), minutes * 6);

            centerGears.setDefaultSpeed(0.45f);
        } else {
            centerGears.setDefaultSpeed(0.15f);
            ActionMaster.addRotateByAction(smallHand, smallHand.getRotation(), 90);
            ActionMaster.addRotateByAction(hand, hand.getRotation(), 90);
            centerGears.activeWork(0.25f, 1);
            displayedTime = 0;
        }
        this.exploreMode = exploreMode;

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        smallHand.setPosition(background.getWidth() / 2,
         background.getHeight() / 2 - smallHand.getHeight() / 2
        );
        hand.setPosition(background.getWidth() / 2,
         background.getHeight() / 2 - hand.getHeight() / 2);

        if (ExplorationMaster.isExplorationOn() != exploreMode) {

            setExploreMode(ExplorationMaster.isExplorationOn());
        }
        if (exploreMode) {
            float time = DC_Game.game.getDungeonMaster().getExplorationMaster().
             getTimeMaster().getTime();
            timeToTurn = time - displayedTime;

        }

        initActions();

    }


}

