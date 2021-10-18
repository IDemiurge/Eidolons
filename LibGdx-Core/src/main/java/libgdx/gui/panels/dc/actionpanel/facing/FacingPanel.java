package libgdx.gui.panels.dc.actionpanel.facing;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.datasource.FullUnitDataSource;
import libgdx.gui.generic.GearCluster;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.action.context.Context;
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
    private final GearCluster gears;
    FadeImageContainer face;
    GroupX background;
    private FullUnitDataSource dataSource;
    private FACING_DIRECTION facing;
    //    private static final String FACE_BACKGROUND = StrPathBuilder.build(
    //     PathFinder.getComponentsPath(),  "dc", "facing",
    //     "FACE_BACKGROUND.png");
    //    private final ImageContainer faceBackground;

    private final SymbolButton btnTurnClockwise;
    private final SymbolButton btnTurnAntiClockwise;
    boolean sneaking;


    public FacingPanel() {
        GuiEventManager.bind(GuiEventType.UPDATE_MAIN_HERO,
                p -> {
                    if (p.get() == null)
                        setUserObject(new FullUnitDataSource(Eidolons.getMainHero()));
                    else
                        setUserObject(new FullUnitDataSource((Unit) p.get()));
                });
        addActor(gears = new GearCluster(0.65f));
        addActor(background = new GroupX());
        TextureRegion texture = TextureCache.getOrCreateR(ROTATE_BACKGROUND);
        background.addActor(new Image(texture));
        background.setSize(
                texture.getRegionWidth(),
                texture.getRegionHeight());

        background.setOrigin(background.getWidth() / 2, background.getHeight() / 2);

        addActor(face = new FadeImageContainer());
        face.setPosition(24, 8);
        face.setFadeDuration(getAnimationDuration() / 1.5f);
        //TODO into smartbutton
        face.addListener(new SmartClickListener(this) {
            @Override
            protected boolean isBattlefield() {
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //                DungeonScreen.getInstance().getCameraMan().centerCameraOn(DC_Game.game.getManager().getMainHero());

                GuiEventManager.triggerWithParams(GuiEventType.CAMERA_PAN_TO_UNIT, Eidolons.getMainHero(), 2f);
                DungeonScreen.getInstance().getController().inputPass();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        background.addActor(btnTurnClockwise =
                new SymbolButton(ButtonStyled.STD_BUTTON.UP, () -> turn(true)));
        btnTurnClockwise.setNoClickCheck(true);
        btnTurnClockwise.setFlipY(true);
        background.addActor(btnTurnAntiClockwise = new SymbolButton(ButtonStyled.STD_BUTTON.UP, () -> turn(false)));
        btnTurnAntiClockwise.setNoClickCheck(true);
        float center = GdxMaster.centerWidth(btnTurnAntiClockwise);
        float top = background.getHeight() - btnTurnAntiClockwise.getHeight();
        btnTurnAntiClockwise.setPosition(center, top);
        btnTurnClockwise.setPosition(center, -top / 2);

        // btnTurnClockwise.setScale(0.5f);
        // btnTurnAntiClockwise.setScale(0.5f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        btnTurnClockwise.setNoClickCheck(true);
        btnTurnAntiClockwise.setNoClickCheck(true);
        float center = GdxMaster.centerWidth(btnTurnAntiClockwise);
        float top = background.getHeight() - btnTurnAntiClockwise.getHeight();
        btnTurnAntiClockwise.setPosition(center, -top / 2 + btnTurnAntiClockwise.getWidth() - 12);
        btnTurnClockwise.setPosition(center, top - btnTurnAntiClockwise.getWidth() + 12);
    }

    private void move(boolean forward) {
        DC_Game.game.getLoop().actionInputManual(
                new ActionInput(dataSource.getEntity().getAction(forward? "Move" : "Move Back"),
                        new Context(dataSource.getEntity().getRef())));
        gears.activeWork(0.25f, 0.5f);
    }
        private void turn(boolean clockwise) {
        DC_Game.game.getLoop().actionInputManual(
                new ActionInput(dataSource.getTurnAction(clockwise),
                        new Context(dataSource.getEntity().getRef())));
        gears.activeWork(0.25f, 0.5f);
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
            ActionMasterGdx.addRotateByAction(background,
                    facing.getDirection().getDegrees(),
                    dataSource.getFacing().getDirection().getDegrees());
        } else {
            background.setRotation(dataSource.getFacing().getDirection().getDegrees());
        }
        face.setImage(getImage(dataSource.getFacing()));
        facing = dataSource.getFacing();
    }


    private float getAnimationDuration() {
        return 0.85f;
    }

    private String getImage(FACING_DIRECTION facing) {
        return StrPathBuilder.build(PathFinder.getComponentsPath(),
                "dc", "facing", "face " + facing.toString() + ".png");
    }
}
