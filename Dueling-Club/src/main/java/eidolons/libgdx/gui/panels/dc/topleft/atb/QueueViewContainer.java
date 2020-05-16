package eidolons.libgdx.gui.panels.dc.topleft.atb;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.grid.cell.QueueView;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

import java.util.HashMap;
import java.util.Map;

class QueueViewContainer extends Container<QueueView> {
    private  Image intentIconBg;
    public int initiative;
    public float queuePriority; //same as ini?!
    public int id;
    public boolean mobilityState; //is moving now?
    public boolean mainHero;
    public boolean immobilized;

    SpriteX intentIconSprite;
    Map<INTENT_ICON, SpriteAnimation> iconMap = new HashMap<>();
    private INTENT_ICON intentIcon;

    public QueueViewContainer(QueueView actor) {
        super(actor);
        mainHero = actor.isMainHero();
        if (actor == null) {
            return;
        } //TODO ensure this never happens twice!
        actor.
                addActor(intentIconBg = new Image(TextureCache.getOrCreateR(Images.INTENT_ICON_BG)));
        intentIconBg.setPosition( (AtbPanel.imageSize-30)/2,  80);
        actor.
                addActor(intentIconSprite = new SpriteX());

        intentIconBg.setVisible(isIntentIconsOn());

//        intentIconSprite.setX(getWidth() / 2 - 14);
//        intentIconSprite.setFps(15);
//            shadow.addListener(new DynamicTooltip(() -> getIntentTooltip(actor.getUserObject())));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
//            IntentIconMaster
        if (isIntentIconsOn())
            if (getActor() != null) {
                    if (getActor().getUserObject() instanceof Unit) {
                    Unit unit = (Unit) getActor().getUserObject();
                    intentIcon =unit.getIntentIcon();
                    if (intentIcon == null) {
                        intentIcon = unit.getAI().getCombatAI().getIntentIcon();
                    }
                    if (intentIcon == null) {
                        intentIcon = INTENT_ICON.UNKNOWN;
                    }

                    SpriteAnimation sprite = iconMap.get(intentIcon);
                        if (sprite == null) {
                            iconMap.put(intentIcon,
                                    sprite = SpriteAnimationFactory.getSpriteAnimation(intentIcon.getPath(), false, false, false));
                        }
                    sprite.setBlending(intentIcon.blending);
                    intentIconSprite.setSprite(sprite);

                    intentIconSprite.setY(intentIconBg.getY()+ (intentIconBg.getY()-intentIconSprite.getHeight())/2);
                    intentIconSprite.setX(intentIconBg.getX()+ (intentIconSprite.getWidth()-intentIconBg.getWidth())/2);
//                    intentIconSprite.setX(getPrefWidth() / 2);

                    if (intentIcon == INTENT_ICON.UNKNOWN) {
                        intentIconSprite.setFps(10);
                    } else
                        intentIconSprite.setFps(15);

                    intentIconSprite.setZIndex(Integer.MAX_VALUE);
                }
            }

    }

    private boolean isIntentIconsOn() {
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram shader = batch.getShader();
        if (immobilized) {
            if (batch instanceof CustomSpriteBatch) {
                ((CustomSpriteBatch) batch).setFluctuatingShader(DarkShader.getInstance());
            }
        }
//            if (intentIconSprite != null) {
//                intentIconSprite.centerOnParent(getActor());
//                intentIconSprite.setOffsetY(intentIconSprite.getOffsetY()-500);
//                intentIconSprite.setOffsetX(intentIconSprite.getOffsetX());
//                intentIconSprite.draw(batch);
//            }
        super.draw(batch, parentAlpha);
        batch.setShader(shader);
    }
}
