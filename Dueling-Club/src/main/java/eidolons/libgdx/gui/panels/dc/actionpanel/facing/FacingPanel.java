package eidolons.libgdx.gui.panels.dc.actionpanel.facing;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.datasource.FullUnitDataSource;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

/**
 * Created by JustMe on 3/30/2018.
 */
public class FacingPanel extends TablePanel {

    private static final String ROTATE_BACKGROUND = StrPathBuilder.build(
     PathFinder.getComponentsPath(), "dc", "facing",
     "BACKGROUND.png");
    boolean sneaking;
    FadeImageContainer face;
    GroupX background;
    private FullUnitDataSource dataSource;
    private FACING_DIRECTION facing;
//    private static final String FACE_BACKGROUND = StrPathBuilder.build(
//     PathFinder.getComponentsPath(),  "dc", "facing",
//     "FACE_BACKGROUND.png");
//    private final ImageContainer faceBackground;
//    private  GearCluster gearsClockwise;
//    private  GearCluster gearsAntiClockwise;


    public FacingPanel() {
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO,
         p -> {
            if (p.get()==null )
                setUserObject(new FullUnitDataSource(Eidolons.getMainHero()));
                else
             setUserObject(new FullUnitDataSource((Unit) p.get()));
         });
        addActor(background = new GroupX());
        TextureRegion texture = TextureCache.getOrCreateR(ROTATE_BACKGROUND);
        background.addActor(new Image(texture));
        background.setSize(
         texture.getRegionWidth(),
         texture.getRegionHeight());

        background.setOrigin(background.getWidth() / 2, background.getHeight() / 2);

        addActor(face = new FadeImageContainer());
        face.setPosition(24, 8);
        face.setFadeDuration(getAnimationDuration()/1.5f);
        addListener(new SmartClickListener(this){
            @Override
            protected boolean isBattlefield() {
                return true;
            }
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                DungeonScreen.getInstance().getCameraMan().centerCameraOn(DC_Game.game.getManager().getMainHero());

                GuiEventManager.triggerWithParams(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero() , 2f);
                DungeonScreen.getInstance().getController().inputPass();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
//        background.addActor(gearsClockwise = new GearCluster(0.35f));
//        background. addActor(gearsAntiClockwise = new GearCluster(0.35f));
//        gearsClockwise.addListener(getGearListener(true));
//         addListener(new HoverListener((Boolean enter) -> {
//            if (enter) {
//                gearsClockwise.setZIndex(Integer.MAX_VALUE);
//                gearsAntiClockwise.setZIndex(Integer.MAX_VALUE);
//            } else {
//                gearsClockwise.setZIndex(0);
//                gearsAntiClockwise.setZIndex(0);
//                ActorMaster.addScaleAction(gearsAntiClockwise, 0.5f, 0.5f);
//            }
//        }
//         ));
    }

//    private EventListener getGearListener(boolean clockwise) {
//        return new ClickListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
////                GuiEventManager
//                WaitMaster.receiveInput(WAIT_OPERATIONS.ACTION_INPUT,
//                 new ActionInput(dataSource.getTurnAction(clockwise),
//                  new Context(dataSource.getEntity().getRef())));
//                GearCluster gear = clockwise ? gearsClockwise : gearsAntiClockwise;
//                gear.activeWork(0.25f, 0.5f);
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        };
//    }

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
            ActionMaster.addRotateByAction(background,
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
        return 0.85f;
    }

    private String getImage(FACING_DIRECTION facing) {
        return StrPathBuilder.build(PathFinder.getComponentsPath(),
         "dc", "facing", "face " + facing.toString() + ".png");
    }
}
