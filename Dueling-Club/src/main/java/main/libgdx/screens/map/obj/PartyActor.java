package main.libgdx.screens.map.obj;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates;
import main.game.module.adventure.entity.MacroParty;
import main.libgdx.anims.ActorMaster;
import main.libgdx.screens.map.obj.PartyActorFactory.PartyActorParameters;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager.BORDER;

/**
 * Created by JustMe on 2/7/2018.
 */
public class PartyActor extends MapActor {
    public static final int offsetX = 11;
    public static final int offsetY = 14;
    public static final int emblemOffsetX = 109;
    public static final int emblemOffsetY = 113;
    private Image emblem;
    private Image modeIcon;
    private Image ironBorder;
    private MoveToAction orderAction;
    private MacroParty party;

    public PartyActor(PartyActorParameters parameters) {
        super(parameters.mainTexture);
        init(parameters);
    }


    private void init(PartyActorParameters parameters) {
        setPosition(parameters.position.x, parameters.position.y);
        portrait.setPosition(offsetX, offsetY);
        party = parameters.party;
        addActor(ironBorder = new Image(TextureCache.getOrCreateR(BORDER.MAP_PARTY_CIRCLE.getImagePath())));
        ironBorder.setScale(getPortraitScale());
        setSize(ironBorder.getWidth(), ironBorder.getHeight());
        setTeamColor(parameters.color);
        addActor(emblem = new Image(parameters.emblem));

        init();
        minimize();
    }

    public void hover() {
        if (getAllChildrenActions(MoveToAction.class).size > 0)
            return;
        hovered = true;
        ActorMaster.addScaleAction(highlight.getContent(), 1.0f, 1.0f, 0.5f);
        //highlight underlay?

        highlight.setZIndex(0);
        highlight.setVisible(true);

        portrait.setVisible(true);
        portrait.setColor(1, 1, 1, 0);
        ActorMaster.addFadeInAction(portrait, 0.5f);
        ActorMaster.addScaleAction(portrait, 1.0f, 1.0f, 0.5f);

        ActorMaster.addFadeInAction(ironBorder, 1.0f);
        ActorMaster.addMoveToAction(ironBorder, 0, 0, 0.75f);

        ActorMaster.addFadeInAction(highlight, 0.5f);
        ActorMaster.addScaleAction(highlight.getContent(), 1.0f, 1.0f, 0.5f);
        ActorMaster.addMoveToAction(emblem,
         getWidth() - emblem.getWidth()
//          *getEmblemScale()
         , getHeight() - emblem.getHeight()
//          *getEmblemScale()
         , 0.5f);

        ActorMaster.addScaleAction(portrait, getPortraitScale()
         , 0.5f);

        ActorMaster.addScaleAction(emblem, getEmblemScaleHovered(), 0.5f);
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
        if (orderAction != null) {
            removeAction(orderAction);
        }
        orderAction = new MoveToAction() {
            @Override
            protected void update(float percent) {
                super.update(percent);
                //can we update logic model x/y here?
            }
        };
        orderAction.setPosition(x, y);
        float distance = new Vector2(x, y).dst(new Vector2(getX(), getY()));
        orderAction.setDuration(distance / getSpeed());
        addAction(orderAction);
        orderAction.setTarget(this);
    }

    private float getSpeed() {
        return 210;
    }

    public void minimize() {
        if (getAllChildrenActions(MoveToAction.class).size > 0)
            return;
        hovered = false;
        emblem.setTouchable(Touchable.enabled);
        ActorMaster.addFadeOutAction(ironBorder, 1.25f);
        ActorMaster.addMoveToAction(ironBorder, -ironBorder.getWidth() / 2, 0, 1.25f);

        ActorMaster.addFadeOutAction(portrait, 0.5f);
        ActorMaster.addMoveToAction(emblem, 0f, 0f, 0.5f);

        ActorMaster.addFadeOutAction(highlight, 0.5f);
        ActorMaster.addScaleAction(highlight.getContent(), 0f, 0f, 0.5f);

        ActorMaster.addScaleAction(emblem, getEmblemScaleDefault(), 0.5f);

        ActorMaster.addScaleAction(portrait, 0
         , 0.5f);

    }


    @Override
    public void act(float delta) {
        super.act(delta);
        party.setCoordinates(new Coordinates(getX()+getWidth()/2, getY()+getHeight()/2));
//        party.getCoordinates().setX(); Coordinates(getX()+getWidth()/2, getY()+getHeight()/2));

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
}
