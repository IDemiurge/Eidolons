package eidolons.libgdx.gui.panels.dc.actionpanel.facing;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.ActionInput;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.mouse.HoverListener;
import eidolons.libgdx.gui.datasource.FullUnitDataSource;
import eidolons.libgdx.gui.panels.GroupX;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.clock.GearCluster;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.action.context.Context;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FacingPanel extends TablePanel {

//    private static final String FACE_BACKGROUND = StrPathBuilder.build(
//     PathFinder.getComponentsPath(),  "2018", "facing",
//     "FACE_BACKGROUND.png");
    private static final String ROTATE_BACKGROUND = StrPathBuilder.build(
     PathFinder.getComponentsPath(),  "2018", "facing",
     "BACKGROUND.png");
//    private final ImageContainer faceBackground;
    private final GearCluster gearsClockwise;
    private final GearCluster gearsAntiClockwise;
    boolean sneaking;
    FadeImageContainer face;
    ImageContainer arrow;
    GroupX background;
    private FullUnitDataSource dataSource;
    private FACING_DIRECTION facing;
    private float fadePercentage;


    public FacingPanel() {
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO,
         p -> {
             setUserObject(new FullUnitDataSource((Unit) p.get()));
         });
        addActor(background  =new GroupX());
        background.addActor(new ImageContainer(ROTATE_BACKGROUND));
//        background.addActor(arrow  =new ImageContainer(ARROW));
//        addActor(faceBackground  =new ImageContainer(FACE_BACKGROUND));
//        faceBackground.setPosition(20, 20);
        addActor(face  =new FadeImageContainer());
        face.setPosition(40, 40);
        face.setFadeDuration(getAnimationDuration());
        background.addActor(gearsClockwise = new GearCluster(0.1f));
        background. addActor(gearsAntiClockwise = new GearCluster(0.1f));
        gearsClockwise.setPosition(0,
         GdxMaster.centerWidth(gearsClockwise));
        gearsClockwise.setPosition(GdxMaster.top(gearsClockwise),
         GdxMaster.centerWidth(gearsClockwise));

        gearsClockwise.addListener(getGearListener(true));

         addListener(new HoverListener((Boolean enter) -> {
            if (enter) {
                gearsClockwise.setZIndex(Integer.MAX_VALUE);
                gearsAntiClockwise.setZIndex(Integer.MAX_VALUE);
            } else {
                gearsClockwise.setZIndex(0);
                gearsAntiClockwise.setZIndex(0);
                ActorMaster.addScaleAction(gearsAntiClockwise, 0.5f, 0.5f);
            }

        }

         ));
    }

    private EventListener getGearListener(boolean clockwise) {
        return new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                GuiEventManager
                WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
                 new ActionInput(dataSource.getTurnAction(clockwise),
                  new Context(dataSource.getEntity().getRef())));
                GearCluster gear = clockwise ? gearsClockwise : gearsAntiClockwise;
                gear.activeWork(0.25f, 0.5f);
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    @Override
    public void updateAct(float delta) {
        dataSource = (FullUnitDataSource) getUserObject();
        if (facing == dataSource.getFacing())
            return;
        boolean animated = true;
        if (facing == null) {
            animated = false;
        }
        if (animated) {
            ActorMaster.addRotateByAction(background,
             facing.getDirection().getDegrees(),
             dataSource.getFacing().getDirection().getDegrees());
        } else {
            background.setRotation(dataSource.getFacing().getDirection().getDegrees());
        }
        face.setImage(getImage(dataSource.getFacing()));
        facing = dataSource.getFacing();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private float getAnimationDuration() {
        return 0.5f;
    }

    private String getImage(FACING_DIRECTION facing) {
        return StrPathBuilder.build(PathFinder.getComponentsPath(),
         "2018", "facing", "face " + facing.toString() + ".png");
    }
}
