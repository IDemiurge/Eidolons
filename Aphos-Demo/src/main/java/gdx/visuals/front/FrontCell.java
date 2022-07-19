package gdx.visuals.front;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.content.consts.libgdx.GdxColorMaster;
import gdx.controls.CellMouseListener;
import libgdx.GdxMaster;
import libgdx.anims.actions.FloatActionLimited;
import libgdx.anims.sprite.SpriteX;
import libgdx.gui.generic.GroupX;
import libgdx.screens.batch.CustomSpriteBatch;
import logic.functions.GameController;
import logic.functions.MoveLogic;
import logic.lane.HeroPos;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.ColorManager;

import java.util.Collection;

public class FrontCell extends GroupX {
    public static final int STATE_NORMAL = 0;
    public static final int STATE_REACHABLE = 1;
    public static final int STATE_REACHABLE2 = 2;
    public static final int STATE_HOVER = 3;
    public static final int STATE_REACHABLE_HOVER = 4;
    public static final int STATE_REACHABLE2_HOVER = 5;
    private static final int FPS = 12;
    private static FrontCell hovered;
    private HeroPos pos;
    private SpriteX sprite;
    private SpriteX overlay;
    private boolean jumpReachable, stepReachable, hover;
    private int state;
    private FloatActionLimited adjustAction;


    public FrontCell(HeroPos pos) {
        this.pos = pos;
        addActor(sprite = new SpriteX("sprite\\cell\\veil.txt"));
        addActor(overlay = new SpriteX("sprite\\cell\\cell_rotate_warp.txt"));

        sprite.setBlending(GenericEnums.BLENDING.SCREEN); //inverse if..?
        sprite.setFlipX(pos.isLeftSide());
        sprite.setFlipY(pos.isFront());
        sprite.setFps(FPS);
        sprite.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.SUN);
        GuiEventManager.bind(GuiEventType.POS_UPDATE, p -> resetReachable((HeroPos) p.get()));

        setWidth(sprite.getWidth());
        setHeight(sprite.getHeight());
        sprite.setPosition(getWidth() / 2, getHeight() / 2);
        overlay.setPosition(overlay.getWidth() / 2, overlay.getHeight() / 2);
//        GdxMaster.center(sprite);
//        GdxMaster.center(overlay);
//        sprite.addListener(new CellMouseListener(c -> hover(c), ()-> clicked()));
//        sprite.setTouchable(Touchable.enabled);

        addListener(new CellMouseListener(c -> hover(c), () -> GameController.getInstance().getMoveLogic().cellClicked(pos)));
        setTouchable(Touchable.enabled);

        setState(STATE_NORMAL);

        debugAll();
    }

    private void resetReachable(HeroPos heroPos) {
        jumpReachable = false;
        stepReachable = false;
        if (MoveLogic.isReachable(heroPos, pos, 2)) {
            jumpReachable = true;
            stepReachable = true;
        } else
            stepReachable = MoveLogic.isReachable(heroPos, pos, 1);
        reset();
    }

    private void hover(Boolean c) {
        hover = c;
        if (hover) {
            if (hovered != null)
                hovered.hover(false);
            hovered = this;
        }
        reset();
    }

    private void reset() {
            if (jumpReachable) {
                setState(hover ? STATE_REACHABLE2_HOVER : STATE_REACHABLE2);
            } else if (stepReachable) {
                setState(hover ? STATE_REACHABLE_HOVER : STATE_REACHABLE);
            } else
                setState(hover ? STATE_HOVER : STATE_NORMAL);
            overlay.fadeIn();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    protected Actor hit(float x, float y, boolean touchable, Collection<? extends Actor> children) {
        return super.hit(x, y, touchable, children);
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case STATE_NORMAL -> setVals(0.25f);
            case STATE_REACHABLE -> setVals(0.4f);
            case STATE_REACHABLE2 -> setVals(0.6f);
            case STATE_HOVER -> setVals(0.75f);
            case STATE_REACHABLE_HOVER -> setVals(0.85f);
            case STATE_REACHABLE2_HOVER -> setVals(1);
        }
        switch (state) {
            case STATE_NORMAL -> setColor(GdxColorMaster.darker(Color.WHITE, 0.65f));
            case STATE_REACHABLE -> setColor(GdxColorMaster.darker(Color.WHITE, 0.55f));
            case STATE_REACHABLE2 -> setColor(GdxColorMaster.darker(Color.WHITE, 0.45f));
            case STATE_HOVER -> setColor(GdxColorMaster.darker(Color.WHITE, 0.25f));
            case STATE_REACHABLE_HOVER -> setColor(GdxColorMaster.darker(Color.WHITE, 0.15f));
            case STATE_REACHABLE2_HOVER -> setColor(Color.WHITE);
        }
        if (state > STATE_NORMAL) {
            if (!GdxMaster.isVisibleEffectively(overlay))
                overlay.fadeIn();
        } else {
            if (GdxMaster.isVisibleEffectively(overlay))
                overlay.fadeOut();
        }
    }

    @Override
    public void setColor(Color color) {
        sprite.setColor(color);
    }

    private void setVals(float i) {
        i = i * i + 0.2f;
        adjustAction = new FloatActionLimited();
        adjustAction.setStart(sprite.getBaseAlpha()); // ?
        adjustAction.setEnd(i);
        adjustAction.setDuration(1.5f);
        sprite.setBaseAlpha(i);
        sprite.setFps((int) (FPS * i));
        // overlay
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        //TODO
        if (adjustAction !=null)
            if (adjustAction.getTime() >= adjustAction.getDuration()){
            float i = adjustAction.getValue();
            sprite.setBaseAlpha(i);
            sprite.setFps((int) (FPS*i));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }

    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }
}
