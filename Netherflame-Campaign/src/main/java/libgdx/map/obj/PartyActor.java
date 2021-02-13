package libgdx.map.obj;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.anims.actions.ActionMaster;
import libgdx.map.obj.PartyActorFactory.PartyActorParameters;
import libgdx.map.path.SteerableParty;
import libgdx.texture.TextureCache;
import eidolons.macro.entity.party.MacroParty;
import main.game.bf.Coordinates;
import main.system.images.ImageManager.BORDER;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActor extends MapActor {
    public static final int offsetX = 11;
    public static final int offsetY = 14;
    public static final int emblemOffsetX = 109;
    public static final int emblemOffsetY = 113;
    boolean marker;
    SteerableParty steerableParty;
    private Image emblem;
    private Image modeIcon;
    private Image ironBorder;
    private MoveToAction orderAction;
    private MacroParty party;
    private boolean moving;


    public PartyActor(PartyActorParameters parameters) {
        super(parameters.mainTexture);
        init(parameters);
    }

    public MacroParty getParty() {
        return party;
    }

    private void init(PartyActorParameters parameters) {
        portrait.setPosition(offsetX, offsetY);
        party = parameters.party;
        addActor(ironBorder = new Image(TextureCache.getOrCreateR(BORDER.MAP_PARTY_CIRCLE.getImagePath())));
        ironBorder.setScale(getPortraitScale());
        setSize(ironBorder.getWidth(), ironBorder.getHeight());
        setTeamColor(parameters.color);
        addActor(emblem = new Image(parameters.emblem));

        init();
        minimize();
        setPosition(parameters.position.x, parameters.position.y);
    }

    public void hover() {
        if (getAllChildrenActions(MoveToAction.class).size > 0)
            return;
        if (getAllChildrenActions(ScaleToAction.class).size > 0)
            return;
        hovered = true;
        ActionMaster.addScaleAction(highlight.getContent(), 1.0f, 1.0f, 0.5f);
        //highlight underlay?

        highlight.setZIndex(0);
        highlight.setVisible(true);

        portrait.setVisible(true);
        portrait.setColor(1, 1, 1, 0);
        ActionMaster.addFadeInAction(portrait, 0.5f);
        ActionMaster.addScaleAction(portrait, 1.0f, 1.0f, 0.5f);

        ActionMaster.addFadeInAction(ironBorder, 1.0f);
        ActionMaster.addMoveToAction(ironBorder, 0, 0, 0.75f);

        ActionMaster.addFadeInAction(highlight, 0.5f);
        ActionMaster.addScaleAction(highlight.getContent(), 1.0f, 1.0f, 0.5f);
        ActionMaster.addMoveToAction(emblem,
         getWidth() - emblem.getWidth()
//          *getEmblemScale()
         , getHeight() - emblem.getHeight()
//          *getEmblemScale()
         , 0.5f);

        ActionMaster.addScaleAction(portrait, getPortraitScale()
         , 0.5f);

        ActionMaster.addScaleAction(emblem, getEmblemScaleHovered(), 0.5f);
    }

    private float getEmblemScaleHovered() {
        return 0.65f;// * GdxMaster.getFontSizeMod()  ;
    }

    private float getEmblemScaleDefault() {
        return 1f;
    }

    private float getPortraitScale() {
        return 0.75f;// TODO * GdxMaster.getFontSizeMod()  ;
    }

    public void move(Vector2 destination) {
        moveTo(destination.x, destination.y);
    }

    public void moveTo(float x, float y) {
        moveTo(x, y, getSpeed());
    }

    public void moveTo(float x, float y, float speed) {
        if (steerableParty == null)
            steerableParty = new SteerableParty(this);
        try {
            steerableParty.moveTo(x, y, speed);
            party.setHasMoved(true);
            setMoving(true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
//        if (orderAction != null) {
//            removeAction(orderAction);
//        }
//        orderAction = FreeTravelMaster.getInstance().travelTo(this,(int) x,(int) y, speed);
//        orderAction.setPosition(x, y);
//        float distance = new Vector2(x, y).dst(new Vector2(getX(), getY()));
//        orderAction.setDuration(distance / speed);
//        addAction(orderAction);
//        orderAction.setTarget(this);
    }

    public float getSpeed() {
        return 2100;
    }

    public void minimize() {
        if (getAllChildrenActions(MoveToAction.class).size > 0)
            return;
        if (getAllChildrenActions(ScaleToAction.class).size > 0)
            return;
        hovered = false;
        emblem.setTouchable(Touchable.enabled);
        ActionMaster.addFadeOutAction(ironBorder, 1.25f);
        ActionMaster.addMoveToAction(ironBorder, -ironBorder.getWidth() / 2, 0, 1.25f);

        ActionMaster.addFadeOutAction(portrait, 0.5f);
        ActionMaster.addMoveToAction(emblem, 0f, 0f, 0.5f);

        ActionMaster.addFadeOutAction(highlight, 0.5f);
        ActionMaster.addScaleAction(highlight.getContent(), 0f, 0f, 0.5f);

        ActionMaster.addScaleAction(emblem, getEmblemScaleDefault(), 0.5f);

        ActionMaster.addScaleAction(portrait, 0
         , 0.5f);

    }


    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    @Override
    public void act(float delta) {
//        FreeTravelMaster.getInstance(). check(this);
        super.act(delta);
        if (!marker) {
            party.setCoordinates(Coordinates.get(getX() + getWidth() / 2, getY() + getHeight() / 2));
        } else {
            setVisible(isMarkerVisible());
        }
//        party.getCoordinates().setX(); Coordinates(getX()+getWidth()/2, getY()+getHeight()/2));
        if (steerableParty != null)
            steerableParty.act(delta);
    }

    private boolean isMarkerVisible() {
        return false;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        party.setCoordinates(Coordinates.get(true,
         (int)  (getX() + getWidth() / 2), (int) (getY() + getHeight() / 2)));
    }

    @Override
    public float getWidth() {
        if (hovered)
            return ironBorder.getWidth();
        return emblem.getWidth();
    }

    @Override
    public float getHeight() {
        if (hovered)
            return ironBorder.getWidth();
        return emblem.getHeight();
    }

    public Image getEmblem() {
        return emblem;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}
