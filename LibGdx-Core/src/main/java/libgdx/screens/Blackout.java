package libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class Blackout {
    FloatAction blackoutAction = new FloatAction();
    BlackSprite blackSprite= new BlackSprite();
    private float blackout;
    private boolean blackoutBack;
    private boolean whiteout;

    public Blackout() {
        GuiEventManager.bind(GuiEventType.BLACKOUT_IN, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=false;
            blackout(dur, 1);
        });
        GuiEventManager.bind(GuiEventType.BLACKOUT_OUT, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=false;
            blackout(dur, 0);
        });
        GuiEventManager.bind(GuiEventType.BLACKOUT_AND_BACK, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=false;
            blackoutBack = true;
            blackout(dur, 1);
        });

        GuiEventManager.bind(GuiEventType.WHITEOUT_IN, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=true;
            blackout(dur, 1);
        });
        GuiEventManager.bind(GuiEventType.WHITEOUT_OUT, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=true;
            blackout(dur, 0);
        });
        GuiEventManager.bind(GuiEventType.WHITEOUT_AND_BACK, p -> {
            Float dur = 5f;
            if (p.get() instanceof Float) {
                dur = ((Float) p.get());
            }
            whiteout=true;
            blackoutBack = true;
            blackout(dur, 1);
        });
    }

    public void  draw(CustomSpriteBatch batch ) {
        batch.begin();
        blackSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blackSprite.resetColor(blackout);
        if (whiteout)
            batch.setBlending(GenericEnums.BLENDING.SCREEN);
        else
            batch.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        blackSprite.draw(batch);
        batch.resetBlending();
        batch.end();
    }

    public boolean isOpaque() {
        return blackout >= 0.999f;
    }

    public static class BlackSprite extends Sprite {
        public BlackSprite() {
            setRegion(TextureCache.getOrCreateR("ui/white.png"));
            setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        public void resetColor(float alpha) {
            setColor(1,1,1, alpha);
        }
    }



    public void blackout(float dur, float to) {
        blackout(dur, to, false);
    }

    public void blackout(float dur, float to, boolean back) {
        if (back)
            blackoutBack = back;
        if (dur>=1000) {
            dur = dur/1000;
        }
        main.system.auxiliary.log.LogMaster.devLog(toString() + " Blackout NEW to " + to);
        blackoutAction.setDuration(dur);
        if (!whiteout)
            blackoutAction.setInterpolation(Interpolation.fade);
        else
            blackoutAction.setInterpolation(Interpolation.sineIn);
        blackoutAction.setStart(blackout);
        blackoutAction.setEnd(to);
        blackoutAction.restart();
    }

    public void act(float delta) {
//       TODO  if (!BlackoutOld.isOnNewScreen())
//            if (isBlackoutIn()) {
//                blackout.fadeOutAndBack(2f);
//                setBlackoutIn(false);
//            }

        if (blackoutAction.getTime() >= blackoutAction.getDuration()) {
            if (blackoutBack) {
                main.system.auxiliary.log.LogMaster.devLog("Blackout new BACK;"  + " blackout=="+blackout);
                blackoutAction.setStart( (blackout));
                blackoutAction.setEnd(0);
                blackoutAction.restart();
                blackoutBack = false;
            }
        } else {
            blackoutAction.act(delta);
            blackout = blackoutAction.getValue();
            if (blackout > 0) {
//                main.system.auxiliary.log.LogMaster.dev("Blackout new drawn" + blackout + " whiteout=="+whiteout);
            }
        }


    }
}
